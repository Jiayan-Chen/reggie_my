package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.entity.Employee;
import com.chenjiayan.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<String> login(HttpServletRequest request, @RequestBody Employee employee){
        // log.info(employee.toString()); //接收信息成功
        // 密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(employee.getUsername()!=null,Employee::getUsername,employee.getUsername());

        Employee emp = employeeService.getOne(queryWrapper);
        if(emp==null){
            // 用户不存在
            return R.error("用户名或密码错误！");
        }

        // 用户存在，进行密码匹对
        if(!emp.getPassword().equals(password)){
            // 密码不同
            return R.error("用户名或密码错误！");
        }
        //查看员工账号状态
        if(emp.getStatus()!=1){
            return R.error("该账号已禁用！");
        }
        // 将用户id存储到session中
        request.getSession().setAttribute("employee",emp.getId());
        return R.success("登录成功！");
    }
}
