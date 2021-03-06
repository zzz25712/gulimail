package com.itdemo.gulimail.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 退货原因
 * 
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:00:15
 */
@Data
@TableName("oms_order_return_reason")
public class OrderReturnReasonEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId
	private Long id;
	/**
	 * $column.comments
	 */
	private String name;
	/**
	 * $column.comments
	 */
	private Integer sort;
	/**
	 * $column.comments
	 */
	private Integer status;
	/**
	 * $column.comments
	 */
	private Date createTime;

}
