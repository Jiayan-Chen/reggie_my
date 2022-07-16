package com.chenjiayan.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenjiayan.reggie.dto.OrdersDto;
import com.chenjiayan.reggie.entity.Orders;

import java.sql.Date;

public interface OrdersService extends IService<Orders> {
    /**
     * 保存订单（包括地址等）、订单详细、
     * @param orders
     * @return
     */
    Boolean submitOrder(Orders orders);

    /**
     * 分页查询订单信息和订单详细信息
     * @param page
     * @param pageSize
     * @return
     */
    Page<OrdersDto> pageDto(int page, int pageSize);


    /**
     * 后台分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<OrdersDto> pageAllDto(int page, int pageSize, Long number, String beginTime, String endTime);
}
