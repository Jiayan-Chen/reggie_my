package com.chenjiayan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenjiayan.reggie.common.BaseContext;
import com.chenjiayan.reggie.common.R;
import com.chenjiayan.reggie.entity.User;
import com.chenjiayan.reggie.service.UserService;
import com.chenjiayan.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/code")
    public R<String> sendCode(@RequestBody User user, HttpSession session){
        if(StringUtils.isNotBlank(user.getPhone())){
            String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
            log.info("验证码为："+code);
            session.setAttribute(user.getPhone(),code);
            return R.success("验证码已发送");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session){
        // 通过手机号获取session中保存的验证码
        String phone = (String) map.get("phone");
        String code = (String)session.getAttribute(phone);
        if(StringUtils.isBlank(code)){
            // 验证码为空
            R.error("验证码输入错误！");
        }
        String code1 = (String)map.get("code");
        if(!code.equals(code1)){
            // 验证码输入错误
            R.error("验证码输入错误！");
        }
        // 验证码正确 通过手机号查User 如果没有则创建

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if(user == null){
            log.info("新用户！");
            User user1 = new User();
            user1.setPhone(phone);
            userService.save(user1);
            session.setAttribute("user",user1.getId());
        }else{
            log.info("旧用户！");
            if(user.getStatus() == 0){
                return R.error("该用户禁用！");
            }else {
                session.setAttribute("user",user.getId());
            }
        }

        return R.success("登录成功！");
    }

    /**
     * 退出登录
     * @param httpSession
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession httpSession){
        httpSession.removeAttribute("user");
        return R.success("退出成功！");
    }
}
