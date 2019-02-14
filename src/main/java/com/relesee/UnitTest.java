package com.relesee;


import com.alibaba.fastjson.JSON;
import com.relesee.constant.NraPriorityStatus;
import com.relesee.dao.EbayApplicationDao;
import com.relesee.dao.NraFileDao;
import com.relesee.domains.EbayApplication;
import com.relesee.domains.NraFile;
import com.relesee.domains.Result;
import com.relesee.service.AuditorService;
import com.relesee.service.ForeignAccService;
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
    EbayApplicationDao dao;

    @Test
    public void doTest() {
        EbayApplication e = dao.selectOneEbayApplication();
        System.out.println(e);
    }


}
