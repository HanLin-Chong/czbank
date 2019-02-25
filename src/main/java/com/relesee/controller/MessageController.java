package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.User;
import com.relesee.domains.layim.*;
import com.relesee.service.MessageService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

//配合layim使用，即layim的所有操作都在这
@Controller
public class MessageController {

    @Autowired
    MessageService messageService;

    @RequiresPermissions( {"auditorController"} )
    @RequestMapping(value = "auditor/message/init", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String init(){
        Init init = messageService.init();
        return JSON.toJSONString(init);
    }

    /**
     * 参数id是layim传递过来的，代表群组的Id
     * @param id
     * @return
     */
    @RequiresPermissions( {"auditorController"} )
    @RequestMapping(value = "auditor/message/members", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String members(String id){
        Members result = messageService.members(id);
        return JSON.toJSONString(result);
    }

    @RequiresPermissions( {"auditorController"} )
    @RequestMapping(value = "auditor/message/uploadImage", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String uploadImage(MultipartFile file){
        return "";
    }

}
