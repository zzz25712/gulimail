package com.itdemo.gulimail.auth.feign;

import com.itdemo.common.utils.R;
import com.itdemo.gulimail.auth.vo.SocialUserVo;
import com.itdemo.gulimail.auth.vo.UserLoginVo;
import com.itdemo.gulimail.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimail-member")
public interface MemberFeign {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R oauthlogin(@RequestBody SocialUserVo vo) throws Exception;
}
