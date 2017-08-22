package com.oversea.task.job;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.MallDefinition;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.Product;
import com.oversea.task.domain.ProductEntity;
import com.oversea.task.domain.Resources;
import com.oversea.task.mapper.DispatchManager;
import com.oversea.task.mapper.MallDefinitionDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderCreditCardDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.ProductDAO;
import com.oversea.task.mapper.ProductEntityDAO;
import com.oversea.task.mapper.ResourcesDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.DateUtil;

public class ProductOrderCheckServiceJob implements RpcCallback {

	private Logger log = Logger.getLogger(getClass());
	
	private final Integer CHECK_PRODUCT_MAX_NUM = 5; // 每次检查商品的数量
	
	@Resource
	private ResourcesDAO resourcesDAO;
	@Resource
	private ProductDAO productDAO;
	@Resource
	private MallDefinitionDAO mallDefinitionDAO;
	@Resource
    private OrderAccountDAO orderAccountDAO;
	@Resource
    private OrderCreditCardDAO orderCreditCardDAO;
	@Resource
    private OrderDeviceDAO orderDeviceDAO;
	@Resource
    private RpcServerProxy rpcServerProxy;
	@Resource
	private ProductEntityDAO productEntityDAO;
	@Resource
	private DispatchManager dispatchManager;
	
	public void run(){
		try{
			log.error("==========ProductOrderCheckServiceJob begin============");
			
			String time = DateUtil.ymdFormat(DateUtil.zerolizedTime(new Date()));
			List<Resources> resources = resourcesDAO.getSiteResourcesByTime(time);
			if(resources!=null && resources.size()>0){
				Resources resource = resources.get(0);
				excu(resource);
			}
			
			log.error("==========ProductOrderCheckServiceJob end============");
		}catch(Exception e){
			log.error("ProductOrderCheckServiceJob error :" , e);
		}
	}
	
	private void excu(Resources siteResource) throws ParseException{
		if(siteResource == null){
			log.error("ProductOrderCheckServiceJob error : siteResource is null");
			return;
		}
		
		Resources resourceByOper = new Resources();
		resourceByOper.setId(siteResource.getId());
		
		Date endTime = DateUtil.ymdString2Date(siteResource.getValue1());  // 判断是否符合条件
		String status = siteResource.getValue2();
		String dateStr = DateUtil.ymdFormat(new Date());
		if("1".equals(status)){
			Date now = DateUtil.ymdString2Date(dateStr);
			if(now.getTime() < endTime.getTime()){
				log.error("ProductOrderCheckServiceJob error : 未到第二天 endTime - " + endTime + " , currTime - " + now);
				return;
			}
		}else{
			MallDefinition mall = mallDefinitionDAO.getMallDefinitionByName(siteResource.getName());
			if(mall == null){
				log.error("ProductOrderCheckServiceJob error : 商城不存在-" + siteResource.getName());
				return; 
			}
			
			List<Product> productList = productDAO.getCheckProductByCondition(Long.valueOf(siteResource.getResValue().trim()), mall.getId());
			if(productList.size() == 0 || productList.size() < CHECK_PRODUCT_MAX_NUM){ // 最后一批 , 状态改为1、ID更为0、时间更新为当前时间
				resourceByOper.setResValue("0");
				resourceByOper.setValue1(dateStr);
				resourceByOper.setValue2("1");
				resourcesDAO.updateResourcesByDynamic(resourceByOper);
				
				if(productList.size() == 0){ 
					return;
				}
			}else{
				resourceByOper.setResValue(productList.get(CHECK_PRODUCT_MAX_NUM - 1).getId().toString()); // 更新资源表中商品id
				resourcesDAO.updateResourcesByDynamic(resourceByOper);
			}
			
			Map<Long,String> skuMap = new HashMap<>();
			for(Product product : productList){
				ProductEntity entity = productEntityDAO.getProductEntityById(product.getDefaultEntityId());
				String sku = dispatchManager.getProductSkuStr(entity);
				skuMap.put(product.getId(), sku);
			}
			
			String account = siteResource.getValue3();
			OrderAccount acc = orderAccountDAO.findById(Long.valueOf(account));
			if (acc == null) {
				log.error("ProductOrderCheckServiceJob error :" + account + "找不到对应的账号信息");
				return;
			}
			
			String ip = orderDeviceDAO.findById(siteResource.getPriority()).getDeviceIp();
			Task task = new TaskDetail();
			task.addParam("robotOrderDetails", productList);
			task.addParam("account", acc);
			task.addParam("mallName", siteResource.getName());
			task.addParam("skuMap", skuMap);
			task.addParam("originStatus", siteResource);
			task.setGroup(ip);
			
			TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
			taskService.productOrderCheckService(task);
		}
	}
	
	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		if(isSuccess){
			log.error("ProductOrderCheckServiceJob 请求成功,正在处理");
		}else{
			Task task = (Task)objs[0];
			Resources siteResource = (Resources)task.getParam("originStatus");
			resourcesDAO.updateResourcesByDynamic(siteResource);
			log.error("ProductOrderCheckServiceJob 请求失败,状态已经回滚为 :" + siteResource.toString());
		}
	}

	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		if(result == null){
			return;
		}
		TaskResult taskResult = (TaskResult)result;
		Boolean checkStatus = (Boolean)taskResult.getParam("checkStatus");
		if(checkStatus){
			@SuppressWarnings("unchecked")
			List<String> errorExternalEntityIdList  = (List<String>)taskResult.getParam("errorExternalId");
			if(errorExternalEntityIdList!=null && errorExternalEntityIdList.size()>0){
				String externalIds = StringUtils.join(errorExternalEntityIdList, ",");
				List<String> productIdList = productEntityDAO.getProductIdListByExternalIds(externalIds);
				String productIds = StringUtils.join(productIdList, ",");
				int row = productDAO.updateProductStatusByIds(productIds);
				if(row == errorExternalEntityIdList.size()){
					log.error("ProductOrderCheckServiceJob success : 更新成功，总共更新" + row + "条 , entityIds :" + externalIds);
				}else{
					log.error("ProductOrderCheckServiceJob success : 更新成功，小于应更新数 ，总共更新" + row + "条 ,实际应更新" + errorExternalEntityIdList.size()+ "条, entityIds :" + externalIds);
				}
			}
			log.error("ProductOrderCheckServiceJob success : 无更新商品");
		}
	}

}
