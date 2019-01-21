package com.relesee.service;

import com.alibaba.fastjson.JSON;
import com.relesee.dao.UserDao;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    @Autowired
    UserDao userDao;

    public Result login(String userId, String pass){
        Result<User> result = new Result();
        User u = userDao.selectUserByIdAndPass(userId, pass);
        if (u == null){
            result.setFlag(false);
            result.setMessage("ID或密码错误");
        } else {
            result.setFlag(true);
            result.setMessage("登录成功");
            result.setResult(u);
        }
        return result;
    }

    public Result checkManagerLogin(){
        Result result = new Result();
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("manager")){
            result.setFlag(true);
        } else {
            result.setFlag(false);
        }
        return result;
    }

    public Result checkAuditorLogin(){
        Result result = new Result();
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("auditor")){
            result.setFlag(true);
        } else {
            result.setFlag(false);
        }
        return result;
    }

}
