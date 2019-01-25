package com.relesee.service;

import com.relesee.dao.AmazonEUdao;
import com.relesee.dao.AmazonUSdao;
import com.relesee.dao.EbayApplicationDao;
import com.relesee.domains.AmazonEUapplication;
import com.relesee.domains.AmazonUSapplication;
import com.relesee.domains.EbayApplication;
import com.relesee.domains.Result;
import com.relesee.utils.FileUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ForeignAccService {

    private static final Logger logger = Logger.getLogger(ForeignAccService.class);

    @Value("#{projectProperties['output.rootpath']}")//projectProperties是在spring的配置文件中配置的一个bean
    private String OUTPUT_ROOT_PATH;

    @Autowired
    EbayApplicationDao ebayApplicationDao;

    @Autowired
    AmazonUSdao amazonUSdao;

    @Autowired
    AmazonEUdao amazonEUdao;

    /**
     * 将上传的两个文件保存在：OUTPUT_ROOT_PATH/files/ebay/{uuid}文件夹内，名字保持原名即可。
     * 先将文件写到磁盘后再将模板信息存入数据库
     * @param ebayApplication
     * @return
     */
    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result<String> ebayAcc(EbayApplication ebayApplication){

        String uuid = FileUtil.uuid();
        //手动注入uuid
        ebayApplication.setId(uuid);
        String DIRECTORY = OUTPUT_ROOT_PATH+"/files/ebay/"+uuid;

        MultipartFile applicationFile = ebayApplication.getApplicationFile();
        MultipartFile transactionRecord = ebayApplication.getTransactionRecord();
        boolean fileWriteSuccess = false;
        try {
            FileUtil.createDirIfNotExist(DIRECTORY);
            ebayApplication.setApplicationFileName(applicationFile.getOriginalFilename());
            FileUtil.writeInputStreamToDirectory(applicationFile.getInputStream(), DIRECTORY+"/"+applicationFile.getOriginalFilename());
            if (transactionRecord != null){
                ebayApplication.setTransactionRecordName(transactionRecord.getOriginalFilename());
                FileUtil.writeInputStreamToDirectory(transactionRecord.getInputStream(), DIRECTORY+"/"+transactionRecord.getOriginalFilename());
            }
            fileWriteSuccess = true;

        } catch (Exception e){
            e.printStackTrace();
            logger.error("ebay文件上传出错",e);
            fileWriteSuccess = false;
        }
        int count = 0;
        if (fileWriteSuccess){
            count = ebayApplicationDao.insertApplication(ebayApplication);
        }
        Result<String> result = new Result();
        if (count == 1){
            result.setFlag(true);
            result.setMessage("申请已提交");
        } else {
            result.setMessage("申请提交失败");
            result.setFlag(false);
        }
        return result;
    }

    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result amazonUSacc(AmazonUSapplication amazonUS){
        String uuid = FileUtil.uuid();
        //手动注入uuid
        amazonUS.setId(uuid);

        String DIRECTORY = OUTPUT_ROOT_PATH+"/files/amazon/us/"+uuid;

        MultipartFile applicationFile = amazonUS.getApplicationFile();
        MultipartFile transactionRecord = amazonUS.getTransactionRecord();
        boolean fileWriteSuccess = false;
        try {
            FileUtil.createDirIfNotExist(DIRECTORY);
            amazonUS.setApplicationFileName(applicationFile.getOriginalFilename());
            FileUtil.writeInputStreamToDirectory(applicationFile.getInputStream(), DIRECTORY+"/"+applicationFile.getOriginalFilename());
            if (transactionRecord != null){
                amazonUS.setTransactionRecordName(transactionRecord.getOriginalFilename());
                FileUtil.writeInputStreamToDirectory(transactionRecord.getInputStream(), DIRECTORY+"/"+transactionRecord.getOriginalFilename());
            }
            fileWriteSuccess = true;
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Amazon US文件上传出错",e);
            fileWriteSuccess = false;
        }
        int count = 0;
        if (fileWriteSuccess){
            count = amazonUSdao.insertApplication(amazonUS);
        }
        Result<String> result = new Result();
        if (count == 1){
            result.setFlag(true);
            result.setMessage("申请已提交");
        } else {
            result.setMessage("申请提交失败");
            result.setFlag(false);
        }
        return result;
    }

    @Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result amazonEUacc(AmazonEUapplication amazonEU){
        String uuid = FileUtil.uuid();
        //手动注入uuid
        amazonEU.setId(uuid);

        String DIRECTORY = OUTPUT_ROOT_PATH+"/files/amazon/eu/"+uuid;

        MultipartFile applicationFile = amazonEU.getApplicationFile();
        boolean fileWriteSuccess = false;
        try {
            FileUtil.createDirIfNotExist(DIRECTORY);
            amazonEU.setApplicationFileName(applicationFile.getOriginalFilename());
            FileUtil.writeInputStreamToDirectory(applicationFile.getInputStream(), DIRECTORY+"/"+applicationFile.getOriginalFilename());

            fileWriteSuccess = true;
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Amazon EU文件上传出错",e);
            fileWriteSuccess = false;
        }
        int count = 0;
        if (fileWriteSuccess){
            count = amazonEUdao.insertApplication(amazonEU);
        }
        Result<String> result = new Result();
        if (count == 1){
            result.setFlag(true);
            result.setMessage("申请已提交");
        } else {
            result.setMessage("申请提交失败");
            result.setFlag(false);
        }
        return result;
    }

}
