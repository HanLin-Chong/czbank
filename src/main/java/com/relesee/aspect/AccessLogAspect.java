package com.relesee.aspect;

import com.relesee.dao.AccessLogDao;
import com.relesee.domains.AccessLog;
import com.relesee.domains.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccessLogAspect {

    private static final Logger logger = Logger.getLogger(AccessLogAspect.class);

    @Autowired
    AccessLogDao accessLogDao;

    @After("execution(String com.relesee.controller.PageController.distribute(..))")
    public Object distributeLog(ProceedingJoinPoint joinPoint) throws Throwable{
        AccessLog log = new AccessLog();

        String s = (String) joinPoint.proceed();
        log.setPageName(s);
        log.setUserId(((User) SecurityUtils.getSubject().getPrincipal()).getUserId());
        int count = accessLogDao.insertLog(log);
        if (count != 1){
            logger.info("分发器日志切面在向数据库写入日志时失败，日志信息：（pageName:"+s+",userId:"+log.getUserId()+"）");
        }
        return s;
    }

    @After("execution(String com.relesee.controller.PageController.*Page(..))")
    public Object PageLog(ProceedingJoinPoint joinPoint) throws Throwable{
        AccessLog log = new AccessLog();

        String s = (String) joinPoint.proceed();
        log.setPageName(s);
        log.setUserId(((User) SecurityUtils.getSubject().getPrincipal()).getUserId());
        int count = accessLogDao.insertLog(log);
        if (count != 1){
            logger.info("页面转发器在向数据库写入日志时失败，日志信息：（pageName:"+s+",userId:"+log.getUserId()+"）");
        }
        return s;
    }

}
