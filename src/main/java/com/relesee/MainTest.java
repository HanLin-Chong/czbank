package com.relesee;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.ForeignFeedback;
import com.relesee.domains.MailParameters;
import com.relesee.domains.layim.Message;
import com.relesee.utils.ExcelUtil;
import com.relesee.utils.MailUtil;

import javax.mail.MessagingException;
import java.io.*;
import java.util.List;

public class MainTest {
    public static void main(String[] args){
        String s = "{\"username\":\"审核员(开发用账号)\",\"avatar\":\"ee0cd7d09bbee6fcd1d1d55c8dfaa040.jpg\",\"id\":\"a1\",\"type\":\"friend\",\"content\":\"阿卡is的各方Yuki阿萨德GV部分户口i\",\"mine\":false,\"fromid\":\"a1\",\"recipientId\":\"m1\"}";

        Message message = JSON.parseObject(s, Message.class);
        System.out.println(message.getRecipientId());
        /*Object object = JSON.parse(s);
        Message m = (Message) object;
        System.out.println(JSON.toJSONString(m));*/

        /*try {
            InputStream input = new FileInputStream("E:/毕业设计/文件区/外行反馈文件.xlsx");
            //ExcelUtil.printSheet(input, 0);
            List<ForeignFeedback> result = ExcelUtil.readFeedBack(input);
            for (ForeignFeedback temp:result){
                System.out.println(JSON.toJSONString(temp));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        /*MailParameters p = new MailParameters();
        p.setSenderAddress("1220347113@qq.com");
        p.setSenderPassword("nlvhvqysugdjjhad");
        p.setSenderAccount("1220347113@qq.com");
        p.setRecipientAddress("1220347113@qq.com");
        p.setMailSubject("境外账户申请《账户通知书》");
        p.setMailContent("您的申请已收到外行的反馈");
        p.setFileAbsolutePosition("E:/模型.pdf");
        try {
            MailUtil.send(p);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/


    }
}
