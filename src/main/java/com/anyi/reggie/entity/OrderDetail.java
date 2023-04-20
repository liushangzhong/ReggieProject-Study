package com.anyi.reggie.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单明细表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderDetail对象", description="订单明细表")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private int id;

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "订单id")
    private Integer orderId;

    @ApiModelProperty(value = "菜品id")
    private Integer dishId;

    @ApiModelProperty(value = "套餐id")
    private Integer setmealId;

    @ApiModelProperty(value = "口味")
    private String dishFlavor;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;


}
