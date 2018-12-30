package com.relesee;


import com.alibaba.fastjson.JSON;
import com.relesee.dao.RolePermissionDao;
import com.relesee.dao.UserDao;
import com.relesee.domains.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application*.xml"})
public class UnitTest {

    @Autowired
    UserDao userDao;

    @Test
    public void doTest(){

        User user = userDao.selectUserById("m0000001");
        User user2 = userDao.selectUserByIdAndPass("m0000001","000000");
        System.out.println(JSON.toJSONString(user));
        System.out.println(JSON.toJSONString(user2));
    }



}
