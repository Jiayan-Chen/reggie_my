package com.chenjiayan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenjiayan.reggie.common.CustomException;
import com.chenjiayan.reggie.entity.Category;
import com.chenjiayan.reggie.entity.Dish;
import com.chenjiayan.reggie.entity.Setmeal;
import com.chenjiayan.reggie.mapper.CategoryMapper;
import com.chenjiayan.reggie.service.CategoryService;
import com.chenjiayan.reggie.service.DishService;
import com.chenjiayan.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setmealService;
    /**
     * 根据id删除分类，并判断是否有关联菜品或套餐
     * @param id
     * @return
     */
    @Override
    public Boolean delete(Long id) {
        log.info(String.valueOf(id));
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishQueryWrapper);
        if(count>0){
            // 菜品有关联
            throw new CustomException("当前分类下有菜品正在售卖，无法删除！");
        }

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealQueryWrapper);
        if(count1>0){
            // 套餐有关联
            throw new CustomException("当前分类下有套餐正在售卖，无法删除！");
        }

        //删除
        boolean b = this.removeById(id);
        if(!b) return false;
        return true;
    }
}
