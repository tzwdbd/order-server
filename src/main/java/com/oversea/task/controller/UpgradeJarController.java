package com.oversea.task.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.oversea.task.update.ClientJarVersionProcessor;

/**
 * @author 
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.task.controller
 * @Description: 客户端更新
 * @date 15/12/6 23:22
 */

@Controller
public class UpgradeJarController {

    private Log log = LogFactory.getLog(getClass());
    @Value("${client.jar.folder.path}")
    private String clientJarFolderPath;

    @Value("${client.zip.name}")
    private String clientZipName;
    
    @Value("${client.jar.download.url}")
    private String clientDownloadUrl;
    
    @Resource
    private ClientJarVersionProcessor clientJarVersionProcessor;
    
    private static final int BATCH_COUNT = 12;//一批更新12个
    
    private static final int BATCH_TIME = 25000;//每隔25秒更新一批
    

    private volatile AtomicReference<byte[]> zipRef = new AtomicReference<byte[]>();
    private volatile AtomicLong lastModify = new AtomicLong(100);
    
    private AtomicLong clientNum = new AtomicLong(0);
    private volatile long lastUpdateTime;
    
    public String getClientJarFolderPath() {
		return clientJarFolderPath;
	}

	public void setClientJarFolderPath(String clientJarFolderPath) {
		this.clientJarFolderPath = clientJarFolderPath;
	}

	public String getClientZipName() {
		return clientZipName;
	}

	public void setClientZipName(String clientZipName) {
		this.clientZipName = clientZipName;
	}

	/**
     * 下载更新jar包进程
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/upgrade_jar", method = RequestMethod.GET)
    public void upgradeJar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clientJarPath = clientJarFolderPath + clientZipName;
        log.error("更新的路径为:"+clientJarPath);
        File file = new File(clientJarPath);
        if (file.exists() && file.length() > 0) {
            log.error("[服务]最后更新时间:" + new Date(lastModify.get()));
            log.error("[文件]最后更新时间:" + new Date(file.lastModified()));
            if (zipRef.get() == null || lastModify.get() != file.lastModified()) {
                synchronized (this) {
                    if (zipRef.get() == null || lastModify.get() != file.lastModified()) {
                        byte[] bytes = getFileBytes(file);
                        if (bytes != null) {
                            zipRef.set(bytes);
                            lastModify.set(file.lastModified());
                        }
                    }
                }
            }
        }else{
        	log.error("更新的路径为flie不存在:");
        }
        response.getOutputStream().write(zipRef.get());
    }
    
	@RequestMapping(value = "/versionCheck", method = RequestMethod.GET)
	public void versionCheck(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String version = request.getParameter("version");
		boolean isUpdate = false;
		if(!StringUtils.isEmpty(version) && !StringUtils.isEmpty(clientJarVersionProcessor.getClientJarVersion())){
			if(!version.equalsIgnoreCase(clientJarVersionProcessor.getClientJarVersion())){
				if(clientNum.get() < BATCH_COUNT){
					log.error("正在更新的ip:" + getIpAddress(request));
					clientNum.addAndGet(1);
					lastUpdateTime = System.currentTimeMillis();
					isUpdate = true;
				}else{
					if(System.currentTimeMillis() - lastUpdateTime >= BATCH_TIME){
						clientNum.set(0);
					}
				}
			}
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("isUpdate", String.valueOf(isUpdate));
		resultMap.put("clientDownloadUrl", clientDownloadUrl);
		writeJson(resultMap, response);
	}
	
	/**
	 * 反写json信息
	 * @param response
	 * @param jsonMap
	 * @throws IOException
	 */
	public String writeJson(Object responses, HttpServletResponse response) {
		String json = new Gson().toJson(responses);
		response.setContentType("text/json; charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println(json);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			out.close();
		}
		return null;
	}
    public byte[] getFileBytes(File downloadFile) {
    	FileInputStream fis = null;
    	try{
    		fis = new FileInputStream(downloadFile);
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		byte[] data = new byte[8192];
    		int len = 0;
        	while((len = fis.read(data)) != -1){
        		bos.write(data, 0, len);
        	}
        	return bos.toByteArray();
    	}catch(Exception e){
    		log.error("读取更新包文件出错:" + e);
    	}
    	finally{
    		if(fis != null){
    			try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			fis = null;
    		}
    	}
        return null;
    }
    
    public static String getIpAddress(HttpServletRequest request) { 
        String ip = request.getHeader("x-forwarded-for"); 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("WL-Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("HTTP_CLIENT_IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getRemoteAddr(); 
        } 
        return ip; 
      }
}