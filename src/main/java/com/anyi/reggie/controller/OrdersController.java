package com.anyi.reggie.controller;


import cn.hutool.db.sql.Order;
import com.anyi.reggie.common.BaseContext;
import com.anyi.reggie.common.R;
import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.dto.OrderDetailDto;
import com.anyi.reggie.dto.OrdersDto;
import com.anyi.reggie.entity.OrderDetail;
import com.anyi.reggie.entity.Orders;
import com.anyi.reggie.entity.ShoppingCart;
import com.anyi.reggie.service.OrderDetailService;
import com.anyi.reggie.service.OrdersService;
import com.anyi.reggie.service.ShoppingCartService;
import com.anyi.reggie.utils.ObjectConverter;
import com.anyi.reggie.utils.ObjectConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author
 * @since
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @Resource
    private OrderDetailService orderDetailService;

//    @Resource
//    private ShoppingCartService shoppingCartService;

    /**
     * 添加订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R addOrders(@RequestBody Orders orders,HttpServletRequest request){

        Long userId = (Long) request.getSession().getAttribute("user");
        orders.setUserId(userId);
        //ordersService.submit(orders);
        ordersService.addOrders(orders);
        return R.success("下单成功！");
    }

//    @PostMapping("/submit")
//    public R<String> submit(@RequestBody Orders orders,
//                            HttpServletRequest request) {
//        Long userId = (Long) request.getSession().getAttribute("user");
//        orders.setUserId(userId);
//        ordersService.submit(orders);
//        return R.success("下单成功");
//    }

    /**
     * 获取订单详情
     * @param page
     * @param pageSize
     * @return
     */
//    @GetMapping("/userPage")
//    public R getOrders(int page, int pageSize){
//        Page<OrdersDto> ordersDtoPage = ordersService.userPage(page,pageSize);
//        return R.success(ordersDtoPage);
//    }
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(@RequestParam(required = false, defaultValue = "1") int page,
                                       @RequestParam(required = false, defaultValue = "10") int pageSize,
                                       HttpServletRequest request) throws Exception {
        Long userId = (Long) request.getSession().getAttribute("user");
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> pageDto = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, queryWrapper);

        LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();

        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> orderDtoList = records.stream().map((item) ->{
            OrdersDto ordersDto = new OrdersDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            int orderId = item.getId();//获取订单id
            List<OrderDetail> orderDetailList = this.getOrderDetailListByOrderId(orderId);
            BeanUtils.copyProperties(item,ordersDto);
            //对orderDto进行OrderDetails属性的赋值
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());

        //使用dto的分页有点难度.....需要重点掌握
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }


    public List<OrderDetail> getOrderDetailListByOrderId(int orderId){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }

    @PutMapping
    public R<Orders> dispatch(@RequestBody Orders orders){
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(orders.getId()!=0,Orders::getId,orders.getId());
        Orders one = ordersService.getOne(queryWrapper);

        one.setStatus(orders.getStatus());
        ordersService.updateById(one);
        return R.success(one);
    }


    @GetMapping("/page")
    public R page(int page, int pageSize){
        Page<OrdersDto> ordersDtoPage = ordersService.page(page,pageSize);
        return R.success(ordersDtoPage);
    }

//    @PostMapping("/again")
//    public R<String> againSubmit(@RequestBody Map<String,String> map,HttpServletRequest request){
//        //获取再来一单的订单id
//        String ids = map.get("id");
//        long id = Long.parseLong(ids);
//
//        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(OrderDetail::getOrderId,id);
//        //获取所有该订单中的菜品
//        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
//
//        //通过用户id把原来的购物车给清空，这里的clean方法是视频中讲过的,建议抽取到service中,那么这里就可以直接调用了
//        shoppingCartService.clean();
//
//        //获取用户id
//        Long userId = (Long) request.getSession().getAttribute("user");
//        //因为菜品详细表和购物车表内容很像，所有容易相互赋值
//        List<ShoppingCart> shoppingCartList=orderDetailList.stream().map((item)->{
//            ShoppingCart shoppingCart=new ShoppingCart();
//            shoppingCart.setUserId(userId);
//            shoppingCart.setImage(item.getImage());
//            int dishId = item.getDishId();
//            int setmealId = item.getSetmealId();
//            if (dishId != 0) {
//                //如果是菜品那就添加菜品的查询条件
//                shoppingCart.setDishId(dishId);
//            } else {
//                //添加到购物车的是套餐
//                shoppingCart.setSetmealId(setmealId);
//            }
//            shoppingCart.setName(item.getName());
//            shoppingCart.setDishFlavor(item.getDishFlavor());
//            shoppingCart.setNumber(item.getNumber());
//            shoppingCart.setAmount(item.getAmount());
//            //shoppingCart.setCreateTime(LocalDateTime.now());
//
//            return shoppingCart;
//        }).collect(Collectors.toList());
//
//        //把携带数据的购物车批量插入购物车表  这个批量保存的方法要使用熟练！！！
//        shoppingCartService.saveBatch(shoppingCartList);
//
//        return R.success("操作成功");
//    }

}

