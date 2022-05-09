package com.mszlu.blog.controller;

import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author xiaokaixin
 * @Date 2021/8/16 09:03
 * @Version 1.0
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginParam loginParam, HttpServletRequest request, HttpSession session){
        //登陆 验证用户，访问用户表，但是
        Result login = loginService.login(loginParam);
        String  token = (String)login.getData();
        //request.setAttribute("Authorization",token);
        session.setAttribute("Authorization",token);
        return login;
    }
}
