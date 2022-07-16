package com.chenjiayan.reggie.dto;

import com.chenjiayan.reggie.entity.Setmeal;
import com.chenjiayan.reggie.entity.SetmealDish;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SetmealDto extends Setmeal implements Serializable {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
