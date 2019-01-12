package com.relesee.service;

import com.relesee.dao.NraFileDao;
import com.relesee.domains.NraFile;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import com.relesee.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ManagerService {

    private static final Logger logger = Logger.getLogger(ManagerService.class);

    @Value("#{projectProperties['output.rootpath']}")
    private String OUTPUT_ROOT_PATH;

    @Autowired
    NraFileDao nraFileDao;

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

    public List<NraFile> getQueueBetween(int begin, int size){
        List<NraFile> result = nraFileDao.selectNraQueueLimit(begin, size);
        int i = 1;
        for (NraFile record : result){
            record.setQueueNo(i+begin);
            i++;
        }
        return result;
    }

    public int getNraQueueLength(){
        return nraFileDao.selectCountNraQueue();
    }
}
