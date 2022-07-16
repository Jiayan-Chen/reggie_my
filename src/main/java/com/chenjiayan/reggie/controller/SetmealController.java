package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.dto.SetmealDto;
import com.chenjiayan.reggie.entity.Setmeal;
import com.chenjiayan.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
@CacheConfig(cacheNames = "setmealCache")
public class SetmealController {
    @Autowired
    private SetMealService setmealService;

    /**
     * 保存套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(allEntries = true)
    public R<String> add(@RequestBody SetmealDto setmealDto){
        Boolean b = setmealService.addSetMeal(setmealDto);
        if(!b){
            return R.error("套餐添加失败！");
        }
        return R.success("套餐添加成功！");
    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @Cacheable(key = "#page+'_'+#pageSize+'_'+#name")
    public R<Page<SetmealDto>> pageR(int page,int pageSize,String name){
        log.warn("没有使用Cache!");
        Page<SetmealDto>  pageDto = setmealService.pageDto(page,pageSize,name);
        return R.success(pageDto);
    }

    /**
     * 根据ID获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Cacheable(key = "#id")
    public R<SetmealDto> getOne(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getOneById(id);
        if(setmealDto == null) R.error("请求失败！");
        return R.success(setmealDto);
    }

    /**
     * 更新套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        Boolean b = setmealService.myUpdate(setmealDto);
        if(!b) return R.error("修改套餐失败！");
        return R.success("修改套餐成功！");
    }

    /**
     * 该变套餐的状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(allEntries = true)
    public R<String> status(@PathVariable int status,@RequestParam List<Long> ids){
        List<Setmeal> setmeals = ids.stream().map((item)->{
            Setmeal setmeal = new Setmeal();
            setmeal.setStatus(status);
            setmeal.setId(item);
            return setmeal;
        }).collect(Collectors.toList());
        boolean b = setmealService.updateBatchById(setmeals);
        if(!b) return R.error("状态修改失败！");
        return R.success("状态修改成功！");
    }

    /**
     * 删除套餐信息
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        Boolean b = setmealService.deleteAllMessage(ids);
        if(!b) return R.error("删除套餐失败！");
        return R.success("删除套餐成功！");
    }

    /**
     * 查询分类下的菜品信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(key = "#setmeal.getCategoryId()")
    public R<List<Setmeal>> listR(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId())
                        .eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus())
                        .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
