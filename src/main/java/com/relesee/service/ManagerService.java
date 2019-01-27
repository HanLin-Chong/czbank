package com.relesee.service;

import com.relesee.dao.ManagerDao;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import com.relesee.utils.FileUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ManagerService {

    private static final Logger logger = Logger.getLogger(ManagerService.class);

    @Autowired
    ManagerDao managerDao;

    public Result advice(String content){
        Result result = new Result();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        int count = managerDao.insertAdvice(content, user.getUserId());
        if (count == 1){
            result.setFlag(true);
        } else {
            result.setFlag(false);
        }
        return result;
    }

    public Result changeHeadPhoto(MultipartFile headPhoto, String userId, String realpath){

        Result<String> result = new Result();
        String DIRECTORY = realpath+"img/userHeadPhotos/"+userId;
        String PHOTO_URI = realpath+"img/userHeadPhotos/"+userId+"/"+headPhoto.getOriginalFilename();
        boolean fileWriteSuccess;
        try {
            FileUtil.createDirIfNotExist(DIRECTORY);
            FileUtil.writeInputStreamToDirectory(headPhoto.getInputStream(), PHOTO_URI);
            fileWriteSuccess = true;
        } catch (Exception e){
            logger.error("经理在更改头像上传文件时发生错误", e);
            fileWriteSuccess = false;
        }
        int count = 0;
        if (fileWriteSuccess){
            count = managerDao.updateHeadPhoto(headPhoto.getOriginalFilename(), userId);
        }

        if (count == 1){
            result.setFlag(true);
            result.setMessage("头像更改成功");
            result.setResult(headPhoto.getOriginalFilename());
            ((User) SecurityUtils.getSubject().getPrincipal()).setHeadPhoto(headPhoto.getOriginalFilename());
        } else {
            result.setMessage("头像更改出错");
        }
        return result;
    }



}
