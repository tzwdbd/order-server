package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.GiftCardCheck;
public interface GiftCardCheckDAO {
	public void addGiftCardCheck(GiftCardCheck giftCardCheck);
	public void updateGiftCardCheckById(GiftCardCheck giftCardCheck);
	public void updateGiftCardCheckByDynamic(GiftCardCheck giftCardCheck);
	public List<GiftCardCheck> getGiftCardCheckById(Long id);
	public int countGiftCardCheckById(Long id);
}