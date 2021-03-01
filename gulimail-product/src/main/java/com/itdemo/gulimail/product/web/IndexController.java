package com.itdemo.gulimail.product.web;

import com.itdemo.gulimail.product.entity.CategoryEntity;
import com.itdemo.gulimail.product.service.CategoryService;
import com.itdemo.gulimail.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/","/index.html"})
    public String getLevel1Category(Model model){
        List<CategoryEntity> list = categoryService.getLevel1Category();
        model.addAttribute("list",list);
        return "index";
    }

    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/write")
    public String write(){
        String s = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rewrite_lock");
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        System.out.println("写锁 锁");
        try {
            s = UUID.randomUUID().toString();
            Thread.sleep(10000);
            redisTemplate.opsForValue().set("WriteValue",s);

        }catch (Exception e){

        }finally {
            rLock.unlock();
            System.out.println("写锁 放");
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String read(){
        String s = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rewrite_lock");
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        System.out.println("读锁 锁");
        try {
            Thread.sleep(10000);
            s = redisTemplate.opsForValue().get("WriteValue");
        }catch (Exception e) {

        }finally {
            rLock.unlock();
            System.out.println("读锁 放");
        }
        return s;
    }

    //信号量锁
    @ResponseBody
    @GetMapping("/park")
    public String park() {
        RSemaphore sigleValue = redissonClient.getSemaphore("sigleValue");
        String s = redisTemplate.opsForValue().get("sigleValue");
        try {
            sigleValue.acquire();//拿
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "park:"+s;
    }

    @ResponseBody
    @GetMapping("/go")
    public String go() {
        RSemaphore sigleValue = redissonClient.getSemaphore("sigleValue");
        sigleValue.release();//释放
        return "go";
    }

    //闭锁
    @ResponseBody
    @GetMapping("/lockdoor")
    public String lockdoor() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
        latch.trySetCount(3);
        try {
            latch.await();
        } catch (InterruptedException e) {

        }
        return "lockdoor";
    }

    @ResponseBody
    @GetMapping("/leave")
    public String leave() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
        String s = redisTemplate.opsForValue().get("anyCountDownLatch");
        try {
            latch.countDown();
        } catch (Exception e) {

        }
        if(!StringUtils.isEmpty(s)){
            return "第"+s+"个人离开";
        }else {
            return "没人了";
        }
    }
}
