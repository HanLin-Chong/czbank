package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.*;
import com.relesee.service.ManagerService;
import com.relesee.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiresPermissions( {"managerController"} )
@RequestMapping("cManager/c/")
public class ManagerController {

    @Autowired
    UserService userService;

    @Autowired
    ManagerService managerService;

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


    @RequestMapping(value = "nraUpload", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String nraUpload(MultipartFile file, boolean isQualityCustomer, String note){

        NraFile nraFile = new NraFile();
        nraFile.setQualityCustomer(isQualityCustomer);
        nraFile.setNote(note);


        Result result = managerService.nraUpload(file, nraFile);

        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getQueueBetween", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getQueueBetween(int page, int limit, String test){
        System.out.println(test);
        LayTableResponse<List<NraFile>> response = new LayTableResponse<>();


        int begin = (page - 1) * limit;
        //由于前端插件的原因，直接返回List比较方便
        List<NraFile> list = managerService.getQueueBetween(begin, limit);
        response.setCode(0);
        response.setCount(managerService.getNraQueueLength());
        response.setData(list);
        response.setMsg("获取队列成功，共有"+list.size()+"条数据");

        return JSON.toJSONString(response);

    }

}
