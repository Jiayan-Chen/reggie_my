package com.chenjiayan.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenjiayan.reggie.dto.DishDto;
import com.chenjiayan.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {

    /**
     * 保存菜品及菜品的口味信息
     * @param dishDto
     * @return
     */
    Boolean add(DishDto dishDto);

    /**
     * 分页查询菜品及其菜品分类名称
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<DishDto> pageDto(int page,int pageSize,String name);

    /**
     * 查询菜品及其菜品口味信息
     * @param id
     * @return
     */
    DishDto getDtoById(Long id);

    /**
     * 修改菜品信息（包含菜品口味）
     * @param dishDto
     * @return
     */
    Boolean updateDto(DishDto dishDto);

    /**
     * 删除菜品信息 包括（口味，图片等）
     * @param ids
     * @return
     */
    Boolean deleteAllMessage(List<Long> ids);
}
