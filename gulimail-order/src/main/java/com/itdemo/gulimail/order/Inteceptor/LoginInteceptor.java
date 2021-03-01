package com.itdemo.gulimail.order.Inteceptor;

import com.itdemo.common.constant.AuthServiceConstant;
import com.itdemo.common.vo.MemberReponsVo;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInteceptor implements HandlerInterceptor{
    public static ThreadLocal<MemberReponsVo> threadLocal= new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberReponsVo vo = (MemberReponsVo) request.getSession().getAttribute(AuthServiceConstant.LOGIN_KEY);

        //远程调用订单号查询订单信息 直接放行
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", uri);
        if(match){
            return true;
        }

        if(vo!=null){
            //登录
            threadLocal.set(vo);
            return true;
        }else {
            //未登录
            request.getSession().setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.gulimail.com/login.html");
            return false;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }
}
