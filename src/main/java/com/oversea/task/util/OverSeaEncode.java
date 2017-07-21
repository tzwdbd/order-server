package com.oversea.task.util;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.common.encode
 * @date 15/7/17 15:25
 */
public class OverSeaEncode {

    public final static String siteCode = "order";
    private final static String siteKey = "3434dac17e8102313ef11afa6a763583";

    public static String encode(String parameters) {
        if (parameters == null) {
            return null;
        }

        String sourceStr = siteCode + "&&" + parameters + "&&" + siteKey;
        return Md5Encrypt.md5(sourceStr, "UTF-8").toLowerCase();
    }
}
