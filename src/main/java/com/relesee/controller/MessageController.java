package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import com.relesee.domains.layim.*;
import com.relesee.service.MessageService;
import com.sun.net.httpserver.HttpContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//配合layim使用，即layim的所有操作都在这
@Controller
public class MessageController {

    @Autowired
    MessageService messageService;

    @RequestMapping(value = "message/init", produces = "text/plane;charset=utf-8")
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
    @RequestMapping(value = "message/members", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String members(String id){
        Members result = messageService.members(id);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "message/uploadImage", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String uploadImage(MultipartFile file, HttpServletRequest request){
        String realPath = request.getServletContext().getRealPath("/");
        String path = realPath+"img/im/";
        UploadRes res = messageService.uploadImage(file, path);
        return JSON.toJSONString(res);
    }

    @RequestMapping(value = "message/uploadFile", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String uploadFile(MultipartFile file, HttpServletRequest request){
        String realPath = request.getServletContext().getRealPath("/");
        String path = realPath+"files/im/";
        UploadRes res = messageService.uploadFile(file, path);
        return JSON.toJSONString(res);
    }

    @RequestMapping("message/downloadImg/{fileName:.+}")
    public ResponseEntity<?> downloadImg(@PathVariable String fileName, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        String DIRECTORY = realpath+"img/im";
        String FILE_URI = DIRECTORY+"/"+fileName;
        System.out.println(FILE_URI);
        File file = new File(FILE_URI);
        HttpHeaders headers = new HttpHeaders();
        if (file.exists()){
            String filename = null;
            try {
                filename = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            ResponseEntity<byte[]> resultTrue = null;
            try {
                resultTrue = new ResponseEntity(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultTrue;
        } else {
            //文件不存在
            ResponseEntity<String> resultFalse = null;
            resultFalse = new ResponseEntity("图片（"+fileName+"）不存在，可能已经被移除", headers, HttpStatus.NOT_FOUND);
            return resultFalse;
        }
    }

    @RequestMapping("message/downloadFile/{fileName:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        String DIRECTORY = realpath+"files/im";
        String FILE_URI = DIRECTORY+"/"+fileName;
        System.out.println(FILE_URI);
        File file = new File(FILE_URI);
        HttpHeaders headers = new HttpHeaders();
        if (file.exists()){
            String filename = null;
                try {
                filename = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            ResponseEntity<byte[]> resultTrue = null;
            try {
                resultTrue = new ResponseEntity(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultTrue;
        } else {
            //文件不存在
            ResponseEntity<String> resultFalse = null;
            resultFalse = new ResponseEntity("文件（"+fileName+"）不存在，可能已经被移除", headers, HttpStatus.NOT_FOUND);
            return resultFalse;
        }
    }

    /**
     * 每次打开消息中心，客户端都要将其ws的地址提交至session中，以便别的客户端连接
     * application中有通讯录属性：addressList,
     * 返回application中的通讯录
     * @param wsPath
     * @return
     */
    /*@RequestMapping(value = "message/iamhere", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String IamHere(String wsPath, HttpServletRequest request){
        ServletContext application = request.getServletContext();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //Map<String, String> addressList = new HashMap();
        Object obj = application.getAttribute("addressList");
        if (obj == null){
            System.out.println("----------  application为空  ----------------");
            obj = new HashMap<String, String>();
            application.setAttribute("addressList", obj);
        }
        Map<String, String> addressList = (HashMap<String, String>) obj;
        addressList.put(user.getUserId(), wsPath);
        return JSON.toJSONString(addressList);
    }*/

    /*@RequestMapping(value = "message/getUserAddress", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getUserAddress(String userId, HttpServletRequest request){
        Result result = new Result();
        ServletContext application = request.getServletContext();
        Map<String, String> addressList = (HashMap<String, String>) application.getAttribute("addressList");
        String address = addressList.get(userId);
        if (StringUtils.isBlank(address)){
            result.setFlag(false);
            result.setMessage("联系人（工号："+userId+"）不在线");
        } else {
            result.setFlag(true);
            result.setMessage(address);
        }
        return JSON.toJSONString(result);
    }*/

    @RequestMapping( value = "message/changeUserSign", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String changeUserSign(String sign){
        Result result = messageService.changeUserSign(sign);
        return JSON.toJSONString(result);
    }
}
