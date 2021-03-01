package com.itdemo.gulimail.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itdemo.common.utils.HttpUtils;
import com.itdemo.gulimail.member.entity.MemberLevelEntity;
import com.itdemo.gulimail.member.exception.MobileeExsitException;
import com.itdemo.gulimail.member.exception.UsernameExsitException;
import com.itdemo.gulimail.member.service.MemberLevelService;
import com.itdemo.gulimail.member.vo.MemberLoginVo;
import com.itdemo.gulimail.member.vo.MemberRegistVo;
import com.itdemo.gulimail.member.vo.SocialUserVo;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.member.dao.MemberDao;
import com.itdemo.gulimail.member.entity.MemberEntity;
import com.itdemo.gulimail.member.service.MemberService;



@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity entity = new MemberEntity();
        //查询默认的会员等级
       MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
       entity.setLevelId(memberLevelEntity.getId());

        checkUsernameUnique(vo.getUserName());
        checkMobileUnique(vo.getPhone());

       entity.setUsername(vo.getUserName());
       entity.setMobile(vo.getPhone());
       entity.setNickname(vo.getUserName());

       //md5 盐值加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(password);

       baseMapper.insert(entity);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(entity!=null){
            String entityPassword = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, entityPassword);
            if(matches){
                return entity;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    @Override
    public MemberEntity login(SocialUserVo vo) throws Exception {
        String uid = vo.getUid();
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("uid", uid));
        if(entity != null){
            //已经注册过 只需更新令牌即可
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setRemindIn(vo.getRemind_in());
            baseMapper.updateById(memberEntity);

            entity.setAccessToken(vo.getAccess_token());
            entity.setRemindIn(vo.getRemind_in());
            return entity;
        }else{
            //注册信息
            MemberEntity entity1 = new MemberEntity();
            Map<String, String> header = new HashMap<>();
            Map<String, Object> query = new HashMap<>();
            query.put("access_token",vo.getAccess_token());
            query.put("uid",vo.getUid());
            String s = HttpUtils.getRequest("https://api.weibo.com/2/users/show.json", header, query);
            if(!StringUtils.isEmpty(s)){
                try{
                    JSONObject jsonObject = JSON.parseObject(s);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    entity1.setNickname(name);
                    entity1.setGender("m".equals(gender)?1:0);
                }catch (Exception e){}
                entity1.setAccessToken(vo.getAccess_token());
                entity1.setRemindIn(vo.getRemind_in());
                entity1.setUid(vo.getUid());
                baseMapper.insert(entity1);
            }
                return entity1;
        }
    }

    public void checkUsernameUnique(String username)throws UsernameExsitException{
        Integer integer = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if(integer > 0){
            throw new UsernameExsitException();
        }
    }

    public void checkMobileUnique(String phone)throws MobileeExsitException{
        Integer integer = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(integer > 0){
            throw new MobileeExsitException();
        }
    }

}