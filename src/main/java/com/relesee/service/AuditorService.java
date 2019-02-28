package com.relesee.service;

import com.relesee.dao.AuditorDao;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import com.relesee.utils.FileUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuditorService {

    private static final Logger logger = Logger.getLogger(AuditorService.class);

    @Autowired
    AuditorDao auditorDao;

    @Value("#{projectProperties['server.host']}")//projectProperties是在spring的配置文件中配置的一个bean
    private String SERVER_HOST;

    public Result<String> changeHeadPhoto(MultipartFile headPhoto, String userId, String realpath){
        Result<String> result = new Result();
        String DIRECTORY = realpath+"img/userHeadPhotos/"+userId;
        String PHOTO_URI = realpath+"img/userHeadPhotos/"+userId+"/"+headPhoto.getOriginalFilename();
        String PHOTO_WEB_URL = SERVER_HOST+"/img/userHeadPhotos/"+userId+"/"+headPhoto.getOriginalFilename();
        boolean fileWriteSuccess;
        try {
            FileUtil.createDirIfNotExist(DIRECTORY);
            FileUtil.writeInputStreamToDirectory(headPhoto.getInputStream(), PHOTO_URI);
            fileWriteSuccess = true;
        } catch (Exception e){
            logger.error("审核员在更改头像上传文件时发生错误", e);
            fileWriteSuccess = false;
        }
        int count = 0;
        if (fileWriteSuccess){
            count = auditorDao.updateHeadPhoto(PHOTO_WEB_URL, userId);
        }

        if (count == 1){
            result.setFlag(true);
            result.setMessage("头像更改成功");
            result.setResult(PHOTO_WEB_URL);
            ((User) SecurityUtils.getSubject().getPrincipal()).setHeadPhoto(PHOTO_WEB_URL);
        } else {
            result.setMessage("头像更改出错");
        }
        return result;
    }


}
