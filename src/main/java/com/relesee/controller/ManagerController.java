package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import com.relesee.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiresPermissions( {"managerController"} )
@RequestMapping("cManager/c/")
public class ManagerController {

    @Autowired
    UserService userService;

    @RequestMapping("checkLogin")
    @ResponseBody
    public String checkLogin(){
        if (userService.checkLogin().isFlag()){
            return "1";
        } else {
            return "0";
        }

    }

    @RequestMapping("getManager")
    @ResponseBody
    public String getManager(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Result<User> result = new Result<>();
        if (StringUtils.equals(user.getRole(), "manager")){
            result.setFlag(true);
            result.setMessage("当前Subject的角色为manager");
            result.setResult(user);
        } else {
            result.setFlag(false);
            result.setMessage("当前Subject的角色不为manager，无法获取用户信息，请注销后重新登录");
        }
        return JSON.toJSONString(result);
    }


}
