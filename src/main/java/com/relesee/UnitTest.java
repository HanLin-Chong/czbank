package com.relesee;


import com.alibaba.fastjson.JSON;
import com.relesee.constant.NraQueueJumpingStatus;
import com.relesee.dao.NraFileDao;
import com.relesee.domains.NraFile;
import com.relesee.domains.Result;
import com.relesee.service.AuditorService;
import com.relesee.service.ManagerService;
import com.relesee.service.NraQueueService;
import com.relesee.utils.FileUtil;
import com.relesee.utils.RedisUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context.xml"})
public class UnitTest {

    @Autowired
    NraFileDao dao;

    @Test
    public void doTest() {
        NraFile nraFile = new NraFile();
        nraFile.setStatusCode(0);
        nraFile.setId("e31bc99fe18548869b60f0de7e615537");
        int count = dao.updateStatusPassed(nraFile);
        System.out.println(count);
    }

}
