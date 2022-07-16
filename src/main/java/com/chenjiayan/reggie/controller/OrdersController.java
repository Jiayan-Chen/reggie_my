package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.dto.OrdersDto;
import com.chenjiayan.reggie.entity.Orders;
import com.chenjiayan.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 保存订单信息
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        Boolean b = ordersService.submitOrder(orders);
        if(!b) return R.error("提交失败！");
        return R.success("提交成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
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
}
