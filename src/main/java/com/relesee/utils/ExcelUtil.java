package com.relesee.utils;

import com.relesee.domains.ForeignFeedback;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {

    private static final Logger logger = Logger.getLogger(ExcelUtil.class);

    private static DecimalFormat numberFormatter = new DecimalFormat("0");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");


    public static List<ForeignFeedback> readFeedBack(InputStream inputStream){
        List<ForeignFeedback> result = new ArrayList();
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            logger.error("读取反馈文件IO错误",e);
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            logger.error("读取反馈文件发生错误，可能不是Excel文件"+e);
        }
        Sheet sheet = wb.getSheetAt(0);
        for (Row row:sheet){
            //跳过第一行元数据
            if (row.getRowNum() == 0){continue;}
            ForeignFeedback foreignFeedback = new ForeignFeedback();
            for (Cell cell:row){
                int index = cell.getColumnIndex();
                switch(index){
                    case ForeignFeedback.FOUND_TIME:
                        foreignFeedback.setFoundTime(getCellAsString(cell));
                        break;
                    case ForeignFeedback.ACC_NAME:
                        foreignFeedback.setAccName(getCellAsString(cell));
                        break;
                    case ForeignFeedback.ACC:
                        foreignFeedback.setAcc(getCellAsString(cell));
                        break;
                    case ForeignFeedback.CURRENCY:
                        foreignFeedback.setCurrency(getCellAsString(cell));
                        break;
                    case ForeignFeedback.FOREIGN_BANK:
                        foreignFeedback.setForeignBank(getCellAsString(cell));
                        break;
                    case ForeignFeedback.ROUTING:
                        foreignFeedback.setRouting(getCellAsString(cell));
                        break;
                    case ForeignFeedback.POST_SCRIPT:
                        foreignFeedback.setPostScript(getCellAsString(cell));
                        break;
                    case ForeignFeedback.NOTE:
                        foreignFeedback.setNote(getCellAsString(cell));
                        break;
                }
            }
            result.add(foreignFeedback);
        }
        return result;
    }

    /**
     * 这个方法聚集了poi操作的精髓，话我就说到这里
     * @param inputStream
     * @param whichSheet 从0开始
     */
    public static void printSheet(InputStream inputStream, int whichSheet){
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            logger.error("读取反馈文件IO错误",e);
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            logger.error("读取反馈文件发生错误，可能不是Excel文件"+e);
        }
        Sheet sheet = wb.getSheetAt(whichSheet);
        System.out.println(sheet);
        System.out.println(sheet.getSheetName());
        for (Row row:sheet){
            if (row.getRowNum() == 0){continue;}
            System.out.println("--------------------------------------------------------------------");
            String temp = "";
            //跳过第一行元数据
            for (Cell cell:row){
                //万物皆String，不管啥类型，都转为String
                String cellValue = "";
                int cellType = cell.getCellType();
                switch (cellType){
                    case Cell.CELL_TYPE_STRING:
                        cellValue = cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)){
                            //cellValue = dateFormatter.format(cell.getNumericCellValue());
                            Date date = cell.getDateCellValue();
                            cellValue = dateFormatter.format(date);
                        } else {
                            cellValue = numberFormatter.format(cell.getNumericCellValue());
                        }
                        break;
                }
                temp += cellValue+" | ";
            }
            System.out.println(temp);
        }
    }

    public static String getCellAsString(Cell cell){
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
