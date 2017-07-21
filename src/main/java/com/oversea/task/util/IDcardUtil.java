package com.oversea.task.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonParser;

public class IDcardUtil {
	private static final String HOST = "https://dm-51.data.aliyun.com";
	private static final String PATH = "/rest/160601/ocr/ocr_idcard.json";
	private static final String METHOD = "POST";
	private static final String APP_CODE = "e44ba8ff81da457cb5079ff24eedc58e";
	private static final String FACE_FACE = "face";
	private static final String FACE_BACK = "back";
	private static Map<String, String> headers = new HashMap<String, String>();
	static {
		headers.put("Authorization", "APPCODE " + APP_CODE);
		headers.put("Content-Type", "application/json; charset=UTF-8");
	}

	public static String distinguishBack(String imageUrl, String face) {
		String imgBase64 = "";
        try {
        	URL url = new URL(imageUrl);
            InputStream in = url.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = in.read(bytes)) != -1) {
				out.write(bytes, 0, len);
			}
			imgBase64 = new String(Base64.encodeBase64(out.toByteArray()));
            in.close();
            out.close();
        } catch (IOException e) {
            return null;
        }
		Map<String, String> querys = new HashMap<String, String>();
		String bodys = "{\"inputs\": [{\"image\": {\"dataType\": 50,\"dataValue\": \"" + imgBase64
				+ "\"},\"configure\": {\"dataType\": 50,\"dataValue\": \"{\\\"side\\\":\\\"" + face + "\\\"}\"}}]}";
		try {
	    	HttpResponse response = HttpUtils.doPost(HOST, PATH, METHOD, headers, querys, bodys);
	    	return EntityUtils.toString(response.getEntity());
	    } catch (Exception e) {
			return null;
	    }
	}
	
	public static String getEndDateByIdCardBack(String cdnId) {
		String imageUrl = cdnId + "@1c_1e_1wh_250w";
		String result = distinguishBack(imageUrl, FACE_BACK);
		JsonParser parser = new JsonParser();
		String dataValue = parser.parse(result).getAsJsonObject().get("outputs").getAsJsonArray().get(0)
				.getAsJsonObject().get("outputValue").getAsJsonObject().get("dataValue").getAsString();
		String endDate = parser.parse(dataValue).getAsJsonObject().get("end_date").getAsString();
		return endDate;
	}
	
	public static void main(String[] args) {
		String date = getEndDateByIdCardBack("https://img.haihu.com/01170707991b3ccd7fa948aba7764b14ca29aff3.png");
		System.out.println(date);
	}

}
