package com.chenjiayan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenjiayan.reggie.common.BaseContext;
import com.chenjiayan.reggie.dto.OrdersDto;
import com.chenjiayan.reggie.entity.*;
import com.chenjiayan.reggie.mapper.OrdersMapper;
import com.chenjiayan.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 保存订单（包括地址等）、订单详细、
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public Boolean submitOrder(Orders orders) {
        String number = IdWorker.getIdStr();
        orders.setNumber(number);
        orders.setStatus(2);
        Long userId = BaseContext.getCurrentId();
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        /*
        //用户名
        private String userName;
        //手机号
        private String phone;
        //地址
        private String address;
        //收货人
        private String consignee;*/
        User user = userService.getById(userId);
        orders.setUserName(user.getName());

        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName()==null ? "":addressBook.getProvinceName())+
                (addressBook.getCityName()==null ? "":addressBook.getCityName())+
                (addressBook.getDistrictName()==null ? "":addressBook.getDistrictName())+
                addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());

        this.save(orders);
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartQueryWrapper);
        //清空购物车
        shoppingCartService.remove(shoppingCartQueryWrapper);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            BeanUtils.copyProperties(item,orderDetail,"id");
            return orderDetail;
        }).collect(Collectors.toList());

        boolean b = orderDetailService.saveBatch(orderDetails);
        return b;
    }

    /**
     * 分页查询订单信息和订单详细信息
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    @Transactional
    public Page<OrdersDto> pageDto(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
        ordersQueryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId())
                .orderByDesc(Orders::getCheckoutTime);
        this.page(ordersPage,ordersQueryWrapper);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> dtoList = records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            Long ordersId = item.getId();
            LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailQueryWrapper.eq(OrderDetail::getOrderId,ordersId);
            List<OrderDetail> list = orderDetailService.list(orderDetailQueryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(dtoList);
        return ordersDtoPage;
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
    @Override
    @Transactional
    public Page<OrdersDto> pageAllDto(int page, int pageSize, Long number, String beginTime, String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
        ordersQueryWrapper
                .eq(number!=null,Orders::getNumber,number)
                .between(beginTime!=null&&endTime!=null,Orders::getOrderTime,beginTime,endTime)
                .orderByDesc(Orders::getCheckoutTime);
        this.page(ordersPage,ordersQueryWrapper);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> dtoList = records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            Long ordersId = item.getId();
            LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailQueryWrapper.eq(OrderDetail::getOrderId,ordersId);
            List<OrderDetail> list = orderDetailService.list(orderDetailQueryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(dtoList);
        return ordersDtoPage;
    }
}
