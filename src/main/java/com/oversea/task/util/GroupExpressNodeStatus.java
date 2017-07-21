package com.oversea.task.util;

public enum GroupExpressNodeStatus {

	    ZHONG_TONG("zhongtong","中通速递"),
	    SHUN_FENG("shunfeng","顺丰速运"),
        YUAN_TONG("yuantong","圆通速递"),
	    SHEN_TONG("shentong","申通速递"),
	    YOU_ZHENG("ems","邮政速递"),
	    RU_FENG_DA("rufengda","如风达")
	    ;
	    String code;
	    private String name;
	    
	    GroupExpressNodeStatus(String code,String name){
	        this.code = code;
	        this.name = name;
	    }

	    public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }
	    
		public static GroupExpressNodeStatus getGroupExpressNodeStatusByCode(String name){
			if(name==null){
				return null;
			}
			for(GroupExpressNodeStatus ls: GroupExpressNodeStatus.values()) {
				if(name.contains(ls.getName())) {
					return ls;
				}
			}
			return null;
		}
}
