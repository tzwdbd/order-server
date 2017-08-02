package com.oversea.task.mapper.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.GiftCard;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.GiftCardDAO;

@Repository
public class GiftCardDAOImpl extends BaseDao implements GiftCardDAO {
	@Override
	public List<GiftCard> getUnusedGiftCard(String siteName,int limitNum,int num){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("siteName", siteName);
        map.put("num", num);
        map.put("limitNum", limitNum);
		return getSqlSession().selectList("GiftCardMapper.getUnusedGiftCard",map);
	}
	
	@Override
	public int updateGiftCardProcessStatus(Long id,String isProcess){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("isProcess", isProcess);
        map.put("id", id);
		return getSqlSession().update("GiftCardMapper.updateGiftCardProcessStatus",map);
	}
	
	@Override
	public GiftCard getGiftCardById(Long id){
		return getSqlSession().selectOne("GiftCardMapper.getGiftCardById",id);
	}
	
	@Override
	public int updateGiftCard(GiftCard giftCard){
		return getSqlSession().update("GiftCardMapper.updateGiftCard",giftCard);
	}

	@Override
	public List<GiftCard> getNowRechargeGiftCard(String siteName,int num) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("siteName", siteName);
        map.put("num", num);
		return getSqlSession().selectList("GiftCardMapper.getNowRechargeGiftCard",map);
	}

	@Override
	public List<GiftCard> getPassWordCard(String siteName) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("siteName", siteName);
		return getSqlSession().selectList("GiftCardMapper.getPassWordCard",map);
	}

	@Override
	public List<GiftCard> getGiftCardAccount(int accountId, Date time) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("accountId", accountId);
        map.put("time", time);
		return getSqlSession().selectList("GiftCardMapper.getGiftCardAccount",map);
	}

}
