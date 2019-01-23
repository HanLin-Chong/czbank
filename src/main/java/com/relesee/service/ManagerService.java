package com.relesee.service;

import com.relesee.dao.ManagerDao;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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





}
