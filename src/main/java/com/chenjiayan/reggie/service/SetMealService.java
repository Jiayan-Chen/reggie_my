package com.chenjiayan.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenjiayan.reggie.dto.SetmealDto;
import com.chenjiayan.reggie.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    /**
     * 保存套餐及其套餐菜品
     * @param setMealDto
     * @return
     */
    Boolean addSetMeal(SetmealDto setMealDto);

    /**
     * 分页查询套餐及其套餐分类
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<SetmealDto> pageDto(int page, int pageSize, String name);

    /**
     * 获取套餐信息（包含套餐菜品信息）
     * @param id
     * @return
     */
    SetmealDto getOneById(Long id);

    /**
     * 更新套餐及其菜品
     * @param setmealDto
     * @return
     */
    Boolean myUpdate(SetmealDto setmealDto);

    /**
     * 删除套餐信息 包含（菜品、图片等）
     * @param ids
     * @return
     */
    Boolean deleteAllMessage(List<Long> ids);

}
