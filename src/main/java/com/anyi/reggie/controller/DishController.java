package com.anyi.reggie.controller;


import com.anyi.reggie.common.R;
import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.dto.DishDto;
import com.anyi.reggie.entity.Dish;
import com.anyi.reggie.entity.DishFlavor;
import com.anyi.reggie.entity.Employee;
import com.anyi.reggie.service.CategoryService;
import com.anyi.reggie.service.DishFlavorService;
import com.anyi.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;
    @Resource
    private CategoryService categoryService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    private DishFlavorService dishFlavorService;


    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R addDish(@RequestBody DishDto dishDto){
        try {
            dishService.addDish(dishDto);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("新增菜品失败！");
        }
        return R.success("新增菜品成功！");
    }

    /**
     * 分页查询菜单
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R page(int page ,int pageSize , String name){
        Page<DishDto> pageInfo = dishService.pageSearch(page,pageSize,name);
        return R.success(pageInfo);
    }


    /**
     * 根据id回显菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getDishById(@PathVariable int id){
        DishDto dishDto = dishService.getDishById(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R update(@RequestBody DishDto dishDto){
        dishService.updateDish(dishDto);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("修改菜品成功！");
    }

    /**
     * 根据id删除信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R delete(String  ids){
        dishService.deleteDish(ids);
        return R.success("删除成功");
    }

    /**
     * 更改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R changeStatus(@PathVariable Integer status,String ids){
        String[] idList = ids.split(",");
        for (String id : idList) {
            Dish dish = new Dish();
            dish.setId(Integer.parseInt(id));
            dish.setStatus(status);
            dishService.updateById(dish);
        }

        return R.success("更新状态成功");
    }

    /**
     * 根据类别返回该类菜单
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R list(Integer categoryId,Integer status){
            List<DishDto> dishDtoList = dishService.getList(categoryId,status);
            return R.success(dishDtoList);
    }
}

