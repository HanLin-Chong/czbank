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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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

    @RequestMapping(value="changeHeadPhoto", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String changeHeadPhoto(MultipartFile headPhoto, String userId, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        Result<String> result = managerService.changeHeadPhoto(headPhoto, userId, realpath);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value="getManager", produces = "text/plane;charset=utf-8")
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

    @RequestMapping(value = "modifyPersonalInfo", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String modifyPersonalInfo(User user, HttpServletRequest request){
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        Result<User> result = managerService.updatePersonalInformation(user);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "changePassword", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String changePassword(String oldPassword, String newPassword){
        Result<User> result = managerService.updatePassword(oldPassword, newPassword);
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
        Result result = nraQueueService.applyPriority(id);
        return JSON.toJSONString(result);
    }

    /**
     *  以下是境外账户申请功能,主要使用ForeignAccService
     */
    @RequestMapping(value = "ebayApplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String ebayApplication(EbayApplication ebayApplication, HttpServletRequest request){

        String realpath = request.getServletContext().getRealPath("/");
        Result<String> result = foreignAccService.ebayAcc(ebayApplication, realpath);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "amazonUSapplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String amazonUSapplication(AmazonUSapplication amazonUS, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        Result result = foreignAccService.amazonUSacc(amazonUS, realpath);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "amazonEUapplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String amazonEUapplication(AmazonEUapplication amazonEU, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        amazonEU.valueFormat();
        Result result = foreignAccService.amazonEUacc(amazonEU, realpath);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "advice", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String advice(String content){
        System.out.println(content);
        Result result = managerService.advice(content);
        return JSON.toJSONString(result);
    }
}
