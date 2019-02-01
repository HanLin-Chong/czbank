package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.NraFile;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import com.relesee.service.AuditorService;
import com.relesee.service.NraQueueService;
import com.relesee.service.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Header;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("auditor/c/")
@RequiresPermissions( {"auditorController"} )
public class AuditorController {

    private static final Logger logger = Logger.getLogger(AuditorController.class);

    @Autowired
    UserService userService;

    @Autowired
    AuditorService auditorService;

    @Autowired
    NraQueueService nraQueueService;

    @Value("#{projectProperties['output.rootpath']}")//projectProperties是在spring的配置文件中配置的一个bean
    private String OUTPUT_ROOT_PATH;


    @RequestMapping("downloadNraFile/{id}")
    public ResponseEntity<?> downloadNraFile(@PathVariable String id, HttpServletResponse response){

        String DIRECTORY = OUTPUT_ROOT_PATH+"/files/nra";
        String FILE_URI = DIRECTORY+"/"+id;
        NraFile nraFile = nraQueueService.getNraFileById(id);
        nraFile.setFileName(StringUtils.trim(nraFile.getFileName()));
        File file = new File(FILE_URI);
        HttpHeaders headers = new HttpHeaders();

        if (file.exists()){
            String filename = null;
            try {
                filename = new String(nraFile.getFileName().getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            ResponseEntity<byte[]> resultTrue = null;
            try {
                resultTrue = new ResponseEntity(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
            } catch (IOException e) {
                logger.error("审核员[downloadNraFile/{?}]创建ResponseEntity时出错");

            }
            return resultTrue;
        } else {
            //文件不存在
            logger.error("下载的nra文件（"+nraFile.getFileName()+"）不存在，nra文件信息："+JSON.toJSONString(nraFile));
            ResponseEntity<String> resultFalse = null;

            resultFalse = new ResponseEntity("未找到文件("+nraFile.getFileName()+")", headers, HttpStatus.NOT_FOUND);
            return resultFalse;
        }

    }




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

    @RequestMapping(value="changeHeadPhoto", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String changeHeadPhoto(MultipartFile headPhoto, String userId, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        Result<String> result = auditorService.changeHeadPhoto(headPhoto, userId, realpath);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getNraQueue", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getNraQueue(String fileName, String beginDate, String endDate){
        return JSON.toJSONString(nraQueueService.getQueue(fileName, beginDate, endDate));
    }


}
