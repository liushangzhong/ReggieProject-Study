package com.anyi.reggie.dto;


import com.anyi.reggie.entity.OrderDetail;
import com.anyi.reggie.entity.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;
	
}
