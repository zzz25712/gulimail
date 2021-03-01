package com.itdemo.gulimail.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itdemo.gulimail.product.service.CategoryBrandRelationService;
import com.itdemo.gulimail.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.product.dao.CategoryDao;
import com.itdemo.gulimail.product.entity.CategoryEntity;
import com.itdemo.gulimail.product.service.CategoryService;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listwithtree() {
        List<CategoryEntity> categorys = baseMapper.selectList(null);

        List<CategoryEntity> level1 = categorys.stream().filter(
                CategoryEntity -> CategoryEntity.getParentCid() == 0
        ).map((menu)->{
             menu.setChildren(getChildrens(menu,categorys));
             return menu;
        }).sorted((menu1,menu2)->{
           return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return level1;

    }

    @Override
    public void removeMenuByIds(List<Long> longs) {
        //TODO 删除前判断是否有引用
        baseMapper.deleteBatchIds(longs);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }


    /**
     * 更新级联属性
     * */
    @CacheEvict(value = "category",allEntries = true)//删除category分区下的所有数据
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Cacheable(value={"category"},key="#root.methodName")
    @Override
    public List<CategoryEntity> getLevel1Category() {
        List<CategoryEntity> list = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return list;
    }

    @Cacheable(value={"category"},key="#root.methodName")
    @Override
    public Map<String,List<Catelog2Vo>> getCatelogJson(){

        //改进 1.查出所有菜单 2.从查出的菜单中选取ParentCid为指定值的 (减少与数据库的交互)
        List<CategoryEntity> categorylist = baseMapper.selectList(null);

        //1、获取所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(categorylist,0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> catelog2VoListMap = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、根据每一个的一个分类，查到这个分类的二级分类
            List<CategoryEntity> level2Categorys = getParent_cid(categorylist,v.getCatId());
            //2、封装2级分类的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (level2Categorys != null) {
                catelog2Vos = level2Categorys.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Categorys = getParent_cid(categorylist,l2.getCatId());
                    if (level3Categorys != null) {
                        List<Catelog2Vo.Catelog3Vo> catelog3VoList = level3Categorys.stream().map(l3 -> {
                            //2、封装成指定的格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3VoList);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return catelog2VoListMap;
    }

    public Map<String, List<Catelog2Vo>> getCatelogJson2(){
        String catelogJson = redisTemplate.opsForValue().get("CatelogJson");
        if(StringUtils.isEmpty(catelogJson)){
            Map<String, List<Catelog2Vo>> result = getCatelogJsonFromDbWithRedisLock();
            String s = JSON.toJSONString(result);
            redisTemplate.opsForValue().set("CatelogJson",s);
            //查出来返回
            return result;
        }
        Map<String, List<Catelog2Vo>> listMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return listMap;
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedissonLock() {
        RLock lock = redissonClient.getLock("CatelogJson_lock");
        Map<String, List<Catelog2Vo>> data = null;
        lock.lock();
        try {
            data = getDataFromDb();
        }finally {
            lock.unlock();
        }
        return data;

    }
    //使用自己编写的分布式锁
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
        //使用uuid作为lock的值
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,300, TimeUnit.SECONDS);//redis中的setNx 设置300秒的过期时间
        if(lock){
            Map<String, List<Catelog2Vo>> data = null;
            try {
                //加锁成功 查询数据并释放锁
                data = getDataFromDb();
            }finally {
                //获取lock的key和删除锁必须是同时进行（原子操作） 使用lua脚本
                String script = "if (redis.call('GET', KEYS[1]) == ARGV[1]) then return redis.call('DEL',KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class),Arrays.asList("key"),uuid);
            }
            return data;
        }else {
            //加锁失败 进行自旋 重试
            return getCatelogJsonFromDbWithRedisLock();
        }

    }


    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        //拿到锁先看缓存是否有数据 无则查询数据库
        String catelogJson = redisTemplate.opsForValue().get("CatelogJson");
        if(!StringUtils.isEmpty(catelogJson)){
            Map<String, List<Catelog2Vo>> listMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return listMap;
        }
        Map<String, List<Catelog2Vo>> catelogJson2 = getCatelogJson();
        //查出来先放到缓存 防止二次查询
        String s = JSON.toJSONString(catelogJson2);
        redisTemplate.opsForValue().set("CatelogJson",s);

        return catelogJson2;
    }


    //从数据库中查出CatelogJson --本地锁
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
        synchronized (this){
        //拿到锁先看缓存是否有数据 无则查询数据库
            return getDataFromDb();
       }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> categorylist,Long parent_cid) {
       return categorylist.stream().filter(item->item.getParentCid() == parent_cid).collect(Collectors.toList());
    }

    //递归查找所有路径
    public List<Long> findParentPath(Long catelogId,List<Long> paths){
        paths.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if(category.getParentCid() != 0){
            findParentPath(category.getParentCid(),paths);
        }
        return paths;
    }

    public List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map((menu) -> {
            menu.setChildren(getChildrens(menu, all));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

}