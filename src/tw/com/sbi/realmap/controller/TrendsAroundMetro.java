package tw.com.sbi.realmap.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.gson.Gson;

import tw.com.sbi.common.controller.CommonMethod;

public class TrendsAroundMetro extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TrendsAroundMetro.class);
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/text");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String action = CommonMethod.null2str(request.getParameter("action"));
		Map<String, String> requestMap = CommonMethod.requestParameter(request);
		try {
			if("select_metro_from_and_to".equals(action)){	
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(CommonMethod.null2str(requestMap.get("metro_name")));
				request_VO.put(CommonMethod.null2str(requestMap.get("to_and_from_type")));
				String outcome = new CommonMethod(getServletConfig())
						.useMysqlDBString("call sp_select_metro_people_trend(?,?)",request_VO);
				response.getWriter().write(outcome);
				
			}else{
				logger.debug("[TransToJSP]");
				response.sendRedirect("./index.jsp");
				
			}
		} catch (Exception e) {
			response.getWriter().write("fail!");
			e.printStackTrace(System.err);
			logger.debug("full-range-log",e);
		}	
	}
}