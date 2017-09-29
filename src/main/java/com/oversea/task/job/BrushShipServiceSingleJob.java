package com.oversea.task.job;

import com.oversea.task.enums.AutoBuyStatus;

public class BrushShipServiceSingleJob extends BrushShipServiceJob{
	public String getScanStatus(){
    	return ""+ AutoBuyStatus.AUTO_SCRIBE_NEED_SINGLE.getValue();
    }
}
