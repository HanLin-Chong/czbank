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
@RequestMapping("auditor/c/")
@RequiresPermissions( {"auditorController"} )
public class AuditorController {

    @Autowired
    UserService userService;

    @RequestMapping("checkLogin")
    @ResponseBody
    public String checkLogin(){
        if (userService.checkAuditorLogin().isFlag()){
            return "1";
        } else {
            return "0";
        }
    }

    @RequestMapping("getAuditor")
    @ResponseBody
    public String getManager(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Result<User> result = new Result<>();
        if (StringUtils.equals(user.getRole(), "auditor")){
            result.setFlag(true);
            result.setMessage("当前Subject的角色为auditor");
            result.setResult(user);
        } else {
            result.setFlag(false);
            result.setMessage("当前Subject的角色不为manager，无法获取用户信息，请注销后重新登录");
        }
        return JSON.toJSONString(result);
    }
}
