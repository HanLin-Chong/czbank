package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 用于登录事项的控制器
 */
@Controller
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    /**
     * 跳转到登录页
     * @return
     */

    @RequestMapping("/logout")
    public String logout(HttpServletResponse response){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        logger.info("用户注销成功");
        return "redirect:/login";
    }

    /**
     * 用户登录成功后，会在shiro的session中存入 "subject"(User对象，在ShiroService中进行)
     * @param user
     * @return
     */
    @RequestMapping("/c/login")
    public @ResponseBody String login(User user){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserId(), user.getPassword());
        Result result = new Result();

        try {

            subject.login(token);//登陆失败抛出AuthenticationException异常
            //Session session = subject.getSession();
            //session.setAttribute("subject", subject);
            result.setFlag(true);
        } catch (AuthenticationException e){
            result.setFlag(false);
            result.setMessage("用户名或密码错误");
        }

        return JSON.toJSONString(result);
    }

}
