package com.itdemo.gulimail.member;


import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class GulimailMemberApplicationTests {

	@Test
	public void contextLoads() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		System.out.println(passwordEncoder.matches("123456", "$2a$10$S.3O0EAlciQxQS/wgfZvAuXW4fxZQ9eYSPc/VZ2EWWOiuB4tbKcPC"));
	}

}
