package com.relesee.service;

import com.alibaba.fastjson.JSON;
import com.relesee.constant.UserStatus;
import com.relesee.dao.UserDao;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
            if (u.getUserState() == UserStatus.BLOCK_UP.getCode()){
                result.setFlag(false);
                result.setMessage("该账号已被停用");
            } else {
                result.setFlag(true);
                result.setMessage("登录成功");
                result.setResult(u);
            }

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

    @RequiresPermissions( {"auditorController"} )
    public Result addManager(User user){
        user.setDeptId("1");
        Result result = new Result();
        int count = userDao.insertManager(user);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("经理"+user.getUserName()+"注册成功，使用工号作为账号登录");
        } else {
            result.setFlag(false);
            result.setMessage("经理"+user.getUserName()+"注册失败");
        }
        return result;
    }

    public boolean validateManagerId(String userId){
        boolean flag = false;
        int count = userDao.selectCountManagerId(userId);
        if (count == 0){
            flag = true;
        }
        return flag;
    }

    public Result<List<User>> searchManager(String userName, String phone, String email){
        if (StringUtils.isBlank(userName)){
            userName = "";
        }
        if (StringUtils.isBlank(phone)){
            phone = "";
        }
        if (StringUtils.isBlank(email)){
            email = "";
        }
        Result<List<User>> result = new Result();
        List<User> list = userDao.selectManager(userName, phone, email);
        result.setResult(list);
        return result;
    }

    public Result blockManagerAcc(String userId){
        Result result = new Result();
        int count = userDao.updateManagerBlock(userId, UserStatus.BLOCK_UP.getCode());
        if (count == 1){
            result.setFlag(true);
            result.setMessage("停用账号成功");
        } else {
            result.setFlag(false);
            result.setMessage("停用账号失败");
        }
        return result;
    }

    public Result activateManagerAcc(String userId){
        Result result = new Result();
        int count = userDao.updateManagerActive(userId, UserStatus.NORMAL.getCode());
        if (count == 1){
            result.setFlag(true);
            result.setMessage("启用账号成功");
        } else {
            result.setFlag(false);
            result.setMessage("启用账号失败");
        }
        return result;
    }

}
