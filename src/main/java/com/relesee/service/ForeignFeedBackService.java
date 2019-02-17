package com.relesee.service;

import com.relesee.domains.Result;
import com.relesee.utils.ExcelUtil;
import com.relesee.utils.FileUtil;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ForeignFeedBackService {

    private static final Logger logger = Logger.getLogger(ForeignFeedBackService.class);

    public Result process(InputStream stream){
        Result result = new Result();
        ExcelUtil.readFeedBack(stream);

        return result;
    }
}
