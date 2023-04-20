package com.anyi.reggie.service.impl;

import com.anyi.reggie.common.CustomException;
import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.dto.OrdersDto;
import com.anyi.reggie.entity.*;
import com.anyi.reggie.mapper.OrdersMapper;
import com.anyi.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-25
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private OrderDetailService orderDetailService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private UserService userService;

    /**
     * 添加订单
     * @param orders
     */
    @Override
    @Transactional
    public void addOrders(Orders orders) {
        //获得当前用户id
//        Long userId = UserContext.getUserId();
//        //Long uderId =  1;
//        //Long userId = (Long) request.getSession().getAttribute("user");
//
//        //查询当前用户的购物车数据
//        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(ShoppingCart::getUserId,userId);
//        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
//
//        if(shoppingCarts == null || shoppingCarts.size() == 0){
//            throw new CustomException("购物车为空，不能下单");
//        }
//
//        //查询用户数据
//        User user = userService.getById(userId);
//
//        //查询地址数据
//        int addressBookId = orders.getAddressBookId();
//        AddressBook addressBook = addressBookService.getById(addressBookId);
//        if(addressBook == null){
//            throw new CustomException("用户地址信息有误，不能下单");
//        }
        List<ShoppingCart> shoppingCartList = shoppingCartService.listByUserId(orders.getUserId());
        if (ObjectUtils.isEmpty(shoppingCartList)) {
            throw new CustomException("购物车为空，不能下单");
        }
        // 获取用户信息
        User user = userService.getById(orders.getUserId());
        // 获取 地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (ObjectUtils.isEmpty(addressBook)) {
            throw new CustomException("地址为空，不能下单");
        }

        int orderId = (int) IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId( orderId);
        orders.setOrderTime(new Date());
        orders.setCheckoutTime(new Date());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(user.getId());
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        //shoppingCartService.remove(wrapper);
        shoppingCartService.removeByIds(shoppingCartList.stream().map(ShoppingCart::getId).collect(Collectors.toList()));

    }

    /**
     *
     * @param page
     * @param pageSize
     * @return
     */


    @Override
    public Page<OrdersDto> page(int page, int pageSize) {
        Page<Orders> orders = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        Long userId = UserContext.getUserId();
        // 获取用户信息
        User user = userService.getById(userId);
        AddressBook address = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>()
                        .eq(AddressBook::getUserId, userId).
                        eq(AddressBook::getIsDefault, true));
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,userId);
        page(orders, wrapper);
        List<OrdersDto> records = orders.getRecords().stream().map(item->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            ordersDto.setUserName(user.getName());
            BeanUtils.copyProperties(address, ordersDto);
            List<OrderDetail> list = orderDetailService.list(
                    new LambdaQueryWrapper<OrderDetail>()
                            .eq(OrderDetail::getOrderId, item.getId()));
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(orders, ordersDtoPage);
        ordersDtoPage.setRecords(records);
        return ordersDtoPage;
    }


    @Override
    public Page<OrdersDto> userPage(int page, int pageSize) {
        Page<Orders> orders = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        Long userId = UserContext.getUserId();
        // 获取用户信息
        User user = userService.getById(userId);
        AddressBook address = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>()
                        .eq(AddressBook::getUserId, userId).
                        eq(AddressBook::getIsDefault, true));
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,userId);
        page(orders, wrapper);
        List<OrdersDto> records = orders.getRecords().stream().map(item->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            ordersDto.setUserName(user.getName());
            BeanUtils.copyProperties(address, ordersDto);
            List<OrderDetail> list = orderDetailService.list(
                    new LambdaQueryWrapper<OrderDetail>()
                            .eq(OrderDetail::getOrderId, item.getId()));
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(orders, ordersDtoPage);
        ordersDtoPage.setRecords(records);
        return ordersDtoPage;
    }

    @Override
    public void submit(Orders orders) {

        // 查询购物车信息
        List<ShoppingCart> shoppingCartList = shoppingCartService.listByUserId(orders.getUserId());
        if (ObjectUtils.isEmpty(shoppingCartList)) {
            throw new CustomException("购物车为空，不能下单");
        }
        // 获取用户信息
        User user = userService.getById(orders.getUserId());
        // 获取 地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (ObjectUtils.isEmpty(addressBook)) {
            throw new CustomException("地址为空，不能下单");
        }

        // 订单号
        int orderId = (int) IdWorker.getId();
        // 原子整数
        AtomicInteger amount = new AtomicInteger(0);
        // 计算总金额 获得订单明细
        List<OrderDetail> orderDetails = shoppingCartList.stream().map(item -> {
            // 累加金额
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            // 添加到总金额
            amount.getAndAdd(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 给订单表插入数据
        orders.setId(orderId);
        orders.setOrderTime(new Date());
        orders.setCheckoutTime(new Date());
        orders.setStatus(2); //订单状态
        orders.setAmount(new BigDecimal(amount.get())); // 金额
        orders.setUserId(user.getId());
        orders.setNumber(String.valueOf(orderId)); // 设置订单号
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(user.getEmail());
        orders.setAddress(addressBook.getDetail());
        this.save(orders);

        // 订单明细表插入数据
        orderDetailService.saveBatch(orderDetails);
        // 删除购物车中的数据
        shoppingCartService.removeByIds(shoppingCartList.stream().map(ShoppingCart::getId).collect(Collectors.toList()));

    }
}
