package com.relesee.service;

import com.alibaba.fastjson.JSON;
import com.relesee.constant.NraPriorityStatus;
import com.relesee.constant.NraStatus;
import com.relesee.dao.NraFileDao;
import com.relesee.domains.LayTableResult;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Nra的操作需要很好的并发控制，，所以除了只包含查询的服务，其它的全都用SERIALIZABLE隔离级别
 */

@Service
public class NraQueueService {

    private static final Logger logger = Logger.getLogger(NraQueueService.class);

    //private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("#{projectProperties['output.rootpath']}")//projectProperties是在spring的配置文件中配置的一个bean
    private String OUTPUT_ROOT_PATH;

    @Autowired
    NraFileDao nraFileDao;

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
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
     * 队列重新编号、状态码调整工作都在同一个update标签中完成
     * @param id
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public synchronized Result revokeNraFile(String id){
        Result result = new Result();
        int count = nraFileDao.deleteNraFileById(id, NraStatus.CANCELED.getStatusCode());
        if (count >= 1){
            nraFileDao.serializeQueue();
            result.setFlag(true);
            result.setMessage("撤销申请成功");

        } else {
            result.setFlag(false);
            result.setMessage("撤销申请失败");
        }
        return result;
    }

    //申请插队，此操作只是提交申请，不需要改变队列号
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public Result applyPriority(String id){
        Result result = new Result();
        int count = nraFileDao.updatePriorityStatus(id, NraPriorityStatus.WAITING.getCode());
        if (count == 1){
            result.setFlag(true);
            result.setMessage("插队申请申请已提交");
        } else {
            result.setFlag(false);
            result.setMessage("插队申请提交失败");
        }
        return result;
    }

    /**
     * nra文件的上传是manager特有的功能
     * @param file
     * @param nraFile
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
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
        System.out.println(fileWriteSuccess);
        System.out.println(insert);
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

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public NraFile getNraFileById(String id) {
        NraFile nraFile = nraFileDao.selectNraFileById(id);

        return nraFile;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    @RequiresPermissions( {"auditorController"} )
    public Result<NraFile> nraRefuse(NraFile nraFile){
        nraFile.setStatusCode(NraStatus.REFUSED.getStatusCode());

        Result<NraFile> result = new Result();
        int count = nraFileDao.updateStatusRefused(nraFile);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("文件："+nraFile.getFileName()+" 已拒绝");
            result.setResult(nraFile);
            //队列重新编号
            nraFileDao.serializeQueue();
        } else {
            result.setFlag(false);
            result.setMessage("文件："+nraFile.getFileName()+" 在拒绝申请时失败");
            result.setResult(nraFile);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    @RequiresPermissions( {"auditorController"} )
    public Result<NraFile> nraPass(NraFile nraFile){
        nraFile.setStatusCode(NraStatus.PASS.getStatusCode());

        Result<NraFile> result = new Result();
        int count = nraFileDao.updateStatusPassed(nraFile);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("文件："+nraFile.getFileName()+" 审核通过");
            result.setResult(nraFile);
            //队列重新编号
            nraFileDao.serializeQueue();
        } else {
            result.setFlag(false);
            result.setMessage("文件："+nraFile.getFileName()+" 在通过审核时失败");
            result.setResult(nraFile);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    @RequiresPermissions( {"auditorController"} )
    public Result<NraFile> nraRelease(NraFile nraFile){
        nraFile.setStatusCode(NraStatus.QUEUING.getStatusCode());

        Result<NraFile> result = new Result();
        int count = nraFileDao.updateStatusQueuing(nraFile);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("文件："+nraFile.getFileName()+" 已经释放");
            result.setResult(nraFile);
            //队列重新编号
            nraFileDao.serializeQueue();
        } else {
            result.setFlag(false);
            result.setMessage("文件："+nraFile.getFileName()+" 释放失败");
            result.setResult(nraFile);
        }
        return result;
    }

    /**
     * 所有审核员都要通过此方法批量取出NRA申请，每次取出的数量由审核员自己决定
     * 先将NRA文件的状态标为已下载
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    @RequiresPermissions( {"auditorController"} )
    public synchronized Result<List<NraFile>> getForAuditor(int amount){
        Result<List<NraFile>> result = new Result();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //先判断是否有锁定的资源未处理
        List<NraFile> history = nraFileDao.selectLocked(user.getUserId());

        if (history.size() > 0){
            logger.fatal("非法操作，在申请未处理完时获取");
            result.setFlag(false);
            result.setMessage("非法操作，当前账号仍有被锁定的申请未处理，请刷新页面重试");
            //result.setResult(history);
        } else {

            //（逻辑上）锁定资源,锁定后由于只是把状态码从0调为1，队列号并没有改变，故无需重编号
            int count = nraFileDao.updateNraAuditor(amount, user.getUserId(), NraStatus.LOCKED.getStatusCode());

            //取出锁定的申请
            List<NraFile> list = nraFileDao.selectLocked(user.getUserId());
            if (count <= amount && count >= 1){
                result.setFlag(true);
                result.setMessage("success");
                result.setResult(list);
            } else if (count == 0){
                result.setFlag(true);
                result.setMessage("null");
                result.setResult(list);
            } else {
                result.setFlag(false);
                result.setMessage("获取申请失败");
                result.setResult(list);
            }
        }
        return result;
    }

    @RequiresPermissions( {"auditorController"} )
    public Result<List<NraFile>> getLockedFiles(){
        Result<List<NraFile>> result = new Result();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        List<NraFile> list = nraFileDao.selectLocked(user.getUserId());
        result.setResult(list);
        return result;
    }

    @RequiresPermissions( {"auditorController"} )
    public Result<List<NraFile>> getPriorityApplications(){
        Result<List<NraFile>> result = new Result();
        List<NraFile> list = nraFileDao.selectPriorityApplications();
        result.setResult(list);
        return result;
    }

    @RequiresPermissions( {"auditorController"} )
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public synchronized Result priorityPass(NraFile nraFile){
        Result result = new Result();
        nraFile.setPriorityStatus(NraPriorityStatus.PRIORITY.getCode());
        int count = nraFileDao.updatePriorityPass(nraFile);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("插队成功");
            nraFileDao.queueNoMoveForPriority(nraFile.getId());
            //待socket做好后需要通知相应的客户经理
        } else {
            result.setFlag(false);
            result.setMessage("插队操作失败");
        }
        return result;
    }

    @RequiresPermissions( {"auditorController"} )
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
    public synchronized Result priorityRefused(NraFile nraFile){
        Result result = new Result();
        nraFile.setPriorityStatus(NraPriorityStatus.REFUSED.getCode());
        int count = nraFileDao.updatePriorityRefused(nraFile);
        if (count == 1){
            result.setFlag(true);
            result.setMessage("拒绝插队成功");
        } else {
            result.setFlag(false);
            result.setMessage("拒绝插队操作失败");
        }
        return result;
    }

    public LayTableResult<List<NraFile>> getHistoryAuditor(int begin, int limit){
        LayTableResult<List<NraFile>> result = new LayTableResult();
        List<NraFile> data = nraFileDao.selectHistoryAuditor(begin, limit);
        int count = nraFileDao.selectCountAll();
        result.setCode(0);
        result.setMsg("获取数据成功");
        result.setData(data);
        result.setCount(count);
        return result;
    }

    public List<NraFile> searchHistory(String key){
        return nraFileDao.selectSearchHistoryAuditor(key);
    }
}
