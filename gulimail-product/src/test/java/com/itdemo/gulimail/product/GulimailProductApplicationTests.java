package com.itdemo.gulimail.product;


import com.itdemo.gulimail.product.dao.AttrGroupDao;
import com.itdemo.gulimail.product.dao.SkuSaleAttrValueDao;
import com.itdemo.gulimail.product.service.BrandService;
import com.itdemo.gulimail.product.service.impl.CategoryServiceImpl;
import com.itdemo.gulimail.product.vo.SkuInfoSaleAttrVo;
import com.itdemo.gulimail.product.vo.SpuItemAttrgroupVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimailProductApplicationTests {

	@Autowired
	DataSource dataSource;

	@Autowired
	BrandService brandService;

	@Autowired
	StringRedisTemplate redisTemplate;

	@Autowired
	CategoryServiceImpl categoryService;

	@Autowired
	RedissonClient redissonClient;

	@Autowired
	AttrGroupDao attrGroupDao;

	@Autowired
	SkuSaleAttrValueDao skuSaleAttrValueDao;


	@Test
	public void contextLoads() {
		ValueOperations<String, String> value = redisTemplate.opsForValue();
		value.set("Hello","1"+ UUID.randomUUID().toString());
		System.out.println(value);
	}

	@Test
	public void contextLoads2() {
		System.out.println(redissonClient);
	}

	@Test
	public void test(){
		List<SpuItemAttrgroupVo> group = attrGroupDao.getAttrGroupWithAttrBySpuId(3l, 225l);
		System.out.println(group);
	}

	@Test
	public void test02(){
		List<SkuInfoSaleAttrVo> group = skuSaleAttrValueDao.getAttrBySpuid(2l);
		System.out.println(group);
	}
}
