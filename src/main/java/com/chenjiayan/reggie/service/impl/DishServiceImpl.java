package com.chenjiayan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenjiayan.reggie.entity.Dish;
import com.chenjiayan.reggie.mapper.DishMapper;
import com.chenjiayan.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
