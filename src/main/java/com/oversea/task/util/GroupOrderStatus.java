package com.oversea.task.util;


public enum GroupOrderStatus {

	WAIT_PAY(10,"等待付款"),
	PAY_SUCCESS(20,"已支付"),
	PLACE_SUCCESS(30,"已下单"),
	GATE_REVIEW(40,"海关审核"),
	REVIEW_SUCCESS(50,"审核通过"),
	GOODS_SEND(60,"已发货"),
	ORDER_FINISH(70,"已签收"),

	
    PAY_FAILURE(11,"支付失败"),
	APPLY_REFUND(12,"退款中"),
	REFUND_SUCCESS(13,"已退款"),

    ;
    
    private int value;
    private String name;
    
    GroupOrderStatus(int value,String name){
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
    
	public static GroupOrderStatus getGroupOrderStatusByCode(Integer value){
		if(value==null){
			return null;
		}
		for(GroupOrderStatus ls: GroupOrderStatus.values()) {
			if(value.equals(ls.getValue())) {
				return ls;
			}
		}
		return null;
	}
}
