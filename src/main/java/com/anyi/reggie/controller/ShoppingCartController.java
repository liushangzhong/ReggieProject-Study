package com.anyi.reggie.controller;


import com.anyi.reggie.common.R;
import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.common.BaseContext;
import com.anyi.reggie.entity.ShoppingCart;
import com.anyi.reggie.mapper.ShoppingCartMapper;
import com.anyi.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 查询购物车所有商品
     *
     * @return
     */
//    @GetMapping("/list")
//    public R getList(){
//        List<ShoppingCart> list = shoppingCartService.getList();
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId)
                .orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 添加购物车项
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R add(@RequestBody ShoppingCart shoppingCart) {

        shoppingCartService.add(shoppingCart);
        return R.success("添加成功！");
    }

    @PostMapping("/sub")
    public R sub(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.sub(shoppingCart);
        return R.success("取消成功");
    }

    /**
     * @param request
     * @return
     */

    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public R<String> clean(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物成功");
    }
}