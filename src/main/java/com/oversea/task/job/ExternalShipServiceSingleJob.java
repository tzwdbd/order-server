package com.oversea.task.job;

import com.oversea.task.enums.AutoBuyStatus;

public class ExternalShipServiceSingleJob extends ExternalShipServiceJob{
	public String getScanStatus(){
    	return ""+ AutoBuyStatus.AUTO_SCRIBE_NEED_SINGLE.getValue();
    }
}
