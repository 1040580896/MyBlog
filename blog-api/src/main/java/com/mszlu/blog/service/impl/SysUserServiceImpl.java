package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.SysUserMapper;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.LoginUserVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author xiaokaixin
 * @Date 2021/8/14 20:07
 * @Version 1.0
 */
@Service
public class SysUserServiceImpl implements com.mszlu.blog.service.SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private LoginService loginService;

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if(sysUser ==null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("https://dd-static.jd.com/ddimg/jfs/t1/198004/9/3705/10322/611cbdfeE252f77be/f255126f0b12eb0e.jpg");
            sysUser.setNickname("小羊");
        }
        UserVo userVo= new UserVo();
        BeanUtils.copyProperties(sysUser,userVo);
        userVo.setId(String.valueOf(sysUser.getId()));
        return userVo;
    }

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if(sysUser ==null){
            sysUser = new SysUser();
            sysUser.setNickname("小唐");
        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1. token合法性校验
         *      是否为空 是否解析成功，redis是否存在
         * 2.如果校验失败，返回对应的结国
         * 2。如果成功，返回对应的结果
         */
        SysUser sysUser = loginService.checkToken(token);
        if(sysUser==null){
            return Result.fail(ErrorCode.Token_ERROR.getCode(),ErrorCode.Token_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setNickname(sysUser.getNickname());
        return Result.success(loginUserVo);
    }

    /**
     * 查询用户
     * @param account
     * @return
     */
    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getAccount,account);
        lambdaQueryWrapper.last("limit 1");
        return this.sysUserMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 保存用户
     * @param sysUser
     */
    @Override
    public void save(SysUser sysUser) {
        //保存用户 id会自动生成
        //保存 默认生成的id是分布式id 雪花算法
        this.sysUserMapper.insert(sysUser);
    }
}
