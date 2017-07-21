package com.oversea.task.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {

    private static Log log = LogFactory.getLog(FileUtil.class);

    public static List<String> readFile(String filePath) {
        try {
            return readFile(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            log.error("文件读取出错", e);
        }
        return null;
    }

    public static List<String> readFile(InputStream inputStream) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            List<String> list = new ArrayList<String>(50000);
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
            return list;
        } catch (IOException e) {
            log.error("文件读取出错", e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static void writeFile(byte[] dataArr, File file) {
        File parentFile = file.getParentFile();
        if (!parentFile.exists())
            parentFile.mkdirs();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(dataArr);
        } catch (IOException e) {
            log.error("文件写入出错", e);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
            }
        }
    }
}
