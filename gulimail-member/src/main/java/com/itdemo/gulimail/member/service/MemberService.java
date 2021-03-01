package com.itdemo.gulimail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.member.entity.MemberEntity;
import com.itdemo.gulimail.member.vo.MemberLoginVo;
import com.itdemo.gulimail.member.vo.MemberRegistVo;
import com.itdemo.gulimail.member.vo.SocialUserVo;

import java.util.Map;

/**
 * 会员
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:07:39
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUserVo vo) throws Exception;
}

