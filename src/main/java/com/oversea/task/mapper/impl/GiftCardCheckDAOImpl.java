package com.oversea.task.mapper.impl;

import com.oversea.task.domain.GiftCardCheck;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.GiftCardCheckDAO;

import java.util.List;

import org.springframework.stereotype.Repository;
@Repository
public class GiftCardCheckDAOImpl extends BaseDao implements GiftCardCheckDAO {
	public void addGiftCardCheck(GiftCardCheck giftCardCheck) {
		getSqlSession().insert("addGiftCardCheck",giftCardCheck);
}
	public void updateGiftCardCheckById(GiftCardCheck giftCardCheck) {
		getSqlSession().update("updateGiftCardCheckById",giftCardCheck);
}
	public void updateGiftCardCheckByDynamic(GiftCardCheck giftCardCheck) {
		getSqlSession().update("updateGiftCardCheckByDynamic",giftCardCheck);
}
	public List<GiftCardCheck> getGiftCardCheckById(Long id) {
		return getSqlSession().selectList("getGiftCardCheckById", id);
}
	public int countGiftCardCheckById(Long id) {
		return (Integer)getSqlSession().selectOne("countGiftCardCheckById",id);
}
}