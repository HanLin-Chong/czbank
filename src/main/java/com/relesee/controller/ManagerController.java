package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.*;
import com.relesee.service.ForeignAccService;
import com.relesee.service.ManagerService;
import com.relesee.service.NraQueueService;
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

    @Autowired
    NraQueueService nraQueueService;

    @Autowired
    ForeignAccService foreignAccService;

    @RequestMapping("checkLogin")
    @ResponseBody
    public String checkLogin(){
        if (userService.checkManagerLogin().isFlag()){
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


    /**
     *  以下是NRA功能
     */
    @RequestMapping(value = "nraUpload", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String nraUpload(MultipartFile file, boolean isQualityCustomer, String note){

        NraFile nraFile = new NraFile();
        nraFile.setQualityCustomer(isQualityCustomer);
        nraFile.setNote(note);


        Result result = nraQueueService.nraUpload(file, nraFile);

        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getNraQueue", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getNraQueue(String fileName, String beginDate, String endDate){
        return JSON.toJSONString(nraQueueService.getQueue(fileName, beginDate, endDate));
    }

    @RequestMapping(value = "getNraHistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getNraHistory(String fileName, String beginDate, String endDate){
        return JSON.toJSONString(nraQueueService.getHistory(fileName, beginDate, endDate));
    }

    @RequestMapping(value = "revokeNraFile", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String revokeNraFile(String id){

        Result result = nraQueueService.revokeNraFile(id);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "applyPriority", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String applyPriority(String id){
        //TODO 提交到审核员处，等待审核员功能
        return "";
    }

    /**
     *  以下是境外账户申请功能,主要使用ForeignAccService
     */
    @RequestMapping(value = "ebayApplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String ebayApplication(EbayApplication ebayApplication){

        Result<String> result = foreignAccService.ebayAcc(ebayApplication);

        return JSON.toJSONString(result);
    }
}
