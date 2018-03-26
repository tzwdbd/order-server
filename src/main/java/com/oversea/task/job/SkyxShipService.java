package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.mapper.AutoOrderExpressDetailDAO;
import com.oversea.task.mapper.AutoOrderLoginDAO;
import com.oversea.task.mapper.AutoOrderScribeExpressDAO;
import com.oversea.task.mapper.ExchangeDefinitionDAO;
import com.oversea.task.mapper.GiftCardDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderCreditCardDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.OrderPayAccountDAO;
import com.oversea.task.mapper.OrderPayDetailDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.mapper.UserTradeAddressDAO;
import com.oversea.task.mapper.UserTradeDTLDAO;
import com.oversea.task.mapper.ZipcodeDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.OrderUtil;
import com.oversea.task.util.ThreeDES;
@Component
public class SkyxShipService implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	
	@Resource
    private RobotOrderDetailDAO robotOrderDetailDAO;

    @Resource
    private OrderAccountDAO orderAccountDAO;

    @Resource
    private OrderDeviceDAO orderDeviceDAO;
    
    @Resource
    private OrderCreditCardDAO orderCreditCardDAO;
    
    @Resource
    private UserTradeDTLDAO userTradeDTLDAO;

    @Resource
   	private UserTradeAddressDAO userTradeAddressDAO;
       
    @Resource 
   	private ZipcodeDAO zipcodeDAO;
    
    @Resource
    private OrderPayAccountDAO orderPayAccountDAO;
    
    @Resource
    private RpcServerProxy rpcServerProxy;
    
    @Resource
    private ExchangeDefinitionDAO exchangeDefinitionDAO;
    
    @Resource
    private MessageSender rabbitMqMessageSender;
    @Resource
    private CdnService cdnClient;
    @Resource
    private GiftCardDAO giftCardDAO;
    @Resource
    private OrderPayDetailDAO orderPayDetailDao;
    @Resource
    private AutoOrderLoginDAO autoOrderLoginDAO;
    @Resource
    private AutoOrderScribeExpressDAO autoOrderScribeExpressDAO;
    @Resource
    private AutoOrderExpressDetailDAO autoOrderExpressDetailDAO;
    public String getScanStatus(){
    	return AutoBuyStatus.AUTO_SCRIBE_ORDER_NOT_READY.getValue() +
    			"," + AutoBuyStatus.AUTO_PAY_SUCCESS.getValue();
    }
    
    public void run(){
    		log.error("==========handleShip run begin============");
	    	try{
		    	List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.getOrderDetailsForSpiderExpress(getScanStatus());
		    	List<List<RobotOrderDetail>> waittingForPlaceOrders = OrderUtil.getRobotOrderDetailListGroupByAccountId(orderDetails);
		    	for(List<RobotOrderDetail> list : waittingForPlaceOrders){
		    		if(list != null && list.size() > 0){
		    			Task task = new TaskDetail();
		    			RobotOrderDetail orderDetail = list.get(0);
	                OrderAccount account = orderAccountDAO.findById(orderDetail.getAccountId());
	                Integer deviceId = orderDetail.getDeviceId();
	                String ip = orderDeviceDAO.findById(deviceId).getDeviceIp();
	                Map<Long, String> asinCodeMap = new HashMap<Long, String>();
	                for (RobotOrderDetail detail : list) {
	                    long productEntityId = detail.getProductEntityId();
	                    String asinCode = robotOrderDetailDAO.getExternalProductEntityId(productEntityId);
	                    asinCodeMap.put(productEntityId, asinCode);
	                }
	                task.addParam("asinMap", asinCodeMap);
	                task.addParam("robotOrderDetails", list);
	                account.setLoginPwd(ThreeDES.decryptMode(account.getLoginPwd()));
	                task.addParam("account", account);
	                String yaml = insertAtTop("[ { echo: 'pre loading ..' },  { load: 'https://www.clinique.com/account/index.tmpl' },  { echo: 'check colorbox ..' },  { select: { ele: '#colorbox', attr: 'style', path: '/tmp/existColorBox' } },  { echo: '$/tmp/existColorBox' },  { contains: { path: '/tmp/existColorBox', search: 'block' } },  { if: '$1' },  { then:      [ { echo: 'exist colorbox btn .. pre close' },       { click: { ele: '#cboxClose' } },       { echo: 'close success' },       { wait: { type: 'time', millis: 10000 } } ] },  { echo: 'check foregroundBox' },  { exist: '#foreground-node' },  { if: '$1' },  { then:      [ { echo: 'exist foregroundBox btn .. pre close' },       { click: { ele: '#foreground-node .close-link' } },       { echo: 'close success' },       { wait: { type: 'time', millis: 10000 } } ] },  { exist: 'a[data-test-id=\"signOut\"]' },  { if: '$1' },  { then:      [ { echo: 'pre sign out' },       { click: { ele: 'a[data-test-id=\"signOut\"]' } },       { echo: 'sign out success' } ] },  { fill:      { ele: '#form--signin--field--EMAIL_ADDRESS',       value: '$/__sys/username' } },  { fill: { ele: '#form--signin--field--PASSWORD', value: '$/__sys/password' } },  { click: { ele: 'input[data-test-id=\"form_signin_continue\"]' } },  { wait: { type: 'time', millis: 5000 } },  { load: 'https://www.clinique.com/account/past_purchases.tmpl' },  { loop:      { in: { eles: '.past-purchases .outer-wrap .order' },       each:         [ { select: { ele: '.trans-id', under: '$e', path: '/tmp/mallOrder' } },          { echo: '$/tmp/mallOrder' },          { contains: { path: '/tmp/mallOrder', search: '$/__sys/mallorderno' } },          { if: '$1' },          { then:              [ { select:                   { ele: '.tracking-link-list',  under: '$e', path: '/tmp/mallState' } },{ contains: { path: '/tmp/mallState', search: 'Shipped' } },               { if: '$1' },               { then:                   [ { echo: 'mall shipped . pre get expressNo' },                    { select:                        { ele: 'li .tracking-link a',                         under: '$e',path: '/tmp/expressNo' } },{ echo: '$/tmp/expressNo' }, { break: '' } ] } ] } ] } } ]", account.getPayAccount(), account.getLoginPwd(),orderDetail.getMallOrderNo());
	                task.addParam("data", yaml);
	                task.setGroup(ip);
	                
	                TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
	                taskService.skyxShip(task);
		    		}
		    	}
		}catch(Exception e){
			log.error(e);
		}
    	
        log.error("==========SkyxShipService end============");
    }
    
    public  String insertAtTop(String script,String username,String password,String mallorderno){
		Yaml yaml = new Yaml();
		List<Map<String,Object>> yamlObject = null;
		
		yamlObject = yaml.load(script);	
		Map<String,Object> init = new HashMap<String,Object>();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("username", username);
		param.put("password", password);
		param.put("mallorderno", mallorderno);
		init.put("init", param);
		yamlObject.add(0, init);
		return yaml.dump(yamlObject);
	}
    
	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				log.error("Skyx订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流成功"+"ip:"+task.getGroup());
			}
		}else{
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				log.error("Skyx订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流失败");
			}
		}
	}

	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		TaskResult taskResult = (TaskResult)result;
		if(taskResult == null){
			return;
		}
        
        List<RobotOrderDetail> orderDetails = (List<RobotOrderDetail>) taskResult.getValue();
        log.error("SkyxShipService callbackResult订单号:"+orderDetails.get(0).getOrderNo()+"--->status="+orderDetails.get(0).getStatus()+"--->express="+orderDetails.get(0).getExpressNo()+"--->company="+orderDetails.get(0).getExpressCompany());
        
        for(RobotOrderDetail orderDetail:orderDetails){
        	RobotOrderDetail _orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(orderDetail.getId());
        	_orderDetail.setStatus(orderDetail.getStatus());
    		if(orderDetail.getStatus()==100){
        		if(!StringUtil.isBlank(orderDetail.getExpressNo())){
        			_orderDetail.setExpressNo(orderDetail.getExpressNo());
        		}
        		if(!StringUtil.isBlank(orderDetail.getExpressCompany())){
        			_orderDetail.setExpressCompany(orderDetail.getExpressCompany());
        		}
    		}
    		_orderDetail.setGmtModified(new Date());
			robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);
        }
	}
	
}
