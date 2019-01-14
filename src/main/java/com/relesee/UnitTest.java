package com.relesee;


import com.alibaba.fastjson.JSON;
import com.relesee.dao.NraFileDao;
import com.relesee.domains.NraFile;
import com.relesee.domains.Result;
import com.relesee.service.ManagerService;
import com.relesee.service.NraQueueService;
import com.relesee.utils.FileUtil;
import com.relesee.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context.xml"})
public class UnitTest {

    @Autowired
    NraFileDao dao;

    @Test
    public void doTest() {
        //TODO 测试transactional
        List<NraFile> list = dao.selectNraQueue("",  null , null);
        System.out.println(JSON.toJSONString(list));
    }

}
