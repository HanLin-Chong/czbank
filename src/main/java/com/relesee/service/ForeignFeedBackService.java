package com.relesee.service;

import com.itextpdf.text.DocumentException;
import com.relesee.dao.AmazonUSdao;
import com.relesee.dao.EbayApplicationDao;
import com.relesee.dao.ForeignFeedbackDao;
import com.relesee.domains.*;
import com.relesee.socket.SocketHandler;
import com.relesee.utils.ExcelUtil;
import com.relesee.utils.FileUtil;
import com.relesee.utils.MailUtil;
import com.relesee.utils.PdfUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ForeignFeedBackService {

    private static final Logger logger = Logger.getLogger(ForeignFeedBackService.class);

    //private static final String PDF_OUTPUT_DIR = "";

    private static final SimpleDateFormat dateformatter = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    EbayApplicationDao ebayApplicationDao;

    @Autowired
    AmazonUSdao amazonUSdao;

    @Autowired
    ForeignFeedbackDao foreignFeedbackDao;
    /**
     * 长耗时任务
     * process的流程：
     * 0.取出数据库中相关的信息
     * 1.将反馈文件读取为List
     * 2.根据List生成账户通知书.Pdf
     * 3。给相应的客户发送邮件
     * 4.以上操作完成后将反馈插入数据库通知客户经理
     * *若需要记录日志，用aop来做
     * 接受者的邮箱：ebay使用paypalId，amazonUS使用amazonMail
     * @param stream
     * @return
     */
    public Result process(InputStream stream, String requestBasePath){
        String outputPath = requestBasePath+"files/foreignfeedback/"+dateformatter.format(new Date())+"/";
        FileUtil.createDirIfNotExist(outputPath);
        Result result = new Result();
        //1.将反馈文件读取为List
        List<ForeignFeedback> list = ExcelUtil.readFeedBack(stream);
        if (list.size() >= 1){
            result.setFlag(true);
            result.setMessage("共读取"+list.size()+"条反馈");
        } else {
            result.setFlag(false);
            result.setMessage("读取反馈文件出错或反馈文件为空");
        }
        //2.生成通知书
        try {
            for (ForeignFeedback feedback:list){
                //根据账户名称配对数据库中的申请信息
                    //先判断是ebay的还是AmazonUS的
                String accName = feedback.getAccName();
                String businessName = "";
                String managerId = "";
                String recipientMail = "";
                PdfParameters pdfParameters = new PdfParameters();

                if (StringUtils.isBlank(feedback.getNote())){
                    //若备注为空，是AmazonUS的
                    List<AmazonUSapplication> applications =  amazonUSdao.selectMachedFeedback(accName);
                    if (applications.size() == 1){
                        AmazonUSapplication application = applications.get(0);
                        businessName = application.getBusinessName();

                        managerId = application.getManagerId();
                        recipientMail = application.getAmazonMail();
                        pdfParameters.setCzbankAcc(application.getRecipientAcc());
                        pdfParameters.setCzbankAccName(application.getRecipientAccName());
                    } else {
                        logger.error("存在两个一样的境外账户名，系统无法处理，报此错误");
                    }
                } else {
                    //若备注不为空，是Ebay的
                    List<EbayApplication> applications = ebayApplicationDao.selectMachedFeedback(accName);
                    if (applications.size() == 1){
                        EbayApplication application = applications.get(0);
                        businessName = application.getBusinessName();
                        managerId = application.getManagerId();
                        recipientMail = application.getPaypalId();
                        pdfParameters.setCzbankAcc(application.getRecipientAcc());
                        pdfParameters.setCzbankAccName(application.getRecipientAccName());
                    } else {
                        logger.error("存在两个一样的境外账户，系统无法处理，报此错误");
                    }
                }
                //p.setForeignFullName();TODO
                //p.setForeignSwiftCode();TODO
                pdfParameters.setDestination(outputPath+"境外账户通知书.pdf");
                PdfUtil.generateAccNotifications(feedback, pdfParameters);

                //3.发送邮件
                MailParameters mailParameters = new MailParameters();
                mailParameters.setSenderAccount("1220347113@qq.com");
                mailParameters.setSenderPassword("nlvhvqysugdjjhad");
                mailParameters.setSenderAddress("1220347113@qq.com");
                mailParameters.setMailSubject("稠州银行境外合作银行账户申请《账户通知书》");
                mailParameters.setMailContent("您此前提交的账户申请现已收到境外银行的反馈结果，详情请咨询您的客户经理，如果您之前并没有提交过任何申请，请忽略此邮件");
                mailParameters.setFileAbsolutePosition(outputPath+"境外账户通知书.pdf");
                mailParameters.setRecipientAddress(recipientMail);
                try {
                    MailUtil.send(mailParameters);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    result.setFlag(false);
                    result.setMessage("处理失败");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    result.setFlag(false);
                    result.setMessage("处理失败");
                }
                //通知客户经理
                Map<String, WebSocketSession> sessions =  SocketHandler.getSessions();
                String payload = "{system:true,content:\"您的申请已经收到外行反馈\"}";
                sessions.get(managerId).sendMessage(new TextMessage(payload));
            }
            int count = foreignFeedbackDao.insertFeedback(list);
            if (count >= 1){
                result.setFlag(true);
                result.setMessage("处理成功，共"+list.size()+"条数据");
            } else {
                result.setFlag(false);
                result.setMessage("处理失败，条数小于1");
            }



        } catch (IOException e) {
            e.printStackTrace();
            result.setFlag(false);
            result.setMessage("处理失败");
        } catch (DocumentException e) {
            e.printStackTrace();
            result.setFlag(false);
            result.setMessage("处理失败");
        }



        return result;
    }
}
