package com.itdemo.gulimail.cart.config;


import com.itdemo.common.constant.AuthServiceConstant;
import com.itdemo.common.constant.CartConstant;
import com.itdemo.common.vo.MemberReponsVo;
import com.itdemo.gulimail.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Component
public class CartIntercepter implements HandlerInterceptor{
    //线程内部存储类 同一个线程可以访问的公共资源
    public static ThreadLocal<UserInfoTo> threadLocal= new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        //通过session获取登陆信息
        MemberReponsVo memberReponsVo = (MemberReponsVo) session.getAttribute(AuthServiceConstant.LOGIN_KEY);
        if(memberReponsVo!=null){
            //如果登录信息不为空 就设置用户id
            userInfoTo.setUserId(memberReponsVo.getId());
        }

        //拿到所有cookie 找到name为“user-key”的cookie 并把value赋值给to的key
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(CartConstant.Temp_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                }
            }
            //如果没有user-key的cookie 就创建一个
            if(StringUtils.isEmpty(userInfoTo.getUserKey())){
                String s = UUID.randomUUID().toString();
                userInfoTo.setUserKey(s);
                userInfoTo.setTempUser(true);
            }
        }

        threadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if(userInfoTo.getTempUser()){
            Cookie cookie = new Cookie(CartConstant.Temp_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setMaxAge(CartConstant.Temp_USER_COOKIE_TIME);
            cookie.setDomain("gulimail.com");
            response.addCookie(cookie);
        }
    }
}
