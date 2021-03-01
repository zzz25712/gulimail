package com.itdemo.gulimail.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.itdemo.common.exception.BizCodeEnum;
import com.itdemo.gulimail.member.vo.MemberLoginVo;
import com.itdemo.gulimail.member.vo.MemberRegistVo;
import com.itdemo.gulimail.member.vo.SocialUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.itdemo.gulimail.member.entity.MemberEntity;
import com.itdemo.gulimail.member.service.MemberService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.R;



/**
 * 会员
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:07:39
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;



    @RequestMapping(value = "/coupon",method = RequestMethod.GET)
    public R Feigntest(){
        MemberEntity member = new MemberEntity();
        member.setNickname("zhangsan");

        return R.ok().put("member",member);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 注册
     * */
    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo vo){
        try {
            memberService.regist(vo);
        }catch (Exception e){

        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){
        MemberEntity memberEntity = memberService.login(vo);
        if(memberEntity!=null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCodeEnum.LOGIN_PASSWORD_VALIDE_EXCEPTION.getCode(),BizCodeEnum.LOGIN_PASSWORD_VALIDE_EXCEPTION.getMessage());
        }

    }

    @PostMapping("/oauth2/login")
    public R oauthlogin(@RequestBody SocialUserVo vo) throws Exception {
        MemberEntity memberEntity = memberService.login(vo);
        if(memberEntity!=null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCodeEnum.LOGIN_PASSWORD_VALIDE_EXCEPTION.getCode(),BizCodeEnum.LOGIN_PASSWORD_VALIDE_EXCEPTION.getMessage());
        }

    }
}
