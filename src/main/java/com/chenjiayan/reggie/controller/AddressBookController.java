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
        boolean b = addressBookService.save(addressBook);
        if(!b) return R.error("添加地址失败！");
        return R.success("添加地址成功！");
    }
}
