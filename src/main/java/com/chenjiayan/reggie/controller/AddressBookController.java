package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenjiayan.reggie.common.BaseContext;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.entity.AddressBook;
import com.chenjiayan.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 获取用户的收货地址列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> listR(){
        // 获取用户id
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId)
                        .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 根据id查询收货地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getAddressBook(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook==null) R.error("请求错误！");
        return R.success(addressBook);
    }

    /**
     * 修改收货地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        boolean b = addressBookService.updateById(addressBook);
        if(!b) return R.error("修改地址成功!");
        return R.success("修改地址失败！");
    }

    /**
     * 添加收货地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        boolean b = addressBookService.save(addressBook);
        if(!b) return R.error("添加地址失败！");
        return R.success("添加地址成功！");
    }

    /**
     * 根据id删除收货地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestBody Long ids){
        boolean b = addressBookService.removeById(ids);
        if(!b) return R.error("删除失败！");
        return R.success("删除成功！");
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId)
                .eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }

    /**
     * 设置为默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        // 将用户的所以地址的isDefault设置为0
        Long  userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        AddressBook addressBook1 = new AddressBook();
        addressBook1.setIsDefault(0);
        addressBookService.update(addressBook1,queryWrapper);
        // 再根据前端传过来的id设置默认收货地址
        addressBook.setIsDefault(1);
        boolean b = addressBookService.updateById(addressBook);
        if(!b) return R.error("设置失败！");
        return R.success("设置成功！");
    }
}
