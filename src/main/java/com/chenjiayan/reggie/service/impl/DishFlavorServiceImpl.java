package com.chenjiayan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenjiayan.reggie.entity.DishFlavor;
import com.chenjiayan.reggie.mapper.DishFlavorMapper;
import com.chenjiayan.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
