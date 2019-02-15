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
        EbayApplication input = new EbayApplication();
        input.setId("temp");
        input.setPaypalId("3");
        input.setBusinessName("3");
        input.setShopUrl("3");
        input.setShopName("3");
        input.setApplicantName("3");
        input.setApplicantId("3");
        input.setRecipientAcc("3");
        input.setRecipientAccName("3");
        input.setRecipientId("3");
        input.setAddress("3");
        input.setManagerName("3");
        input.setManagerDepartment("3");
        input.setManagerId("3");
        dao.updateApplication(input);

    }


}
