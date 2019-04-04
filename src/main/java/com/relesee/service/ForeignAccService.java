package com.relesee.service;

import com.alibaba.fastjson.JSON;
import com.relesee.constant.ForeignApplicationStatus;
import com.relesee.dao.AmazonEUdao;
import com.relesee.dao.AmazonUSdao;
import com.relesee.dao.EbayApplicationDao;
import com.relesee.domains.*;
import com.relesee.utils.FileUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public Result<String> ebayAcc(EbayApplication ebayApplication, String realpath){

        String uuid = FileUtil.uuid();
        //手动注入uuid
        ebayApplication.setId(uuid);
        String DIRECTORY = realpath+"/files/ebay/"+uuid;

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
    public Result amazonUSacc(AmazonUSapplication amazonUS, String realpath){
        String uuid = FileUtil.uuid();
        //手动注入uuid
        amazonUS.setId(uuid);

        String DIRECTORY = realpath+"/files/amazon/us/"+uuid;

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
    public Result amazonEUacc(AmazonEUapplication amazonEU, String realpath){
        String uuid = FileUtil.uuid();
        //手动注入uuid
        amazonEU.setId(uuid);

        String DIRECTORY = realpath+"/files/amazon/eu/"+uuid;

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

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result<EbayApplication> getOneEbayApplication(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Result<EbayApplication> result = new Result();
        EbayApplication temp = new EbayApplication();
        temp.setStatus(ForeignApplicationStatus.LOCKED.getCode());
        temp.setAuditor(user.getUserId());
        EbayApplication locked = ebayApplicationDao.selectLocked(temp);
        if (locked == null){
            EbayApplication resultDo = ebayApplicationDao.selectOneEbayApplication();
            if (resultDo == null){
                result.setFlag(false);
                result.setMessage("没有可以处理的申请");
            } else {
                resultDo.setAuditor(user.getUserId());
                resultDo.setStatus(ForeignApplicationStatus.LOCKED.getCode());
                System.out.println(JSON.toJSONString(resultDo));
                ebayApplicationDao.updateStatus(resultDo);
                ebayApplicationDao.updateAuditor(resultDo);
                result.setFlag(true);
                result.setMessage("获取申请成功，即将加载");
                result.setResult(resultDo);
            }
        } else {
            result.setFlag(true);
            result.setMessage("您有锁定的申请未处理，即将加载");
            result.setResult(locked);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result ebayPassed(EbayApplication input, String realpath){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        input.setAuditor(user.getUserId());
        Result result = new Result();
        input.setStatus(ForeignApplicationStatus.SUBMITTED.getCode());

        //新增的打包下载功能
        /*String uuid = input.getId();
        String forderPath = realpath+"/files/amazon/us/"+uuid;*/

        int count = ebayApplicationDao.updateStatus(input);

        if (count == 1){
            result.setFlag(true);
            result.setMessage("通过申请成功，即将打包下载");
        } else {
            result.setFlag(false);
            result.setMessage("通过申请失败");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result ebayRefused(EbayApplication input){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        input.setAuditor(user.getUserId());
        Result result = new Result();
        input.setStatus(ForeignApplicationStatus.RETREAT.getCode());
        int count = ebayApplicationDao.updateStatus(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("申请已拒绝");
        } else {
            result.setFlag(false);
            result.setMessage("申请拒绝失败");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result updateEbayApplication(EbayApplication input){
        Result result = new Result();
        int count = ebayApplicationDao.updateApplication(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("更新ebay申请信息成功");
        } else {
            result.setFlag(false);
            result.setMessage("更新ebay申请信息出错");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result<AmazonUSapplication> getOneAmazonUSapplication(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Result<AmazonUSapplication> result = new Result();
        AmazonUSapplication temp = new AmazonUSapplication();
        temp.setStatus(ForeignApplicationStatus.LOCKED.getCode());
        temp.setAuditor(user.getUserId());
        AmazonUSapplication locked = amazonUSdao.selectLocked(temp);
        if (locked == null){
            AmazonUSapplication resultDo = amazonUSdao.selectOneApplication();
            if (resultDo == null){
                result.setFlag(false);
                result.setMessage("没有可以处理的申请");
            } else {
                resultDo.setAuditor(user.getUserId());
                resultDo.setStatus(ForeignApplicationStatus.LOCKED.getCode());
                amazonUSdao.updateStatus(resultDo);
                amazonUSdao.updateAuditor(resultDo);
                result.setFlag(true);
                result.setMessage("获取申请成功，即将加载");
                result.setResult(resultDo);
            }
        } else {
            result.setFlag(true);
            result.setMessage("您有锁定的申请未处理，即将加载");
            result.setResult(locked);
        }
        return result;
    }

    //TODO 别忘了把下面4个的auditor的id设置好
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result amazonUSpassed(AmazonUSapplication input){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        input.setAuditor(user.getUserId());
        Result result = new Result();
        input.setStatus(ForeignApplicationStatus.SUBMITTED.getCode());
        int count = amazonUSdao.updateStatus(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("通过申请成功，即将打包下载");
        } else {
            result.setFlag(false);
            result.setMessage("通过申请失败");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result amazonUSrefused(AmazonUSapplication input){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        input.setAuditor(user.getUserId());
        Result result = new Result();
        input.setStatus(ForeignApplicationStatus.RETREAT.getCode());
        int count = amazonUSdao.updateStatus(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("申请已拒绝");
        } else {
            result.setFlag(false);
            result.setMessage("申请拒绝失败");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result updateAmazonUSapplication(AmazonUSapplication input){
        Result result = new Result();
        int count = amazonUSdao.updateApplication(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("更新AmazonUS申请信息成功");
        } else {
            result.setFlag(false);
            result.setMessage("更新AmazonUS申请信息失败");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result getOneAmazonEUapplication(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Result<AmazonEUapplication> result = new Result();
        AmazonEUapplication temp = new AmazonEUapplication();
        temp.setStatus(ForeignApplicationStatus.LOCKED.getCode());
        temp.setAuditor(user.getUserId());
        AmazonEUapplication locked = amazonEUdao.selectLocked(temp);
        if (locked == null){
            AmazonEUapplication resultDo = amazonEUdao.selectOneApplication();
            if (resultDo == null){
               result.setFlag(false);
               result.setMessage("没有可以处理的申请");
            } else {
                resultDo.setAuditor(user.getUserId());
                resultDo.setStatus(ForeignApplicationStatus.LOCKED.getCode());
                amazonEUdao.updateStatus(resultDo);
                amazonEUdao.updateAuditor(resultDo);
                result.setFlag(true);
                result.setMessage("获取申请成功，即将加载");
                result.setResult(resultDo);
            }
        } else {
            result.setFlag(true);
            result.setMessage("您有锁定的申请未处理，即将加载");
            result.setResult(locked);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result amazonEUpassed(AmazonEUapplication input){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        input.setAuditor(user.getUserId());
        Result result = new Result();
        input.setStatus(ForeignApplicationStatus.SUBMITTED.getCode());
        int count = amazonEUdao.updateStatus(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("通过申请成功，即将打包下载");
        } else {
            result.setFlag(false);
            result.setMessage("通过申请失败");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result amazonEUrefused(AmazonEUapplication input){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        input.setAuditor(user.getUserId());
        Result result = new Result();
        input.setStatus(ForeignApplicationStatus.RETREAT.getCode());
        int count = amazonEUdao.updateStatus(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("申请已拒绝");
        } else {
            result.setFlag(false);
            result.setMessage("申请拒绝失败");
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result updateAmazonEUapplication(AmazonEUapplication input){
        input.valueFormat();
        Result result = new Result();
        int count = amazonEUdao.updateApplication(input);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("更新AmazonEU申请信息成功");
        } else {
            result.setFlag(false);
            result.setMessage("更新AmazonEU申请信息失败");
        }
        return result;
    }

    public LayTableResult<List<EbayApplication>> getEbayHistory(int begin, int size){
        LayTableResult<List<EbayApplication>> result = new LayTableResult();
        List<EbayApplication> list = ebayApplicationDao.selectPageHistory(begin, size);
        int count = ebayApplicationDao.selectCount();
        result.setCount(count);
        result.setMsg("获取数据成功");
        result.setCode(0);
        result.setData(list);
        return result;
    }

    public List<EbayApplication> searchEbayHistory(String key){
        return ebayApplicationDao.selectSearchHistory(key);
    }

    public LayTableResult<List<AmazonUSapplication>> getAmazonUShistory(int begin, int size){
        LayTableResult<List<AmazonUSapplication>> result = new LayTableResult();
        List<AmazonUSapplication> list = amazonUSdao.selectPageHistory(begin, size);
        int count = amazonUSdao.selectCount();
        result.setCount(count);
        result.setMsg("获取数据成功");
        result.setCode(0);
        result.setData(list);
        return result;
    }

    public List<AmazonUSapplication> searchAmazonUShistory(String key){
        return amazonUSdao.selectSearchHistory(key);
    }

    public LayTableResult<List<AmazonEUapplication>> getAmazonEUhistory(int begin, int size){
        LayTableResult<List<AmazonEUapplication>> result = new LayTableResult();
        List<AmazonEUapplication> list = amazonEUdao.selectHistory(begin, size);
        int count = amazonEUdao.selectCount();
        result.setCount(count);
        result.setMsg("获取数据成功");
        result.setCode(0);
        result.setData(list);
        return result;
    }

    public List<AmazonEUapplication> searchAmazonEUhistory(String key){
        return amazonEUdao.selectSearchHistory(key);
    }
}
