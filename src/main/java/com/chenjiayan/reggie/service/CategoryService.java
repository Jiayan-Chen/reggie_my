package com.chenjiayan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenjiayan.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 根据id删除分类，并判断是否有关联菜品或套餐
     * @param id
     * @return
     */
    Boolean delete(Long id);
}
