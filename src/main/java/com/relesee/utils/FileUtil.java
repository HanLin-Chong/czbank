package com.relesee.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileUtil {

    public static final Logger logger = Logger.getLogger(FileUtil.class);

    /**
     * 输入：InputStream, url（完整的绝对路径）
     * 输出：void
     * 功能：将InputStream写为文件
     *
     */
    public static void writeInputStreamToDirectory(InputStream inputStream, String location) throws IOException {
        FileOutputStream out = new FileOutputStream(location);
        int index;
        byte[] bytes = new byte[52428800];//1024?
        while ((index = inputStream.read(bytes)) != -1){
            out.write(bytes, 0, index);
            out.flush();
        }
        out.close();
        inputStream.close();
    }

    /**
     * 输入：url例:"(windows:C:)/a/b"
     * 输出：boolean是否操作成功。例：若a和b文件夹不存在，则创建，创建成功返回true
     * 功能：将url以/分隔，逐级遍历，如果不存在目录，则创建一个,url不能有文件名
     */
    public static void createDirIfNotExist(String url){
        File file = new File(url);
        if (file.exists()){
            logger.info("目录：（"+url+"）已经存在，无需创建");
        } else {
            logger.info("目录：（"+url+"）不存在，即将自动创建");
            file.mkdirs();
        }
    }

    public static String uuid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
