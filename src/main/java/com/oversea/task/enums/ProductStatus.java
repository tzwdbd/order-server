package com.oversea.task.enums;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.haitao.task.enums
 * @date 15/7/17 14:40
 */
public enum ProductStatus {
    UNPROCESS(-1, "未处理"),
    PROCESSING(-2, "处理中"),
    PREPARE(0, "爬取完成,等待发送"),
    SUCCESS(1, "同步成功"),
    FAIL(2, "处理失败"),
    INVALID(3, "数据失效"),
    TIMEOUT(4, "返回结果超时");

    private int value;
    private String name;

    ProductStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
