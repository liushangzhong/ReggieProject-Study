package com.anyi.reggie.service.impl;

import com.anyi.reggie.entity.OrderDetail;
import com.anyi.reggie.mapper.OrderDetailMapper;
import com.anyi.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-25
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Override
    public List<OrderDetail> findByOrderId(int id) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, id);
        return this.list(queryWrapper);
    }
}
