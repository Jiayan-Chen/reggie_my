package com.chenjiayan.reggie.dto;

import com.chenjiayan.reggie.entity.OrderDetail;
import com.chenjiayan.reggie.entity.Orders;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrdersDto extends Orders implements Serializable {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
