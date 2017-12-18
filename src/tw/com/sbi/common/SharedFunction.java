package tw.com.sbi.common;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Enumeration;
import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

//import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.json.JSONArray;
//import org.json.JSONObject;

import com.google.gson.Gson;

public class SharedFunction {
	private static Logger logger = LogManager.getLogger(SharedFunction.class);
	//統一把 String List 轉換成 List 同時紀錄
	public static Map<String, String> getFormatedRqstPara(HttpServletRequest request){
		
		Map<String, String[]> oriRqstMap = request.getParameterMap();
		Map<String, String> destRqstMap = new HashMap<String, String>();
		
		for (Map.Entry<String, String[]> entry : oriRqstMap.entrySet()) {
			destRqstMap.put(entry.getKey(), String.join(",",entry.getValue()));
		}
		logger.info("rqstPara: "+new Gson().toJson(oriRqstMap));
		
		return destRqstMap;
	}
	
}