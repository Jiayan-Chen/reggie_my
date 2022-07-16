package com.chenjiayan.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenjiayan.reggie.dto.OrdersDto;
import com.chenjiayan.reggie.entity.Orders;

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
}
