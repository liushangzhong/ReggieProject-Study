package com.anyi.reggie.service.impl;

import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.entity.AddressBook;
import com.anyi.reggie.entity.ShoppingCart;
import com.anyi.reggie.mapper.AddressBookMapper;
import com.anyi.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 地址管理 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-25
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Resource
    private AddressBookMapper addressBookMapper;

    /**
     * 设置默认地址
     * @param
     */
//    @Override
//    public void setDefault(AddressBook addressBook) {
//        // 首相把所有地址的 isDefault设置为 0
//        Long userId = UserContext.getUserId();
//        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(AddressBook::getUserId,userId);
//        AddressBook ad = new AddressBook();
//        ad.setIsDefault(false);
//        update(ad, wrapper);
//        // 把当前id地址设置为1
//        addressBook.setIsDefault(true);
//        updateById(addressBook);
//    }


    @Override
    public void setDefault(int id) {
        // 修改原来的默认地址
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getIsDefault, 1)
                .set(AddressBook::getIsDefault, 0);
        addressBookMapper.update(null, updateWrapper);
        // 修改 id 为默认地址
        LambdaUpdateWrapper<AddressBook> updateDefaultWrapper = new LambdaUpdateWrapper<>();
        updateDefaultWrapper.eq(AddressBook::getId, id)
                .set(AddressBook::getIsDefault, 1);
        addressBookMapper.update(null, updateDefaultWrapper);
    }
}
