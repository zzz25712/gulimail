package com.itdemo.gulimail.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itdemo.common.constant.AuthServiceConstant;
import com.itdemo.common.utils.HttpUtils;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.auth.feign.MemberFeign;
import com.itdemo.common.vo.MemberReponsVo;
import com.itdemo.gulimail.auth.vo.SocialUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class Aouth2Controller {

    @Autowired
    MemberFeign memberFeign;

    @GetMapping("/oauth2/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        Map<String, String> header = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("client_id","3662915591");
        map.put("client_secret","e909764fe2f5832a458d14ee8ae4c0a6");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimail.com/oauth2/weibo/success");
        map.put("code",code);
        String s1 = HttpUtils.postRequest("https://api.weibo.com/oauth2/access_token", header, map);

        if(!StringUtils.isEmpty(s1)){
            SocialUserVo vo = JSON.parseObject(s1, SocialUserVo.class);
            R r = memberFeign.oauthlogin(vo);
            if(r.getcode() == 0){
                MemberReponsVo data = r.getData("data", new TypeReference<MemberReponsVo>() {
                });
                log.info("登陆信息为：{}"+data);
                session.setAttribute(AuthServiceConstant.LOGIN_KEY,data);
                return "redirect:http://gulimail.com";
            }else{
                return "redirect:http://auth.gulimail.com/login.html";
            }
        }else{
            return "redirect:http://auth.gulimail.com/login.html";
        }



    }
}
