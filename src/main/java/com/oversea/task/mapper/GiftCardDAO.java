package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.GiftCard;

public interface GiftCardDAO {
	
	public List<GiftCard> getUnusedGiftCard(String siteName,int limitNum,int num);
	
	public int updateGiftCardProcessStatus(Long id,String status);
	
	public GiftCard getGiftCardById(Long id);
	
	public int updateGiftCard(GiftCard giftCard);
	
	public List<GiftCard> getNowRechargeGiftCard(String siteName,int num);
	public List<GiftCard> getPassWordCard(String siteName);
	
}
