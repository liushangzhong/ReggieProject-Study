package com.anyi.reggie.controller;


import com.anyi.reggie.common.CommentRedis;
import com.anyi.reggie.common.R;
import com.anyi.reggie.entity.Category;
import com.anyi.reggie.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author
 * @since
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private RedisTemplate<String, List<Category>> redisTemplate;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort");
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 添加分类
     * @param category
     * @return
     */
/*    @PostMapping
    public R addCate(@RequestBody Category category){
        try {
            categoryService.save(category);
        } catch (Exception e) {
            e.printStackTrace();
            return R.success("添加分类成功");
        }
        return R.success("添加分类成功");
    }*/


    @PostMapping
    public R addCate(@RequestBody Category category){
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        try {
            categoryService.save(category);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("添加分类失败");
        }
        return R.success("添加分类成功");
    }
    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids){

        categoryService.delete(ids);
        return R.success("删除成功");

    }
    /**
     * 更新分类信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("更新分类信息成功");
    }

    /**
     * 获取所有菜品分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Integer type){
        String key = CommentRedis.CATEGORY_PREFIX + type;
        List<Category> categories = redisTemplate.opsForValue().get(key);
        if (categories !=null){
            return R.success(categories);
        }
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        if(type != null ){
            wrapper.eq("type", type);
        }
        List<Category> list = categoryService.list(wrapper);
        redisTemplate.opsForValue().set(key, list,30, TimeUnit.MINUTES);
        return R.success(list);
    }
}