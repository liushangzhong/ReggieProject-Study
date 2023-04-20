package com.anyi.reggie.service;

import com.anyi.reggie.dto.DishDto;
import com.anyi.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品相应的口味数据，需要操作两张表，dish,dish_flavor
    void addDish(DishDto dishDto);

    Page<DishDto> pageSearch(int page, int pageSize, String name);

    DishDto getDishById(int id);

    void updateDish(DishDto dishDto);

    void deleteDish(String  ids);

    List<DishDto> getList(Integer categoryId, Integer status);
}
