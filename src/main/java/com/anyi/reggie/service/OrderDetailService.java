package com.anyi.reggie.service;

import com.anyi.reggie.entity.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 订单明细表 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-05-25
 */
public interface OrderDetailService extends IService<OrderDetail> {
    List<OrderDetail> findByOrderId(int id);
}
