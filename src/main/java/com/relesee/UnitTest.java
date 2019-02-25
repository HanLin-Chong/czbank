package com.relesee;


import com.alibaba.fastjson.JSON;
import com.relesee.constant.NraPriorityStatus;
import com.relesee.dao.EbayApplicationDao;
import com.relesee.dao.ForeignFeedbackDao;
import com.relesee.dao.NraFileDao;
import com.relesee.domains.*;
import com.relesee.service.AuditorService;
import com.relesee.service.ForeignAccService;
import com.relesee.service.ManagerService;
import com.relesee.service.NraQueueService;
import com.relesee.utils.ExcelUtil;
import com.relesee.utils.FileUtil;
import com.relesee.utils.MailUtil;
import com.relesee.utils.RedisUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context.xml"})
public class UnitTest {

    @Autowired
    NraFileDao dao;

    @Test
    public void doTest() {

        MailParameters mailParameters = new MailParameters();
        mailParameters.setSenderAccount("1220347113@qq.com");
        mailParameters.setSenderPassword("nlvhvqysugdjjhad");
        mailParameters.setSenderAddress("1220347113@qq.com");
        mailParameters.setMailSubject("稠州银行境外合作银行账户申请《账户通知书》");
        mailParameters.setMailContent("您此前提交的账户申请现已收到境外银行的反馈结果，详情请咨询您的客户经理，如果您之前并没有提交过任何申请，请忽略此邮件");
        mailParameters.setFileAbsolutePosition("D:/你猜我是什么.rar");
        mailParameters.setRecipientAddress("1220347113@qq.com");
        try {
            for (int i = 1; i <= 10; i++){
                MailUtil.send(mailParameters);
            }

        } catch (MessagingException e) {
            e.printStackTrace();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }

    }

}
