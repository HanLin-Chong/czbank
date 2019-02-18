package com.relesee.utils;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.awt.PdfPrinterGraphics2D;
import com.itextpdf.awt.geom.Line2D;
import com.itextpdf.awt.geom.Shape;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.relesee.domains.ForeignFeedback;
import com.relesee.domains.Result;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfUtil {

    private static final Logger logger = Logger.getLogger(PdfUtil.class);

    public static Result generateAccNotifications(ForeignFeedback feedback, String destination){
        Result result = new Result();
        Document document = new Document(PageSize.A4);//A4纸
        try {
            PdfWriter.getInstance(document, new FileOutputStream(destination));
        } catch (Exception e){
            e.printStackTrace();
            logger.error("生成账户通知书失败", e);
        }

        //字体设置
        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont("C:/Windows/Fonts/SIMHEI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font headCN1 = new Font(baseFont,12, Font.BOLD);//头左上角的中文(浙江稠州商业银行)
        Font headCN2 = new Font(baseFont, 8, Font.DEFAULTSIZE);//坐下中文(境外合作银行)
        Font headCN3 = new Font(baseFont, 10, Font.DEFAULTSIZE);//地址。。。。
        //下面三个是headCN对应的英文下标
        Font headEN1 = new Font(Font.FontFamily.SYMBOL, 5);
        Font headEN2 = new Font(Font.FontFamily.HELVETICA ,10);
        Font headEn3 = new Font(Font.FontFamily.TIMES_ROMAN, 11);


        return result;
    }

    public static void main(String[] args){
        String tempPath = "E:/模型.pdf";
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempPath));
            document.open();
            BaseFont baseFont = BaseFont.createFont("C:/Windows/Fonts/SIMHEI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            /*Font font = new Font(baseFont);
            document.add(new Paragraph("中文", font));
            document.close();*/

            Font headCN1 = new Font(baseFont, 30, Font.BOLD);//头左上角的中文(浙江稠州商业银行)
            Font headCN2 = new Font(baseFont, 15, Font.BOLD);//坐下中文(境外合作银行)
            Font headCN3 = new Font(baseFont, 14, Font.NORMAL);//地址。。。。
            //下面三个是headCN对应的英文下标
            Font headEN1 = new Font(baseFont, 12, Font.NORMAL);
            Font headEN2 = new Font(baseFont, 14);//Font.FontFamily.HELVETICA
            Font headEN3 = new Font(Font.FontFamily.TIMES_ROMAN, 15);
            //
            Font tableLeft = new Font(baseFont, 17, Font.NORMAL);
            Font tableRight = new Font(baseFont, 12, Font.NORMAL);
            Font bigTitleFont = new Font(baseFont, 40, Font.NORMAL);

            PdfDiv headDivLeft = new PdfDiv();
            headDivLeft.setFloatType(PdfDiv.FloatType.LEFT);
            headDivLeft.setPercentageWidth(0.5f);

            PdfDiv headDivRight = new PdfDiv();
            headDivRight.setPercentageWidth(1f);
            headDivRight.setFloatType(PdfDiv.FloatType.LEFT);
            //{
                Paragraph head = new Paragraph("浙江稠州商业银行", headCN1);
                headDivLeft.addElement(head);
                headDivLeft.addElement(new Paragraph("ZHEJIANG CHOUZHOU COMMERCIAL BANK CO.LTD", headEN1));

                Paragraph head2 = new Paragraph("境外合作银行", headCN2);
                head2.setSpacingBefore(20f);
                headDivLeft.addElement(head2);
                headEN2.setColor(BaseColor.RED);//temp
                Paragraph 临时1 = new Paragraph("IBOC(?第一个问号)", headEN2);
                headDivLeft.addElement(临时1);

                Paragraph head3 = new Paragraph("地址：浙江省义乌市义乌乐园东侧", headCN2);
                headDivRight.addElement(head3);
                headDivRight.addElement(new Paragraph("ADD:YIWULEYUAN EAST,JIANGBIN RD,YIWU,ZHEJIANG,CHINA", headEN3));
                headDivRight.addElement(new Paragraph("SWIFT BIC: CZCBCN2X", headEN3));
                headDivRight.addElement(new Paragraph("POST CODE: 322000", headEN3));
                headDivRight.addElement(new Paragraph("TEL: 0579-85551406", headEN3));

                //temp
                Paragraph 临时2 = new Paragraph("?第二个问号", headEN2);
                headDivRight.addElement(临时2);
            //}
            document.add(headDivLeft);
            document.add(headDivRight);

            //本来以为用spacingBefore还是setTop的，实例好久都不行，机智的我用div完成了
            PdfDiv spacingDiv = new PdfDiv();
            spacingDiv.setHeight(40f);
            document.add(spacingDiv);

            //这里画一条长长的横线,文档上说用什么Graphic，搞了半天搞不好，机智的我又用div完成了
            PdfDiv div = new PdfDiv();


            div.setHeight(3f);
            div.setBackgroundColor(BaseColor.BLACK);
            div.setPercentageWidth(1f);
            document.add(div);


            Paragraph bigTitle = new Paragraph("境外账户通知书", bigTitleFont);
            bigTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(bigTitle);

            PdfPTable table = new PdfPTable(2);
            table.setSpacingBefore(40f);
            table.setWidthPercentage(100f);
            table.setTotalWidth(new float[]{0.3f, 0.7f});

            PdfPCell cell_0_0 = new PdfPCell(new Paragraph("境外银行SWIFT代码", tableLeft));
            cell_0_0.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_0.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_0.setFixedHeight(40f);
            cell_0_0.setUseAscender(true);
            cell_0_0.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_0);
            PdfPCell cell_1_0 = new PdfPCell(new Paragraph("IBOCUS44", tableRight));
            cell_1_0.setUseAscender(true);
            cell_1_0.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_0);

            PdfPCell cell_0_1 = new PdfPCell(new Paragraph("境外账户名称", tableLeft));
            cell_0_1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_1.setFixedHeight(40f);
            cell_0_1.setUseAscender(true);
            cell_0_1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_1);
            PdfPCell cell_1_1 = new PdfPCell(new Paragraph("XIE MINGXING", tableRight));
            cell_1_1.setUseAscender(true);
            cell_1_1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_1);

            PdfPCell cell_0_2 = new PdfPCell(new Paragraph("境外银行账号", tableLeft));
            cell_0_2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_2.setFixedHeight(40f);
            cell_0_2.setUseAscender(true);
            cell_0_2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_2);
            PdfPCell cell_1_2 = new PdfPCell(new Paragraph("100030428", tableRight));
            cell_1_2.setUseAscender(true);
            cell_1_2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_2);

            PdfPCell cell_0_3 = new PdfPCell(new Paragraph("境外银行代码", tableLeft));
            cell_0_3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_3.setFixedHeight(40f);
            cell_0_3.setUseAscender(true);
            cell_0_3.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_3);
            PdfPCell cell_1_3 = new PdfPCell(new Paragraph("071006651", tableRight));
            cell_1_3.setUseAscender(true);
            cell_1_3.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_3);

            PdfPCell cell_0_4 = new PdfPCell(new Paragraph("账户币种", tableLeft));
            cell_0_4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_4.setFixedHeight(40f);
            cell_0_4.setUseAscender(true);
            cell_0_4.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_4);
            PdfPCell cell_1_4 = new PdfPCell(new Paragraph("美元/USD(？第三个问号)", tableRight));
            cell_1_4.setBackgroundColor(BaseColor.RED);
            cell_1_4.setUseAscender(true);
            cell_1_4.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_4);

            PdfPCell cell_0_5 = new PdfPCell(new Paragraph("稠州银行账号", tableLeft));
            cell_0_5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_5.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_5.setFixedHeight(40f);
            cell_0_5.setUseAscender(true);
            cell_0_5.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_5);
            PdfPCell cell_1_5 = new PdfPCell(new Paragraph("15603142110300015088", tableRight));
            cell_1_5.setUseAscender(true);
            cell_1_5.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_5);

            PdfPCell cell_0_6 = new PdfPCell(new Paragraph("稠州账户户名", tableLeft));
            cell_0_6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_6.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_6.setFixedHeight(40f);
            cell_0_6.setUseAscender(true);
            cell_0_6.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_6);
            PdfPCell cell_1_6 = new PdfPCell(new Paragraph("程世海", tableRight));
            cell_1_6.setUseAscender(true);
            cell_1_6.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_6);

            PdfPCell cell_0_7 = new PdfPCell(new Paragraph("备注", tableLeft));
            cell_0_7.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_0_7.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell_0_7.setFixedHeight(40f);
            cell_0_7.setUseAscender(true);
            cell_0_7.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_0_7);
            PdfPCell cell_1_7 = new PdfPCell(new Paragraph("Sccneb(?第四个问号)", tableRight));
            cell_1_7.setBackgroundColor(BaseColor.RED);
            cell_1_7.setUseAscender(true);
            cell_1_7.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell_1_7);

            document.add(table);
            document.close();
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
