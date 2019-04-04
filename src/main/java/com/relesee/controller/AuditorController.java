package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.dao.AmazonEUdao;
import com.relesee.dao.AmazonUSdao;
import com.relesee.dao.EbayApplicationDao;
import com.relesee.domains.*;
import com.relesee.service.*;
import com.relesee.utils.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Header;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tools.zip.ZipOutputStream;
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
import java.io.InputStream;
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

    @Autowired
    ForeignAccService foreignAccService;

    @Autowired
    ForeignFeedBackService feedBackService;

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

    @RequestMapping(value="getAuditor", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getAuditor(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Result<User> result = new Result<>();
        if (StringUtils.equals(user.getRole(), "auditor")){
            result.setFlag(true);
            result.setMessage("当前Subject的角色为auditor");
            result.setResult(user);
        } else {
            result.setFlag(false);
            result.setMessage("当前Subject的角色不为auditor，无法获取用户信息，请注销后重新登录");
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "modifyPersonalInfo", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String modifyPersonalInfo(User user, HttpServletRequest request){
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        Result<User> result = auditorService.updatePersonalInformation(user);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "changePassword", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String changePassword(String oldPassword, String newPassword){
        Result<User> result = auditorService.updatePassword(oldPassword, newPassword);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getNraQueue", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getNraQueue(String fileName, String beginDate, String endDate){
        return JSON.toJSONString(nraQueueService.getQueue(fileName, beginDate, endDate));
    }

    @RequestMapping(value = "nraRefuse", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String nraRefuse(NraFile nraFile){
        Result<NraFile> result = nraQueueService.nraRefuse(nraFile);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "nraPass", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String nraPass(NraFile nraFile){
        Result<NraFile> result = nraQueueService.nraPass(nraFile);
        return JSON.toJSONString(result);
    }
    @RequestMapping(value = "nraRelease", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String nraRelease(NraFile nraFile){
        Result<NraFile> result = nraQueueService.nraRelease(nraFile);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getNraForAudit", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getNraForAudit(int amount){
        Result<List<NraFile>> result = nraQueueService.getForAuditor(amount);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getLockedNra", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getLockedNra(){
        Result<List<NraFile>> result = nraQueueService.getLockedFiles();
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getPriorityApplications", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getPriorityApplications(){
        Result<List<NraFile>> result = nraQueueService.getPriorityApplications();
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "priorityPass", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String priorityPass(NraFile nraFile){
        Result result = nraQueueService.priorityPass(nraFile);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "priorityRefused", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String priorityRefused(NraFile nraFile){
        Result result = nraQueueService.priorityRefused(nraFile);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "searchManager", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String searchManagerAcc(String userName, String phone, String email){
        Result<List<User>> result = userService.searchManager(userName, phone, email);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "addManager", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String addManager(User user){
        Result result = userService.addManager(user);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "validateManagerId", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String validateManagerId(String userId){
        boolean result = userService.validateManagerId(userId);
        return result?"{\"valid\":true}":"{\"valid\":false}";
    }

    @RequestMapping(value = "blockManagerAcc", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String blockManagerAcc(String userId){
        Result result = userService.blockManagerAcc(userId);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "activateManagerAcc", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String activateManagerAcc(String userId){
        Result result = userService.activateManagerAcc(userId);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getOneEbayApplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getOneEbayApplication(){
        Result<EbayApplication> result = foreignAccService.getOneEbayApplication();
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "ebayPassed", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String ebayPassed(EbayApplication ebayApplication, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        Result result = foreignAccService.ebayPassed(ebayApplication, realpath);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "ebayRefused", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String ebayRefused(EbayApplication ebayApplication){
        Result result = foreignAccService.ebayRefused(ebayApplication);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "updateEbayApplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String updateEbayApplication(EbayApplication input){
        Result result = foreignAccService.updateEbayApplication(input);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getOneAmazonUSapplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getOneAmazonUSapplication(){
        Result<AmazonUSapplication> result = foreignAccService.getOneAmazonUSapplication();
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "amazonUSPassed", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String amazonUSpassed(AmazonUSapplication amazonUSapplication){
        Result result = foreignAccService.amazonUSpassed(amazonUSapplication);
        return JSON.toJSONString(result);
    }
    @RequestMapping(value = "amazonUSRefused", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String amazonUSrefused(AmazonUSapplication amazonUSapplication){
        Result result = foreignAccService.amazonUSrefused(amazonUSapplication);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "updateAmazonUSapplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String updateAmazonUSapplication(AmazonUSapplication input){
        Result result = foreignAccService.updateAmazonUSapplication(input);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getOneAmazonEUapplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getOneAmazonEUapplication(){
        Result<AmazonEUapplication> result = foreignAccService.getOneAmazonEUapplication();
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "amazonEUPassed", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String amazonEUpassed(AmazonEUapplication amazonEUapplication){
        Result result = foreignAccService.amazonEUpassed(amazonEUapplication);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "amazonEURefused", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String amazonEUrefused(AmazonEUapplication amazonEUapplication){
        Result result = foreignAccService.amazonEUrefused(amazonEUapplication);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "updateAmazonEUapplication", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String updateAmazonEUapplication(AmazonEUapplication input){
        Result result = foreignAccService.updateAmazonEUapplication(input);
        return JSON.toJSONString(result);
    }

    /**
     * 反馈文件处理出错时，是否要启用手动输入？
     * @param file
     * @return
     */
    @RequestMapping(value = "foreignFeedback", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String foreignFeedback(MultipartFile file, HttpServletRequest request){

        Result result = null;
        try {
            result = feedBackService.process(file.getInputStream(), request.getServletContext().getRealPath("/"));
        } catch (IOException e) {
            e.printStackTrace();
            result = new Result();
            result.setFlag(false);
            result.setMessage("上传反馈文件失败");
        }


        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getEbayHistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getEbayHistory(int page, int limit){
        int begin = (page-1)*limit;
        LayTableResult<List<EbayApplication>> result = foreignAccService.getEbayHistory(begin, limit);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "searchEbayHistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String searchEbayHistory(String key){
        List<EbayApplication> result = foreignAccService.searchEbayHistory(key);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getAmazonUShistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getAmazonUSHistory(int page, int limit){
        int begin = (page-1)*limit;
        LayTableResult<List<AmazonUSapplication>> result = foreignAccService.getAmazonUShistory(begin, limit);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "searchAmazonUShistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String searchAmazonUSHistory(String key){
        List<AmazonUSapplication> result = foreignAccService.searchAmazonUShistory(key);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "getAmazonEUhistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getAmazonEUhistory(int page, int limit){
        int begin = (page-1)*limit;
        LayTableResult<List<AmazonEUapplication>> result = foreignAccService.getAmazonEUhistory(begin, limit);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "searchAmazonEUhistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String searchAmazonEUhistory(String key){
        List<AmazonEUapplication> result = foreignAccService.searchAmazonEUhistory(key);
        return JSON.toJSONString(result);
    }

    @RequestMapping("downloadEbay/{id}/{fileName:.+}")//:.+解决后缀名被省略
    public ResponseEntity<?> downloadEbay(@PathVariable String id, @PathVariable String fileName, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        String DIRECTORY = realpath+"/files/ebay/"+id;
        String FILE_URI = DIRECTORY+"/"+fileName;
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
                logger.error("审核员controller[downloadEbay/{id}/{fileName}]创建ResponseEntity时出错");

            }
            return resultTrue;
        } else {
            //文件不存在
            logger.error("文件("+FILE_URI+")不存在，可能已经被移除");
            ResponseEntity<String> resultFalse = null;

            resultFalse = new ResponseEntity("文件（"+fileName+"）不存在，可能已经被移除", headers, HttpStatus.NOT_FOUND);
            return resultFalse;
        }

    }

    @RequestMapping("downloadAmazonUS/{id}/{fileName:.+}")//:.+解决后缀名被省略
    public ResponseEntity<?> downloadAmazonUS(@PathVariable String id, @PathVariable String fileName, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        String DIRECTORY = realpath+"/files/amazon/us/"+id;
        String FILE_URI = DIRECTORY+"/"+fileName;
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
                logger.error("审核员controller[downloadAmazonUS/{id}/{fileName}]创建ResponseEntity时出错");

            }
            return resultTrue;
        } else {
            //文件不存在
            logger.error("文件("+FILE_URI+")不存在，可能已经被移除");
            ResponseEntity<String> resultFalse = null;

            resultFalse = new ResponseEntity("文件（"+fileName+"）不存在，可能已经被移除", headers, HttpStatus.NOT_FOUND);
            return resultFalse;
        }
    }

    @RequestMapping("downloadAmazonEU/{id}/{fileName:.+}")//:.+解决后缀名被省略
    public ResponseEntity<?> downloadAmazonEU(@PathVariable String id, @PathVariable String fileName, HttpServletRequest request){
        String realpath = request.getServletContext().getRealPath("/");
        String DIRECTORY = realpath+"/files/amazon/eu/"+id;
        String FILE_URI = DIRECTORY+"/"+fileName;
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
                logger.error("审核员controller[downloadAmazonEU/{id}/{fileName}]创建ResponseEntity时出错");

            }
            return resultTrue;
        } else {
            //文件不存在
            logger.error("文件("+FILE_URI+")不存在，可能已经被移除");
            ResponseEntity<String> resultFalse = null;

            resultFalse = new ResponseEntity("文件（"+fileName+"）不存在，可能已经被移除", headers, HttpStatus.NOT_FOUND);
            return resultFalse;
        }
    }

    @RequestMapping(value = "getNraHistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String getNraHistory(int page, int limit){
        int begin = (page-1)*limit;
        LayTableResult<List<NraFile>> result = nraQueueService.getHistoryAuditor(begin, limit);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "searchNraHistory", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String searchNraHistory(String key){
        List<NraFile> result = nraQueueService.searchHistory(key);
        return JSON.toJSONString(result);
    }

    @Autowired
    EbayApplicationDao ebayApplicationDao;

    @RequestMapping("ebayZip/{uuid}")
    public void ebayZip(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response){
        String realpath = request.getServletContext().getRealPath("/");
        String forderPath = realpath+"/files/ebay/"+uuid;
        try {
            EbayApplication a = ebayApplicationDao.selectById(uuid);
            String zipName = "ebay_"+ a.getManagerDepartment() +"_"+ a.getManagerName()+ "_" + a.getUploadTime() +".zip";
            response.setHeader("content-type", "application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 设置浏览器响应头对应的Content-disposition
            response.setHeader("Content-disposition", "attachment;filename=" + new String(zipName.getBytes("utf-8"), "iso8859-1"));

            ZipOutputStream out = new ZipOutputStream(response.getOutputStream());

            ZipUtil.zip(forderPath, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    AmazonUSdao amazonUSdao;

    @RequestMapping("amazonUSzip/{uuid}")
    public void amazonUSZip(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response){
        String realpath = request.getServletContext().getRealPath("/");
        String forderPath = realpath+"/files/amazon/us/"+uuid;
        try {
            AmazonUSapplication a = amazonUSdao.selectById(uuid);
            String zipName = "amazon_us_"+ a.getManagerDepartment() +"_"+ a.getManagerName()+ "_" + a.getUploadTime() +".zip";
            response.setHeader("content-type", "application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 设置浏览器响应头对应的Content-disposition
            response.setHeader("Content-disposition", "attachment;filename=" + new String(zipName.getBytes("utf-8"), "iso8859-1"));

            ZipOutputStream out = new ZipOutputStream(response.getOutputStream());

            ZipUtil.zip(forderPath, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    AmazonEUdao amazonEUdao;

    @RequestMapping("amazonEUzip/{uuid}")
    public void amazonEUzip(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response){
        String realpath = request.getServletContext().getRealPath("/");
        String forderPath = realpath+"/files/amazon/eu/"+uuid;
        try {
            AmazonEUapplication a = amazonEUdao.selectById(uuid);
            String zipName = "ebay_"+ a.getManagerDepartment() +"_"+ a.getManagerName()+ "_" + a.getUploadTime() +".zip";
            response.setHeader("content-type", "application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 设置浏览器响应头对应的Content-disposition
            response.setHeader("Content-disposition", "attachment;filename=" + new String(zipName.getBytes("utf-8"), "iso8859-1"));

            ZipOutputStream out = new ZipOutputStream(response.getOutputStream());

            ZipUtil.zip(forderPath, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
