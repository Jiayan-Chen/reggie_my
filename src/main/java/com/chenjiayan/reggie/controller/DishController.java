package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.dto.DishDto;
import com.chenjiayan.reggie.entity.Dish;
import com.chenjiayan.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
@CacheConfig(cacheNames = "dishCache")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 添加菜品包含菜品口味
     * @param dishDto
     * @return
     */
    @PostMapping
    @CacheEvict(allEntries = true)
    public R<String> save(@RequestBody DishDto dishDto){
        Boolean b = dishService.add(dishDto);
        if(!b){
            return R.error("添加失败！");
        }
        return R.success("添加成功！");
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    @CacheEvict(allEntries = true)
    public R<String> update(@RequestBody DishDto dishDto){
        Boolean b = dishService.updateDto(dishDto);
        if(!b){
            return R.error("修改失败！");
        }
        return R.success("修改成功！");
    }

    /**
     * 查询菜品及其菜品口味信息
     * @param id
     * @return
     */
    @Cacheable(key = "#id")
    @GetMapping("/{id}")
    public R<DishDto> getOneById(@PathVariable Long id){
        DishDto dishDto = dishService.getDtoById(id);
        return R.success(dishDto);
    }

    /**
     * 分页查询菜品
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Cacheable(key = "#page+'_'+#pageSize+'_'+#name")
    @GetMapping("/page")
    public R<Page<DishDto>> pageR(int page,int pageSize,String name){
        log.warn("没有使用Cache!");
        Page<DishDto> dishDtoPage = dishService.pageDto(page, pageSize, name);
        return R.success(dishDtoPage);
    }




    /**
     * 菜品的启、停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(allEntries = true)
    public R<String> changeStatus(@PathVariable int status, @RequestParam List<Long> ids){
        //log.info("状态为：{},ID为：{}",status,ids);
        List<Dish> dishes = ids.stream().map((item)->{
            Dish dish = new Dish();
            dish.setId(item);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        boolean b = dishService.updateBatchById(dishes);
        if(!b) return R.error("状态修改失败！");
        return R.success("状态修改成功！");
    }

    /**
     * 删除菜品信息
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        Boolean b = dishService.deleteAllMessage(ids);
        if(!b) return R.error("删除失败！");
        return R.success("删除成功！");
    }

    /**
     * 根据分类id查询菜品列表
     * @param categoryId
     * @return
     */
    @Cacheable(key = "#categoryId")
    @GetMapping("/list")
    public R<List<DishDto>> listR(Long categoryId){
        List<DishDto> list = dishService.listDto(categoryId);
        // 进行过滤，只传在售的
        List<DishDto> list1 = list.stream().filter(item->item.getStatus()==1).collect(Collectors.toList());
        return R.success(list1);
    }

}
