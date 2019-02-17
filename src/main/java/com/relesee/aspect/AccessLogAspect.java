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
 *
 *
 */
public class AccessLogAspect {

/*    private static final Logger logger = Logger.getLogger(AccessLogAspect.class);

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
    }*/


}
