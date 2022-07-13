package com.chenjiayan.reggie.dto;

import com.chenjiayan.reggie.entity.Setmeal;
import com.chenjiayan.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
