package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruiji.common.R;
import com.ruiji.entity.User;
import com.ruiji.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<String> login(@RequestBody User user, HttpServletRequest httpServletRequest){
        String phoneNumber = user.getPhone();
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone,phoneNumber);
        User loginOneUser = userService.getOne(lqw);
        if(loginOneUser!=null){
            httpServletRequest.getSession().setAttribute("user",loginOneUser.getId());
        }else {
            userService.save(user);
            httpServletRequest.getSession().setAttribute("user", user.getId());
        }
        return R.success("登陆成功");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.removeAttribute("user");
        return R.success("退出当前用户成功");
    }
}
