package com.itdemo.gulimail.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class GuliFeignConfig {
    //在容器中添加feign调用时所使用的拦截器
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //通过RequestContextHolder拿到刚进来的请求
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
               if(requestAttributes!=null){
                   HttpServletRequest request = requestAttributes.getRequest();
                   if(request!=null){
                       //把老请求中请求头带的cookie信息放到新发送的请求中去
                       String header = request.getHeader("Cookie");
                       requestTemplate.header("Cookie",header);
                   }
               }
            }
        };
    }
}
