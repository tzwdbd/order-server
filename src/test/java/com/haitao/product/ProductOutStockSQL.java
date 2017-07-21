package com.haitao.product;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author fengjian
 * @version V1.0
 * @title: oversea
 * @Package com.haitao.product
 * @Description:
 * @date 16/7/12
 */
public class ProductOutStockSQL {
    private static String FILEPATH = "/Users/fengjian/Downloads/out_stock.txt";

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static void main(String[] args) throws IOException {
//        File dfile = new File(FILEPATH);
//        String category = null;
//        FileInputStream fis = new FileInputStream(dfile);
//        List<String> list = IOUtils.readLines(fis);
//        for (String line : list) {
//            String[] clos = line.split("\t");
//            System.out.println("update product set status=4 where brand_id=" + clos[0] + " and category_id = " + clos[1] + ";");
//        }
    }
}
