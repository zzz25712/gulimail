package com.itdemo.gulimail.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.itdemo.common.constant.AuthServiceConstant;
import com.itdemo.common.exception.BizCodeEnum;
import com.itdemo.common.utils.R;
import com.itdemo.common.vo.MemberReponsVo;
import com.itdemo.gulimail.auth.feign.MemberFeign;
import com.itdemo.gulimail.auth.feign.ThirdPartyFeign;
import com.itdemo.gulimail.auth.vo.UserLoginVo;
import com.itdemo.gulimail.auth.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    ThirdPartyFeign thirdPartyFeign;

   @Autowired
   StringRedisTemplate redisTemplate;

   @Autowired
   MemberFeign memberFeign;

   @ResponseBody
   @GetMapping("/sms/sendcode")
   public R sendCode(@RequestParam("phone") String phone){
       String s = redisTemplate.opsForValue().get(AuthServiceConstant.SMS_CACHE_CODE_PREFIX + phone);

       if(!StringUtils.isEmpty(s)){
           long l = Long.parseLong(s.split("_")[1]);
           if(System.currentTimeMillis() - l < 60000){
               //60s内不需要再次发送
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
           }
       }
       String code = UUID.randomUUID().toString().substring(0,5)+"_"+System.currentTimeMillis();
       redisTemplate.opsForValue().set(AuthServiceConstant.SMS_CACHE_CODE_PREFIX+phone,code,10, TimeUnit.MINUTES);
       thirdPartyFeign.sendCode(phone,code.split("_")[0]);
       return R.ok();
   }

   @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result,
                         RedirectAttributes redirectAttributes,HttpSession session){
        if(result.hasErrors()){
            Map<String, String> map = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",map);
            return "redirect:http://auth.gulimail.com/reg.html";
        }
        //没错的话校验验证码
       String code = vo.getCode();
       String s = redisTemplate.opsForValue().get(AuthServiceConstant.SMS_CACHE_CODE_PREFIX + vo.getPhone());
       if(!StringUtils.isEmpty(s)){
            if(code.equals(s.split("_")[0])){
                //删除redis中的验证码
                redisTemplate.delete(AuthServiceConstant.SMS_CACHE_CODE_PREFIX + vo.getPhone());
                //注册成功 调用会员服务
                R r = memberFeign.regist(vo);
                if(r.getcode() == 0){
                    MemberReponsVo data = r.getData("data", new TypeReference<MemberReponsVo>() {
                    });
                    session.setAttribute(AuthServiceConstant.LOGIN_KEY,data);
                    return "redirect:http://auth.gulimail.com/login.html";
                }else{
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg",r.getData(new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.gulimail.com/reg.html";
                }
            }else{
                Map<String,String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.gulimail.com/reg.html";
            }
       }else{
        Map<String,String> errors = new HashMap<>();
        errors.put("code","请先获取验证码");
        redirectAttributes.addFlashAttribute("errors",errors);
        return "redirect:http://auth.gulimail.com/reg.html";
       }
   }

   @PostMapping("/login")
   public String login(UserLoginVo vo,HttpSession session){
        //检查登录信息
       R r = memberFeign.login(vo);
       //成功就放到session中
       if(r.getcode()==0){
           MemberReponsVo data = r.getData("data", new TypeReference<MemberReponsVo>() {
           });
           session.setAttribute(AuthServiceConstant.LOGIN_KEY,data);
           return "redirect:http://gulimail.com";
       }else{
           return "redirect:http://auth.gulimail.com/login.html";
       }
   }

   @RequestMapping("/login.html")
   public String loginpage(HttpSession session){
       if(session.getAttribute(AuthServiceConstant.LOGIN_KEY)!=null){
           return "redirect:http://gulimail.com";
       }else {
           return "login";
       }
   }


}
