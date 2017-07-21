package com.oversea.task.util;

import java.io.*;
import java.util.Properties;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.task.util
 * @Description: 文件持久化工具
 * @date 16/1/12 17:56
 */
public class FilePropertiesUtils {

    private static File seoContextFile = new File(System.getProperty("user.home"), "task_config_context.properties");

    private static Properties CONTEXT = new Properties();

    /**
     * 从文件中读取数据
     */
    static {
        FileInputStream stream = null;
        try {
            if (seoContextFile.exists()) {
                stream = new FileInputStream(seoContextFile);
                CONTEXT.load(stream);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String getPropString(String key) {
        if (key == null) {
            return null;
        }

        return CONTEXT.getProperty(key);
    }

    public static Long getPropLong(String key) {
        String result = getPropString(key);
        if (result == null) {
            return 0l;
        } else {
            return Long.valueOf(result);
        }
    }

    public static Integer getPropInt(String key) {
        String result = getPropString(key);
        if (result == null) {
            return 0;
        } else {
            return Integer.valueOf(result);
        }
    }

    public static Boolean getPropBoolean(String key) {
        String result = getPropString(key);
        if (result == null) {
            return false;
        } else {
            return Boolean.valueOf(result);
        }
    }

    public synchronized static void update(String key, Object value) {
        CONTEXT.put(key, String.valueOf(value));
        saveContext(key);
    }

    /**
     * 将数据持久化到文件中
     */
    private static void saveContext(String prop) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(seoContextFile);
            CONTEXT.store(stream, String.format("属性%s发生了修改", prop));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public static void main(String[] args) {
        FilePropertiesUtils.update("haitao", 1);
        System.out.println(FilePropertiesUtils.getPropInt("haitao"));
    }
}
