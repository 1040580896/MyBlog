package com.mszlu.blog.utils;

import com.mszlu.blog.dao.pojo.SysUser;

/**
 * @Author xiaokaixin
 * @Date 2021/8/17 14:09
 * @Version 1.0
 */
public class UserThreadLocal {

    private UserThreadLocal(){}

    //线程变量隔离
    private static final ThreadLocal<SysUser> LOCAL =  new ThreadLocal<>();

    public static void put(SysUser sysUser){
        LOCAL.set(sysUser);
    }

    public static SysUser get(){
        return LOCAL.get();
    }
    public static void remove(){
        LOCAL.remove();
    }
}
