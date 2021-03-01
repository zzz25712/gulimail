package com.itdemo.gulimail.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * 订单项信息
 * 
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:00:15
 */
@ToString
@Data
@TableName("oms_order_item")
public class OrderItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@TableId
	private Long id;
	
	private Long orderId;
	
	private String orderSn;
	
	private Long spuId;
	
	private String spuName;
	
	private String spuPic;
	
	private String spuBrand;
	
	private Long categoryId;
	
	private Long skuId;
	
	private String skuName;
	
	private String skuPic;
	
	private BigDecimal skuPrice;
	
	private Integer skuQuantity;
	
	private String skuAttrsVals;
	
	private BigDecimal promotionAmount;
	
	private BigDecimal couponAmount;
	
	private BigDecimal integrationAmount;
	
	private BigDecimal realAmount;
	
	private Integer giftIntegration;
	
	private Integer giftGrowth;

}
