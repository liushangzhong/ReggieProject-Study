package com.anyi.reggie.controller;


import com.anyi.reggie.common.R;
import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.entity.AddressBook;
import com.anyi.reggie.entity.ShoppingCart;
import com.anyi.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 地址管理 前端控制器
 * </p>
 *
 * @author
 * @since
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 添加地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R add(@RequestBody AddressBook addressBook){
        Long userId = UserContext.getUserId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加地址成功！");
    }

    /**
     * 查询当前用户所有地址
     * @return
     */
//    @GetMapping("/list")
//    public R getList(){
//        Long userId = UserContext.getUserId();
//        List<AddressBook> list = addressBookService.list(new LambdaQueryWrapper<AddressBook>()
//                .orderByDesc(AddressBook::getIsDefault)
//                .eq(AddressBook::getUserId,userId));
//        return R.success(list);
//    }

    /**
     *
     * @param request
     * @return
     */

    @ApiOperation("获取地址列表")
    @GetMapping("/list")
    public R<List<AddressBook>> list( HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( AddressBook::getUserId, userId)
                .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 设置默认地址
     * @param
     * @return
     */
//    @PutMapping("/default")
//    public R changeDefault(@RequestBody AddressBook addressBook){
//        addressBookService.setDefault(addressBook);
//        return R.success("默认地址设置成功!");
//    }

    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody Map<String, Integer> map) {
        int id = map.get("id");
        if (ObjectUtils.isEmpty(id)) {
            return R.error("缺少关键参数");
        }
        addressBookService.setDefault(id);
        return R.success("修改成功");
    }


    /**
     * 根据id获取地址
     * @param id
     * @return
     */
//    @GetMapping("/{id}")
//    public R getAddress(@PathVariable Integer id){
//        AddressBook addressBook = addressBookService.getById(id);
//        return R.success(addressBook);
//    }


    @ApiOperation("获取指定地址")
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable("id") int id) {
        return R.success(addressBookService.getById(id));
    }

    @ApiOperation("修改地址信息")
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    @ApiOperation("删除地址")
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        addressBookService.removeByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 获取默认地址
     * @return
     */
//    @GetMapping("/default")
//    public R getDefault(){
//        Long userId = UserContext.getUserId();
//        AddressBook one = addressBookService.getOne(
//                new LambdaQueryWrapper<AddressBook>()
//                        .eq(AddressBook::getIsDefault, true)
//                        .eq(AddressBook::getUserId,userId));
//        return R.success(one);
//    }

    @ApiOperation("获取默认地址")
    @GetMapping("/default")
    public R<Object> getDefault(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault, 1)
                .eq(AddressBook::getUserId, userId);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(addressBook)) {
            return R.error("不存在默认地址");
        }
        return R.success(addressBook);
    }

}

