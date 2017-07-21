//package com.haitao;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.io.IOUtils;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
///**
// * @author fengjian
// * @version V1.0
// * @title: sea-online
// * @Package com.haitao
// * @Description: 扫描程序asos
// * @date 16/3/11 17:40
// */
//public class ScanAsosFileList2SQL {
//
//    private static String FILEPATH = "/Users/fengjian/Downloads/asos-shoe/girl/";
//
//    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//
//    public static void main(String[] args) throws IOException {
//        File file = new File(FILEPATH);
//        for (String fileName : file.list()) {
//            File dfile = new File(FILEPATH + fileName);
//            String category = null;
//            fileName = fileName.replace(".json", "");
//            if (fileName.contains(".")) {
//                category = fileName.split("\\.")[0];
//            } else {
//                category = fileName;
//            }
//            FileInputStream fis = new FileInputStream(dfile);
//            List<String> list = IOUtils.readLines(fis);
//            StringBuilder sb = new StringBuilder();
//            for (String line : list) {
//                sb.append(line);
//            }
//            Map<String, Object> map = gson.fromJson(sb.toString(), Map.class);
//            for (Map.Entry<String, Object> entity : map.entrySet()) {
//                List<String> urllist = (List<String>) entity.getValue();
//                for (String url : urllist) {
////                    String uuid = UUIDUtil.random();
//                	String uuid = "";
//                    System.out.println("INSERT INTO `haitao_product_info` " +
//                            "(platform,item_id,home_url,create_time,update_time,status,category,priority) " +
//                            "values('asos','" + uuid + "','" + url + "',now(),now(),-1," + category + ",1);");
//                }
//            }
//        }
//    }
//}
