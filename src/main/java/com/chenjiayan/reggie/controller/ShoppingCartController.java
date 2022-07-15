package com.chenjiayan.reggie.controller;

import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.entity.ShoppingCart;
import com.chenjiayan.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> listR(){
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }


}
