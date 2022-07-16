package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenjiayan.reggie.common.BaseContext;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.entity.ShoppingCart;
import com.chenjiayan.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
@CacheConfig(cacheNames = "shoppingCartCache")
public class ShoppingCartController implements Serializable {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/list")
    @Cacheable(keyGenerator = "myKeyGenerator")
    public R<List<ShoppingCart>> listR(){
        log.info("没有使用redis");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 购物车中添加菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    @CacheEvict(allEntries = true)
    public R<ShoppingCart> addCart(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getId()!=null,ShoppingCart::getId,userId)
                .eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId())
                .eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        int number = 1;
        if(shoppingCart1!=null){
            shoppingCart.setId(shoppingCart1.getId());
            number = shoppingCart1.getNumber()+1;
            shoppingCart1.setNumber(number);
            shoppingCart1.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(shoppingCart1);
        }else{
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        shoppingCart.setNumber(number);
        return R.success(shoppingCart);
    }

    /**
     * 购物车中减少菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    @CacheEvict(allEntries = true)
    public R<ShoppingCart> updateCart(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getId()!=null,ShoppingCart::getId,userId)
                .eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId())
                .eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        Integer number = shoppingCart1.getNumber();
        shoppingCart.setNumber(number-1);
        if(number==1){
            // 如果number等于1，直接删除
            shoppingCartService.removeById(shoppingCart1.getId());
        }else{
            shoppingCart1.setNumber(number-1);
            shoppingCart1.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(shoppingCart1);
        }
        return R.success(shoppingCart);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @CacheEvict(allEntries = true)
    public R<String> clean(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        boolean b = shoppingCartService.remove(queryWrapper);
        if(!b) return R.error("清空失败！");
        return R.success("清空成功！");
    }
}
