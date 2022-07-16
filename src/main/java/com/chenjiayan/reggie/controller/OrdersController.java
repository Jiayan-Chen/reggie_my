package com.chenjiayan.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.dto.OrdersDto;
import com.chenjiayan.reggie.entity.Orders;
import com.chenjiayan.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order")
@Slf4j
@CacheConfig(cacheNames = "orderCache")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 保存订单信息
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    @CacheEvict(allEntries = true)
    public R<String> submit(@RequestBody Orders orders){
        Boolean b = ordersService.submitOrder(orders);
        if(!b) return R.error("提交失败！");
        return R.success("提交成功！");
    }

    /**
     * 前台查询
     * @param page
     * @param pageSize
     * @return
     */
    @Cacheable(key = "#page+'_'+#pageSize")
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> pageR(int page,int pageSize){
        Page<OrdersDto> pageDto = ordersService.pageDto(page,pageSize);
        return R.success(pageDto);
    }

    /**
     * 再买一单
     * @param id
     * @return
     */
    @PostMapping("/again")
    public R<String> again(Long id){
        return R.success("s");
    }

    /**
     * 后台分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("page")
    @Cacheable(key = "#paeg+'_'+#pageSize+'_'+#number+'_'+#beginTime+'_'+#endTime")
    public R<Page<OrdersDto>> pageDto(int page, int pageSize, Long number, String beginTime, String endTime){
        Page<OrdersDto> pageAllDto = ordersService.pageAllDto(page,pageSize,number,beginTime,endTime);
        return R.success(pageAllDto);
    }

    /**
     * 修改订单信息
     * @param orders
     * @return
     */
    @PutMapping
    @CacheEvict(allEntries = true)
    public R<String> update(@RequestBody Orders orders){
        boolean b = ordersService.updateById(orders);
        if(!b) return R.error("操作失败！");
        return R.success("操作成功！");
    }
}
