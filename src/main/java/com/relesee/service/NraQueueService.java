package com.relesee.service;

import com.alibaba.fastjson.JSON;
import com.relesee.dao.NraFileDao;
import com.relesee.domains.NraFile;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import com.relesee.utils.FileUtil;
import com.relesee.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class  NraQueueService {

    private static final Logger logger = Logger.getLogger(NraQueueService.class);

    //private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("#{projectProperties['output.rootpath']}")//projectProperties是在spring的配置文件中配置的一个bean
    private String OUTPUT_ROOT_PATH;

    @Autowired
    NraFileDao nraFileDao;

    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public List<NraFile> getQueue(String fileName, String beginDate, String endDate){
        if (StringUtils.isBlank(fileName)){
            fileName = "";
        }
        if (StringUtils.isBlank(beginDate)){
            beginDate = null;
        }
        if (StringUtils.isBlank(endDate)){
            endDate = null;
        }
        return nraFileDao.selectNraQueue(fileName, beginDate, endDate);

    }

    public List<NraFile> getHistory(String fileName, String beginDate, String endDate){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isBlank(fileName)){
            fileName = "";
        }
        if (StringUtils.isBlank(beginDate)){
            beginDate = null;
        }
        if (StringUtils.isBlank(endDate)){
            endDate = null;
        }
        return nraFileDao.selectNraHistory(user.getUserId(), fileName, beginDate, endDate);
    }

    /**
     * 删除文件，需要并发锁，虽然每个客户经理只能删除自己的文件，但是以防万一，得加上锁，
     * 队列重新编号、逻辑删除、状态码调0工作都在同一个update标签中完成
     * @param id
     * @return
     */
    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public synchronized Result revokeNraFile(String id){
        Result result = new Result();
        int count = nraFileDao.deleteNraFileById(id);
        System.out.println(count);
        if (count >= 1){
            result.setFlag(true);
            result.setMessage("撤销申请成功");
        } else {
            result.setFlag(false);
            result.setMessage("撤销申请失败");
        }
        System.out.println(JSON.toJSONString(result));
        return result;
    }

    /**
     * nra文件的上传是manager特有的功能
     * @param file
     * @param nraFile
     * @return
     */
    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result nraUpload(MultipartFile file, NraFile nraFile){
        Result result = new Result();
        String uuid = FileUtil.uuid();
        //获取当前用户信息，这里主要使用userId
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String uri = OUTPUT_ROOT_PATH+"/files/nra/"+uuid;
        String directory = OUTPUT_ROOT_PATH+"/files/nra";



        //手动注入其它字段
        nraFile.setFileName(file.getOriginalFilename());
        nraFile.setUploader(user.getUserId());
        nraFile.setRestorePath(uri);
        nraFile.setId(uuid);

        //将上传信息存入数据库
        int insert = nraFileDao.insertNraFile(nraFile);
        //将文件写入磁盘
        boolean fileWriteSuccess = false;
        String message = "";
        try {
            FileUtil.createDirIfNotExist(directory);
            FileUtil.writeInputStreamToDirectory(file.getInputStream(), uri);
            fileWriteSuccess = true;
        } catch (Exception e){
            e.printStackTrace();
            logger.error("NRA文件上传发生异常："+e);
            message = e.getMessage();
        }

        if (fileWriteSuccess && (insert == 1) ){
            result.setFlag(true);
            result.setMessage("文件("+nraFile.getFileName()+")上传成功");
            result.setResult(nraFile);
        } else {
            result.setFlag(false);
            result.setMessage("文件上传失败:"+message+",详细请查看日志");
        }
        return result;
    }

    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public NraFile getNraFileById(String id) {
        NraFile nraFile = nraFileDao.selectNraFileById(id);

        return nraFile;
    }

    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    @RequiresPermissions( {"auditorController"} )
    public Result<NraFile> nraRefuse(NraFile nraFile){
        nraFile.setStatusCode(2);
        Result<NraFile> result = new Result();
        int count = nraFileDao.updateStatusRefused(nraFile);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("文件："+nraFile.getFileName()+" 已拒绝");
            result.setResult(nraFile);
        } else {
            result.setFlag(false);
            result.setMessage("文件："+nraFile.getFileName()+" 在拒绝申请时失败");
            result.setResult(nraFile);
        }
        return result;
    }

    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    @RequiresPermissions( {"auditorController"} )
    public Result<NraFile> nraPass(NraFile nraFile){
        nraFile.setStatusCode(1);
        Result<NraFile> result = new Result();
        int count = nraFileDao.updateStatusPassed(nraFile);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("文件："+nraFile.getFileName()+" 审核通过");
            result.setResult(nraFile);
        } else {
            result.setFlag(false);
            result.setMessage("文件："+nraFile.getFileName()+" 在通过审核时失败");
            result.setResult(nraFile);
        }
        return result;
    }

    /**
     * 所有审核员都要通过此方法批量取出NRA申请，每次取出的数量由审核员自己决定
     * 先将NRA文件的状态标为已下载
     * @return
     */
    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    @RequiresPermissions( {"auditorController"} )
    public synchronized Result<List<NraFile>> getForAuditor(int amount){
        Result<List<NraFile>> result = new Result();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //（逻辑上）锁定资源
        int count = nraFileDao.updateNraAuditor(amount, user.getUserId());

        List<NraFile> list = nraFileDao.selectForAudit(amount, user.getUserId());
        if (count <= amount && count >= 1){
            result.setFlag(true);
            result.setMessage("成功取出"+count+"条申请");
            result.setResult(list);
        } else if (count == 0){
            result.setFlag(true);
            result.setMessage("没有待审核的申请");
            result.setResult(list);
        } else {
            result.setFlag(false);
            result.setMessage("获取申请失败");
            result.setResult(list);
        }

        return result;
    }
}
