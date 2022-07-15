package com.chenjiayan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenjiayan.reggie.common.CustomException;
import com.chenjiayan.reggie.dto.SetmealDto;
import com.chenjiayan.reggie.entity.Category;
import com.chenjiayan.reggie.entity.Setmeal;
import com.chenjiayan.reggie.entity.SetmealDish;
import com.chenjiayan.reggie.mapper.SetMealMapper;
import com.chenjiayan.reggie.service.CategoryService;
import com.chenjiayan.reggie.service.SetMealDishService;
import com.chenjiayan.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private CategoryService categoryService;

    @Value("${reggie.imgPath}")
    private String BasePath;

    /**
     * 保存套餐及其套餐菜品
     * @param setMealDto
     * @return
     */
    @Override
    @Transactional
    public Boolean addSetMeal(SetmealDto setMealDto) {
        // 保存套餐信息
        this.save(setMealDto);
        Long setMealId = setMealDto.getId();
        List<SetmealDish> setmealDishes = setMealDto.getSetmealDishes();
        // 设置套餐菜品信息的套餐ID
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setMealId);
            return item;
        }).collect(Collectors.toList());
        // 保存套餐菜品
        boolean b = setMealDishService.saveBatch(setmealDishes);
        return b;
    }

    /**
     * 分页查询套餐及其套餐分类
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    @Transactional
    public Page<SetmealDto> pageDto(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        // 查询套餐信息
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name),Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);
        this.page(setmealPage,queryWrapper);

        // 查询套餐分类信息
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtos);

        return setmealDtoPage;
    }

    /**
     * 获取套餐信息（包含套餐菜品信息）
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealDto getOneById(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        Long setmealId = setmeal.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId)
                .orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> list = setMealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 更新套餐及其菜品
     * @param setmealDto
     * @return
     */
    @Override
    @Transactional
    public Boolean myUpdate(SetmealDto setmealDto) {
        // 思路：更新套餐，根据套餐id删除之前的菜品信息，在重新保存最新菜品信息
        // 删除旧照片
        Long setmealId = setmealDto.getId();
        Setmeal oldSetmeal = this.getOneById(setmealId);
        String oldImage = oldSetmeal.getImage();
        String image = setmealDto.getImage();
        if( StringUtils.isNotBlank(oldImage)&& !oldImage.equals(image)){
            File file = new File(BasePath + oldImage);
            if(file.exists()){
                file.delete();
            }
        }
        // 更新套餐信息
        this.updateById(setmealDto);
        // 删除旧菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setMealDishService.remove(queryWrapper);

        // 保存新菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        boolean b = setMealDishService.saveBatch(setmealDishes);
        return b;
    }

    /**
     * 删除套餐信息 包含（菜品、图片等）
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Boolean deleteAllMessage(List<Long> ids) {
        // 查询是否有售卖中的套餐
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,1);
        int count = this.count(setmealQueryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中，无法删除");
        }


        List<Setmeal> setmeals = this.listByIds(ids);
        setmeals.stream().map((item)->{
            // 删除照片
            String image = item.getImage();
            File file = new File(BasePath + image);
            if(file.exists()){
                file.delete();
            }
            return item;
        }).collect(Collectors.toList());
        // 删除套餐菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        boolean remove = setMealDishService.remove(queryWrapper);
        // 删除套餐信息
        boolean b = this.removeByIds(ids);
        return b&&remove;
    }
}
