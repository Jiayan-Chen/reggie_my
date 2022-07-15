package com.chenjiayan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenjiayan.reggie.common.CustomException;
import com.chenjiayan.reggie.dto.DishDto;
import com.chenjiayan.reggie.entity.Category;
import com.chenjiayan.reggie.entity.Dish;
import com.chenjiayan.reggie.entity.DishFlavor;
import com.chenjiayan.reggie.mapper.DishMapper;
import com.chenjiayan.reggie.service.CategoryService;
import com.chenjiayan.reggie.service.DishFlavorService;
import com.chenjiayan.reggie.service.DishService;
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
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Value("${reggie.imgPath}")
    private String BasePath;
    /**
     * 保存菜品及菜品的口味信息
     * @param dishDto
     * @return
     */
    @Override
    @Transactional
    public Boolean add(DishDto dishDto) {
        // 保存菜品
        this.save(dishDto);
        // 菜品id
        Long dishId = dishDto.getId();
        // 设置菜品口味的菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        // 保存菜品口味
        boolean b = dishFlavorService.saveBatch(flavors);
        return b;
    }

    /**
     * 分页查询菜品及其菜品分类名称
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<DishDto> pageDto(int page, int pageSize, String name) {
        // 查询dish的相关信息
        Page<Dish> dishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);
        this.page(dishPage,dishQueryWrapper);

        // 根据dishId查询菜品分类，然后封装到DishDto中
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> dtoList = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dtoList);
        return dishDtoPage;
    }

    /**
     * 查询菜品及其菜品口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getDtoById(Long id) {
        // 查询菜品
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        // 查询菜品口味
        Long dishId = dish.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 修改菜品信息（包含菜品口味）
     * @param dishDto
     * @return
     */
    @Override
    @Transactional
    public Boolean updateDto(DishDto dishDto) {
        // 思路：更新菜品，根据菜品id删除之前的口味信息，在重新保存最新口味信息
        // 如果图片换了需要删除
        Dish oldDish = this.getById(dishDto.getId());
        String image = oldDish.getImage();
        if(StringUtils.isNotBlank(image) && (!image.equals(dishDto.getImage()))){
            File file = new File(BasePath + image);
            if(file.exists()){
                file.delete();
                log.info("旧图片已删除！");
            }
        }
        this.updateById(dishDto);
        Long dishId = dishDto.getId();
        //删除口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(dishFlavorQueryWrapper);
        //保存口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        boolean b = dishFlavorService.saveBatch(flavors);
        return b;
    }

    /**
     * 删除菜品信息 包括（口味，图片等）
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Boolean deleteAllMessage(List<Long> ids) {
        // 查询是否有售卖中的菜品
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(Dish::getId,ids)
                .eq(Dish::getStatus,1);
        int count = this.count(dishQueryWrapper);
        if(count>0){
            throw new CustomException("当前菜品正在售卖，无法删除！");
        }

        List<Dish> dishes = this.listByIds(ids);
        dishes.stream().map((item)->{
            // 删除照片信息
            String image = item.getImage();
            File file = new File(BasePath + image);
            if(file.exists()){
                file.delete();
                log.info("旧图片已删除！");
            }

            return item;
        }).collect(Collectors.toList());
        // 删除口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        // 删除菜品信息
        boolean b = this.removeByIds(ids);
        return b;
    }

    @Override
    public List<DishDto> listDto(Long categoryId) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> list = this.list(dishQueryWrapper);
        List<DishDto> listDto=list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> list1 = dishFlavorService.list(dishFlavorQueryWrapper);
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        return listDto;
    }


}
