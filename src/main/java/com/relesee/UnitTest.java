package com.relesee;


import com.alibaba.fastjson.JSON;
import com.relesee.constant.NraPriorityStatus;
import com.relesee.constant.UserStatus;
import com.relesee.dao.EbayApplicationDao;
import com.relesee.dao.ForeignFeedbackDao;
import com.relesee.dao.NraFileDao;
import com.relesee.dao.UserDao;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context.xml"})
public class UnitTest {



    private static DecimalFormat numberFormatter = new DecimalFormat("0");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    UserDao userDao;

    @Test
    public void doTest() {
        String path = "E:/9900-1导出数据.xls";

        Workbook workbook = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            System.out.println(fileInputStream.available());
            workbook = WorkbookFactory.create(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row:sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            String s = "";
            User user = new User();
            user.setPassword("000000");
            user.setEmail("请尽快完善邮箱信息");

            user.setUserState(UserStatus.NORMAL.getCode());
            user.setPhone("请尽快完善手机信息");
            user.setHeadPhoto("http://120.79.246.240:8080/img/czyh.png");

            user.setSign("编辑");
            for (Cell cell : row) {
                s += g(cell);
                int index = cell.getColumnIndex();
                switch (index) {
                    case 0:
                        //机构编号
                        user.setDeptId(g(cell));
                        break;
                    case 1:
                        //机构名称
                        user.setDeptName(g(cell));
                        break;
                    case 2:
                        //工号
                        user.setUserId(g(cell));
                        break;
                    case 3:
                        //姓名
                        user.setUserName(g(cell));
                        break;
                    case 4:
                        //角色
                        if ("客户经理".equals(g(cell))) {
                            //continue;
                            user.setRole("false");
                        }
                        if ("审核员".equals(g(cell))) {
                            user.setRole("manager");
                        }
                        break;
                }
                //System.out.println(index);

            }
            if ("false".equals(user.getRole())){
                continue;
            } else {
                String userId = user.getUserId();
                userId = "m"+userId;
                user.setUserId(userId);
                userDao.temp(user);
                System.out.println(JSON.toJSONString(user));
            }

        }
/*        for (Row row:sheet){
            if (row.getRowNum() == 0){continue;}
            String s = "";
            User user = new User();
            user.setPassword("000000");
            user.setEmail("请尽快完善邮箱信息");

            user.setUserState(UserStatus.NORMAL.getCode());
            user.setPhone("请尽快完善手机信息");
            user.setHeadPhoto("http://120.79.246.240:8080/img/czyh.png");

            user.setSign("编辑");
            for (Cell cell:row){
                s += g(cell);
                int index = cell.getColumnIndex();
                switch (index){
                    case 0:
                        //机构编号
                        user.setDeptId(g(cell));
                        break;
                    case 1:
                        //机构名称
                        user.setDeptName(g(cell));
                        break;
                    case 2:
                        //工号
                        user.setUserId(g(cell));
                        break;
                    case 3:
                        //姓名
                        user.setUserName(g(cell));
                        break;
                    case 4:
                        //角色
                        if ("客户经理".equals(g(cell))){
                            user.setRole("manager");
                        }
                        if ("审核员".equals(g(cell))){
                            user.setRole("auditor");
                        }
                        break;
                }
                System.out.println(index);

            }
            userDao.temp(user);
            System.out.println(s);
        }*/

    }

    public static String g(Cell cell){//getCellAsString
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            if (DateUtil.isCellDateFormatted(cell)){
                Date date = cell.getDateCellValue();
                return dateFormatter.format(date);
            } else {
                return numberFormatter.format(cell.getNumericCellValue());
            }
        } else if (cell.getCellType() == Cell.CELL_TYPE_STRING){
            return cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK){
            return "";
        } else {
            return "单元格格式非字符(String)或数值(Numeric)";
        }
    }
}
