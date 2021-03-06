package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.entity.Category;
import com.chenjiayan.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
@Slf4j
@CacheConfig(cacheNames = "category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param category
     * @return
     */
    @PostMapping
    @CacheEvict(allEntries = true)
    public R<String> add(@RequestBody Category category){
        boolean save = categoryService.save(category);
        if(!save){
            return R.error("添加失败！");
        }
        return R.success("添加成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    @Cacheable(key = "#page+'_'+#pageSize")
    public R<Page<Category>> getR(int page,int pageSize){
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 设置排序顺序
        queryWrapper.orderByAsc(Category::getType)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        categoryService.page(categoryPage,queryWrapper);
        return R.success(categoryPage);
    }

    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    @CacheEvict(allEntries = true)
    public R<String> update(@RequestBody Category category){
        boolean b = categoryService.updateById(category);
        if(!b){
            return R.error("修改失败！");
        }
        return R.success("修改成功！");
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(allEntries = true)
    public R<String> delete(Long ids){
        Boolean b = categoryService.delete(ids);
        if(!b){
            return R.error("删除失败!");
        }
        return R.success("删除成功！");
    }

    /**
     * 通过类型获取该类的列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    @Cacheable(keyGenerator = "myKeyGenerator")
    public R<List<Category>> listR(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getType);
        queryWrapper.orderByAsc(Category::getSort);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
