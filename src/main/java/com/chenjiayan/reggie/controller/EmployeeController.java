package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.entity.Employee;
import com.chenjiayan.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
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
        return R.success(emp);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    /**
     * 新建用户
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee){
        log.info(employee.toString());
        // 设置初始密码
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        employeeService.save(employee); //若用户名相同，则会报错，所以要进行异常处理
        return R.success("添加成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> pageR(int page,int pageSize,String name){
        Page<Employee> employeePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 根据name查询
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);
        queryWrapper.orderByAsc(Employee::getCreateUser);
        employeeService.page(employeePage,queryWrapper);
        return R.success(employeePage);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getOneById(@PathVariable Long id){
        //log.info(String.valueOf(id));
        Employee emp = employeeService.getById(id);
        if(emp==null){
            return R.error("该用户不存在！");
        }
        return R.success(emp);
    }

    /**
     * 修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){

        if(employee.getId()==1 && (!"admin".equals(employee.getUsername()))){
            // 管理员 管理员的用户名不能修改
            return R.error("管理员的用户名不能修改！");
        }
        if(StringUtils.isNotBlank(employee.getPassword())){
            String password = employee.getPassword();
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            employee.setPassword(password);
        }else {
            employee.setPassword(null);
        }
        boolean b = employeeService.updateById(employee);
        if(!b) return R.error("修改失败！");
        log.info("修改成功！");
        return R.success("修改成功！");
    }

}
