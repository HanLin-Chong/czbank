package com.relesee.aspect;

import com.relesee.dao.AccessLogDao;
import com.relesee.domains.AccessLog;
import com.relesee.domains.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
/**
 * 在对PageController进行环切的时候失败，同时导致一些路径无法访问，
 * 于是决定不使用代理，使用最笨的函数式方法解决。。
 */
public class AccessLogAspect {

    private static final Logger logger = Logger.getLogger(AccessLogAspect.class);

    @Autowired
    AccessLogDao accessLogDao;

    @Around("execution(* com.relesee.service.*.*(..))")
    public Object ServiceLog(ProceedingJoinPoint joinPoint){
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Object object = null;
        try {
            object = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        joinPoint.getTarget().getClass().getName();
        try {
            logger.info("["+user.getUserId()+"-"+user.getUserName()+"]:"+className+"."+methodName);
        } catch (Exception e){
            logger.info("[未登录]:"+className+"."+methodName);
        }

        return object;
    }

    /*public void distributeLog(String path){
        AccessLog log = new AccessLog();


        log.setPageName(path);
        log.setUserId(((User) SecurityUtils.getSubject().getPrincipal()).getUserId());
        int count = accessLogDao.insertLog(log);
        if (count != 1){
            logger.info("分发器日志切面在向数据库写入日志时失败，日志信息：（pageName:"+path+",userId:"+log.getUserId()+"）");
        }

    }

    public void pageLog(String path){
        AccessLog log = new AccessLog();


        log.setPageName(path);
        log.setUserId(((User) SecurityUtils.getSubject().getPrincipal()).getUserId());
        int count = accessLogDao.insertLog(log);
        if (count != 1){
            logger.info("页面转发器在向数据库写入日志时失败，日志信息：（pageName:"+path+",userId:"+log.getUserId()+"）");
        }

    }*/

}
