package com.itdemo.gulimail.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.itdemo.common.valid.AddGroup;
import com.itdemo.common.valid.ListValue;
import com.itdemo.common.valid.UpdateGroup;
import com.itdemo.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 10:47:49
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@Null(message = "新增时品牌id不能为空",groups = {AddGroup.class})
	@NotNull(message = "修改时品牌id不能修改",groups = {UpdateGroup.class})
	@TableId
	private Long brandId;
	/**
	 * $column.comments
	 */
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * $column.comments
	 */
	@NotBlank
	@URL(message = "Logo不是一个合法的url地址",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * $column.comments
	 */
	private String descript;
	/**
	 * $column.comments
	 */
	@NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
	@ListValue(vals={0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * $column.comments
	 */
	@NotBlank(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "首字母必须是a-z或A-Z的一个字母",groups = {AddGroup.class,UpdateGroup.class})
	private String firstLetter;
	/**
	 * $column.comments
	 */
	@NotNull
	@Min(value = 0,message = "排序最小为0",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
