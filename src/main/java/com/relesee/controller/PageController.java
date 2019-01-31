package com.relesee.controller;


import com.alibaba.fastjson.JSON;
import com.relesee.domains.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 此Controller为了实现REST风格,管理所有的非Controller路径请求
 */
@Controller
public class PageController {

    private static final Logger logger = Logger.getLogger(PageController.class);

    /**
     * 页面控制器
     * @param page
     * @return
     */


    @RequestMapping("/")
    public String login1(HttpServletResponse response){
        logger.info("\n----------------------       通过根（/）进入网站,重定向至（/login）      -------------------------");
        return "redirect:/login";
    }

    @RequestMapping("/login")
    public String login2(){
        return "WEB-INF/view/login.html";
    }

    @RequestMapping("/error/{code}")
    public String error(@PathVariable String code){
        logger.info("跳转至错误页："+code);
        return "WEB-INF/view/error.jsp?code="+code;
    }

    @RequestMapping("/forget")
    public String forgetPassword(){
        return "WEB-INF/view/forget.html";
    }

    @RequestMapping("/advice")
    public String advice(){
        return "WEB-INF/view/advice.html";
    }


    @RequestMapping("/distribute")
    public String distribute(){
        logger.info("进入分发器");
        Subject subject = SecurityUtils.getSubject();

        if (subject.hasRole("manager")){
            logger.info("重定向至客户经理主页");
            return "redirect:cManager/index";
        } else if (subject.hasRole("auditor")){
            logger.info("重定向至审核员主页");
            return "redirect:auditor/index";
        } else if (subject.hasRole("root")){
            logger.info("重定向至root主页");
            return "redirect:root";
        } else {
            logger.info("分发器分发时用户不具备权限，跳转至错误页，错误码：1");
            return "redirect:error/1";
        }

    }

    @RequiresPermissions( {"managerPage"} )
    @RequestMapping("cManager/{page}")
    public String managerPage(@PathVariable String page){
        return "WEB-INF/view/manager/"+page+".html";
    }

    @RequiresPermissions( {"auditorPage"} )
    @RequestMapping("auditor/{page}")
    public String auditorPage(@PathVariable String page){
        return "WEB-INF/view/auditor/"+page+".html";
    }

    @RequiresRoles( {"root"} )
    @RequestMapping("root/{page}")
    public String rootPage(@PathVariable String page){
        return "WEB-INF/view/root/"+page+".html";
    }

}
