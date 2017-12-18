package tw.com.sbi.finModel.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tw.com.sbi.vo.FincaseVO;
import tw.com.sbi.vo.FinsimuVO;

public class FinModel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(FinModel.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("FinModel doPost");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		try {
			String userId = request.getSession().getAttribute("user_id").toString();
			String groupId = request.getSession().getAttribute("group_id").toString();
			String role = request.getSession().getAttribute("role").toString();
			String action = request.getParameter("action");
	//		FincaseVO message = null;
			FinModelService finModelService = null;
			Gson gson = null;
			if("gen_d3js".equals(action)){
				logger.debug("action: gen_d3js");
				
				String caseId = request.getParameter("case_id");
				String caseIdbase64 = new String(Base64.encodeBase64String(caseId.getBytes()));
				String url = getServletConfig().getServletContext().getInitParameter("pythonwebservice")+"/finance/type=YmFsYW5jZQ==&case="+caseIdbase64;
				//System.out.println(url);
				HttpGet httpRequest = new HttpGet(url);
	        	HttpClient client = HttpClientBuilder.create().build();
	        	HttpResponse httpResponse;
	        	try {
	        		StringBuffer result = new StringBuffer();
	        		httpResponse = client.execute(httpRequest);
	    			int responseCode = httpResponse.getStatusLine().getStatusCode();
	    
	    	    	if(responseCode==200){
	    	    		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
	        	    	String line = "";
	        	    	while ((line = rd.readLine()) != null) {
	        	    		result.append(line);
	        	    	}	
	    	    		//Gson gson2 = new Gson();
	    	    		//String answer = gson2.fromJson(result.toString(), List.class);
	    	    		//System.out.println(result.toString());
	    	    		response.getWriter().write(result.toString());
	    	    	} else {
	    	    		System.out.println("responseCode: " + responseCode);
	    	    		System.out.println("fail to get data");
	    	    	}    	    	
	    		}catch (Exception e){
	    			logger.debug("full-range-log",e);
	//    			System.out.println(e.toString());
	    		}
	        	
				int i = 0;
				if(i == 0){
					return;
				}
				
	    		String incomeStr = "SELECT f_date AS date, SUM(amount) AS pv FROM tb_finsimu WHERE case_id = '" + caseId + "' AND amount > 0 GROUP BY f_date ORDER BY f_date";
	    		String outlayStr = "SELECT f_date AS date, ABS(SUM(amount)) AS pv FROM tb_finsimu WHERE case_id = '" + caseId + "' AND amount < 0 GROUP BY f_date ORDER BY f_date";
	    		String detailDataStr = "SELECT f_date AS date, amount AS pv FROM tb_finsimu WHERE case_id = '" + caseId + "' ORDER BY f_date";
	    		String fincaseStr = "SELECT Amount, safety_money, create_date FROM tb_fincase WHERE case_id = '" + caseId + "'";
	    		String totalIncomeStr = "SELECT SUM(amount) AS totalIncome FROM tb_finsimu WHERE case_id = '" + caseId + "' AND amount > 0";
	    		String totalOutlayStr = "SELECT IF(SUM(amount) IS NULL,0,SUM(amount)) AS totalOutlay FROM tb_finsimu WHERE case_id = '" + caseId + "' AND amount < 0 ";
			
	    		finModelService = new FinModelService();
				JSONArray incomeJsonArray = finModelService.queryDataInJsonArray(incomeStr);
	    		JSONArray outlayJsonArray = finModelService.queryDataInJsonArray(outlayStr); 
	    		JSONArray detailDataJsonArray = finModelService.queryDataInJsonArray(detailDataStr); 
	    		JSONArray fincaseJsonArray = finModelService.queryDataInJsonArray(fincaseStr); 
				String totalIncome = finModelService.queryDataInStr(totalIncomeStr);
				String totalOutlay = finModelService.queryDataInStr(totalOutlayStr);
	
				JSONArray jsonArray = new JSONArray();
				JSONObject jsonObject = new JSONObject(); 
				jsonObject.put("income", incomeJsonArray);
				jsonArray.put(jsonObject);
				jsonObject = new JSONObject();
				jsonObject.put("outlay", outlayJsonArray);
				jsonArray.put(jsonObject);
				jsonObject = new JSONObject();
				jsonObject.put("detailData", detailDataJsonArray);
				jsonArray.put(jsonObject);
				jsonObject = new JSONObject();
				jsonObject.put("fincase", fincaseJsonArray);
				jsonArray.put(jsonObject);
				jsonObject = new JSONObject();
				jsonObject.put("totalIncome", totalIncome);
				jsonArray.put(jsonObject);
				jsonObject = new JSONObject();
				jsonObject.put("totalOutlay", totalOutlay);
				jsonArray.put(jsonObject);
				logger.debug("ret: "+jsonArray.toString());
				response.getWriter().write(jsonArray.toString());
			}
			if("gen_d3js_bath".equals(action)){
				logger.debug("action: gen_d3js_bath");
				
				String caseId = request.getParameter("case_id");
				String caseIdbase64 = new String(Base64.encodeBase64String(caseId.getBytes()));
				String url = getServletConfig().getServletContext().getInitParameter("pythonwebservice")+"/finance/type=YmF0aHR1Yg==&case="+caseIdbase64;
				//System.out.println(url);
				HttpGet httpRequest = new HttpGet(url);
	        	HttpClient client = HttpClientBuilder.create().build();
	        	HttpResponse httpResponse;
	        	try {
	        		StringBuffer result = new StringBuffer();
	        		httpResponse = client.execute(httpRequest);
	    			int responseCode = httpResponse.getStatusLine().getStatusCode();
	    
	    	    	if(responseCode==200){
	    	    		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
	        	    	String line = "";
	        	    	while ((line = rd.readLine()) != null) {
	        	    		result.append(line);
	        	    	}	
	    	    		response.getWriter().write(result.toString());
	    	    	} else {
	    	    		System.out.println("responseCode: " + responseCode);
	    	    		System.out.println("fail to get data");
	    	    	}    	    	
	    		}catch (Exception e){
	    			logger.debug("full-range-log",e);
	//    			System.out.println(e.toString());
	    		}
			}
			
			// 以admin身份登入
			if ("1".equals(role)){
				logger.debug("role: admin");
				
				if ("onload".equals(action)){
					logger.debug("action: onload");
					
					finModelService = new FinModelService();
					List<FincaseVO> list = finModelService.getFincaseData(groupId);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("case_query".equals(action)){
					logger.debug("action: case_query");
					
					String caseId = request.getParameter("case_id");
					finModelService = new FinModelService();
					List<FinsimuVO> list = finModelService.getFinsimuData(caseId);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
	
				if ("gen_simu_data".equals(action)){
					logger.debug("action: gen_simu_data");
					
					String caseId = request.getParameter("case_id");
					String degree = request.getParameter("degree");
					String blndel = request.getParameter("blndel");
					finModelService = new FinModelService();
					String balance = finModelService.genSimuData(caseId, degree, blndel);
					
					if (!"fail".equals(balance)){
						List<FinsimuVO> list = finModelService.getFinsimuData(caseId);
						gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
						String jsonList = gson.toJson(list);
						response.getWriter().write(jsonList);
					} else {
						response.getWriter().write("fail");
					}	
				}
				
				if ("create".equals(action)) {
					logger.debug("action: create");
					
		    		String caseId = UUID.randomUUID().toString();
					String caseName = request.getParameter("case_name");
					Float amount = Float.valueOf(request.getParameter("amount"));
					Float safetyMoney = Float.valueOf(request.getParameter("safety_money"));
		    		String createDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		    		
					finModelService = new FinModelService();
					List<FincaseVO> list = finModelService.createModel(caseId, groupId, caseName, amount, safetyMoney, createDate);
		
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("insert".equals(action)){
					logger.debug("action: insert");
					
					String caseId = request.getParameter("case_id");
					String simulationId = UUID.randomUUID().toString();
					String f_date = request.getParameter("f_date");
					String f_type = request.getParameter("f_type");
					Boolean p_action = Boolean.parseBoolean(request.getParameter("p_action"));
					String amount = request.getParameter("amount");
					int f_kind = Integer.parseInt(request.getParameter("f_kind"));
					String description = request.getParameter("description");
					String strategy = request.getParameter("strategy");
					finModelService = new FinModelService();
					List<FinsimuVO> list = finModelService.insertFinsimuData(caseId, simulationId, userId, f_date, f_type, p_action, amount, f_kind, description, strategy);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("update".equals(action)){
					logger.debug("action: update");
					
					String caseId = request.getParameter("case_id");
					String simulationId = request.getParameter("simulation_id");
					String f_date = request.getParameter("f_date");
					String f_type = request.getParameter("f_type");
					Boolean p_action = Boolean.parseBoolean(request.getParameter("p_action"));
					String amount = request.getParameter("amount");
					int f_kind = Integer.parseInt(request.getParameter("f_kind"));
					String description = request.getParameter("description");
					String strategy = request.getParameter("strategy");
					finModelService = new FinModelService();
					List<FinsimuVO> list = finModelService.updateFinsimuData(caseId, simulationId, f_date, f_type, p_action, amount, f_kind, description, strategy);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("delete".equals(action)) {
					logger.debug("action: delete");
					
					String caseId = request.getParameter("case_id");
					String simulationId = request.getParameter("simulation_id");
		    		finModelService = new FinModelService();
		    		List<FinsimuVO> list = finModelService.deleteFinsimuData(caseId, simulationId);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
			}
	
			// 以user身份登入
			if ("0".equals(role)){
				logger.debug("role: user");
				
				if ("onload".equals(action)){
					logger.debug("action: onload");
					
					finModelService = new FinModelService();
					List<FincaseVO> list = finModelService.getFincaseData(groupId);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("case_query".equals(action)){
					logger.debug("action: case_query");
					
					String caseId = request.getParameter("case_id");
					finModelService = new FinModelService();
					List<FinsimuVO> list = finModelService.getFinsimuData(caseId);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("gen_simu_data".equals(action)){
					logger.debug("action: gen_simu_data");
					
					String caseId = request.getParameter("case_id");
					String degree = request.getParameter("degree");
					String blndel = request.getParameter("blndel");
					finModelService = new FinModelService();
					String balance = finModelService.genSimuData(caseId, degree, blndel);
					
					if (!"fail".equals(balance)){
						List<FinsimuVO> list = finModelService.getFinsimuData(caseId);
						gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
						String jsonList = gson.toJson(list);
						response.getWriter().write(jsonList);
					} else {
						response.getWriter().write("fail");
					}	
				}
				
				if ("insert".equals(action)){
					logger.debug("action: insert");
					
					String caseId = request.getParameter("case_id");
					String simulationId = UUID.randomUUID().toString();
					String f_date = request.getParameter("f_date");
					String f_type = request.getParameter("f_type");
					Boolean p_action = Boolean.parseBoolean(request.getParameter("p_action"));
					String amount = request.getParameter("amount");
					int f_kind = Integer.parseInt(request.getParameter("f_kind"));
					String description = request.getParameter("description");
					String strategy = request.getParameter("strategy");
					finModelService = new FinModelService();
					List<FinsimuVO> list = finModelService.insertFinsimuData(caseId, simulationId, userId, f_date, f_type, p_action, amount, f_kind, description, strategy);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("update".equals(action)){
					logger.debug("action: update");
					
					String caseId = request.getParameter("case_id");
					String simulationId = request.getParameter("simulation_id");
					String f_date = request.getParameter("f_date");
					String f_type = request.getParameter("f_type");
					Boolean p_action = Boolean.parseBoolean(request.getParameter("p_action"));
					String amount = request.getParameter("amount");
					int f_kind = Integer.parseInt(request.getParameter("f_kind"));
					String description = request.getParameter("description");
					String strategy = request.getParameter("strategy");
					finModelService = new FinModelService();
					List<FinsimuVO> list = finModelService.updateFinsimuData(caseId, simulationId, f_date, f_type, p_action, amount, f_kind, description, strategy);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
				if ("delete".equals(action)) {
					logger.debug("action: delete");
					
					String caseId = request.getParameter("case_id");
					String simulationId = request.getParameter("simulation_id");
		    		finModelService = new FinModelService();
		    		List<FinsimuVO> list = finModelService.deleteFinsimuData(caseId, simulationId);
					gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
					String jsonList = gson.toJson(list);
					response.getWriter().write(jsonList);
				}
				
			}
		}catch (Exception e){
			logger.debug("full-range-log",e);
		}
	}

	/**
	 * 將一個字符串中的小寫字母轉換為大寫字母
	 * 
	 * */
	public static String convertToCapitalString(String src) {
		char[] array = src.toCharArray();
		int temp = 0;
		for (int i = 0; i < array.length; i++) {
			temp = (int) array[i];
			if (temp <= 122 && temp >= 97) { // array[i]为小写字母
				array[i] = (char) (temp - 32);
			}
		}
		return String.valueOf(array);
	}

	/*************************** 處理業務邏輯 ****************************************/
	public class FinModelService {
		private FinModel_interface dao;

		public FinModelService() {
			dao = new FinModelDAO();
		}

		public List<FincaseVO> createModel(String caseId, String groupId, String caseName, Float amount, Float safetyMoney, String createDate) {
			dao.insertTBFincase(caseId, groupId, caseName, amount, safetyMoney, createDate);
			return dao.selectTBFincase(groupId);
		}
		
		public List<FincaseVO> getFincaseData(String groupId){
			return dao.selectTBFincase(groupId);
		}
		
		public List<FinsimuVO> getFinsimuData(String caseId){
			return dao.selectTBFinsimu(caseId);
		}
		
		public String genSimuData(String caseId, String degree, String blndel){
			return dao.genSimuData(caseId, degree, blndel);
		}
		
		public List<FinsimuVO> insertFinsimuData(String caseId, String simulationId, String userId, String f_date, String f_type, Boolean p_action, String amount, int f_kind, String description, String strategy){
			dao.insertFinsimuData(caseId, simulationId, userId, f_date, f_type, p_action, amount, f_kind, description, strategy);
			return dao.selectTBFinsimu(caseId);
		}
		
		public List<FinsimuVO> updateFinsimuData(String caseId, String simulationId, String f_date, String f_type, Boolean p_action, String amount, int f_kind, String description, String strategy){
			dao.updataFinsimuData(simulationId, f_date, f_type, p_action, amount, f_kind, description, strategy);
			return dao.selectTBFinsimu(caseId);
		}
		
		public List<FinsimuVO> deleteFinsimuData(String caseId, String simulationId){
			dao.deleteFinsimuData(simulationId);
			return dao.selectTBFinsimu(caseId);
		}
		
		public JSONArray queryDataInJsonArray(String queryStr){
			return dao.queryDataInJsonArray(queryStr);
		}

		public String queryDataInStr(String queryStr){
			return dao.queryDataInStr(queryStr);
		}
	}
	
	/*************************** interface ****************************************/

	interface FinModel_interface {

		public void insertTBFincase(String caseId, String groupId, String caseName, Float amount, Float safetyMoney, String createDate);

		public List<FincaseVO> selectTBFincase(String groupId);
		
		public List<FinsimuVO> selectTBFinsimu(String caseId);
		
		public String genSimuData(String caseId, String degree, String blndel);
		
		public void insertFinsimuData(String caseId, String simulationId, String userId, String f_date, String f_type, Boolean p_action, String amount, int f_kind, String description, String strategy);
		
		public void updataFinsimuData(String simulationId, String f_date, String f_type, Boolean p_action, String amount, int f_kind, String description, String strategy);
		
		public void deleteFinsimuData(String simulationId);
		
		public JSONArray queryDataInJsonArray(String queryStr);
		
		public String queryDataInStr(String queryStr);
	}

	/*************************** 操作資料庫 ****************************************/
	class FinModelDAO implements FinModel_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		@Override
		public List<FincaseVO> selectTBFincase(String groupId) {
			
			String queryStr = "SELECT * FROM tb_fincase WHERE group_id ='" + groupId + "' ORDER BY create_date";

			List<FincaseVO> list = new ArrayList<FincaseVO>();
			Connection con = null;
			Statement statement = null;
			ResultSet rs = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				statement = con.createStatement();
				rs = statement.executeQuery(queryStr);
				while (rs.next()) {
					FincaseVO fincaseVO= new FincaseVO();
					fincaseVO.setCase_id(rs.getString("case_id"));
					fincaseVO.setGroup_id(rs.getString("group_id"));
					fincaseVO.setCase_name(rs.getString("case_name"));
					fincaseVO.setAmount(Float.valueOf(rs.getString("Amount")));
					fincaseVO.setSafety_money(Float.valueOf(rs.getString("safety_money")));
					fincaseVO.setCreate_date(sdf.parse(rs.getString("create_date")));
					list.add(fincaseVO); // Store the row in the list
				}
				// Handle any driver errors
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
				// Clean up JDBC resources
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} catch (ParseException pe) {
				// TODO Auto-generated catch block
				throw new RuntimeException("A database error occured. " + pe.getMessage());
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
			return list;
		}
		
		@Override
		public void insertTBFincase(String caseId, String groupId, String caseName, Float amount, Float safetyMoney, String createDate) {
			
			String insertStr = "INSERT INTO tb_fincase"
    				+ "(case_id, group_id, case_name, Amount, safety_money, create_date) " + "VALUES"
    				+ "('" + caseId + "', '" + groupId + "', '" +  caseName + "', " + amount + ", " + safetyMoney + ", convert('" + createDate + "', date))";

			Connection con = null;
			Statement statement = null; 
			
			try {				
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				statement = con.createStatement();
				statement.executeUpdate(insertStr);
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
				// Clean up JDBC resources
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
		}
		
		@Override
		public List<FinsimuVO> selectTBFinsimu(String caseId) {
		
			String queryStr = "SELECT * FROM tb_finsimu WHERE case_id = '" + caseId + "' ORDER BY f_date";

			List<FinsimuVO> list = new ArrayList<FinsimuVO>();
			Connection con = null;
			Statement statement = null;
			ResultSet rs = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				statement = con.createStatement();
				rs = statement.executeQuery(queryStr);
				while (rs.next()) {
					FinsimuVO finsimuVO= new FinsimuVO();
					finsimuVO.setSimulation_id(rs.getString("simulation_id") == null? "":rs.getString("simulation_id"));
					finsimuVO.setCase_id(rs.getString("case_id") == null? "":rs.getString("case_id"));
//					finsimuVO.setUser_id(rs.getString("user_id"));
					finsimuVO.setF_date(sdf.parse(rs.getString("f_date")));
					finsimuVO.setF_type(Integer.valueOf(rs.getString("f_type")));
					finsimuVO.setAction("1".equals(rs.getString("action").toString())?true:false);
					finsimuVO.setAmount(Double.valueOf(rs.getString("amount") == null? "0":rs.getString("amount")));
					finsimuVO.setF_kind(Integer.valueOf(rs.getString("f_kind") == null? "0":rs.getString("f_kind")));
					finsimuVO.setDescription(rs.getString("description") == null? "":rs.getString("description"));
					finsimuVO.setStrategy(rs.getString("strategy") == null? "":rs.getString("strategy"));
					list.add(finsimuVO); // Store the row in the list
				}
				// Handle any driver errors
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
				// Clean up JDBC resources
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} catch (ParseException pe) {
				// TODO Auto-generated catch block
				throw new RuntimeException("A database error occured. " + pe.getMessage());
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
			return list;
		}	

		@Override
		public String genSimuData(String caseId, String degree, String blndel) {
			logger.debug("genSimuData:");
			
        	String encodeCaseId = new String(Base64.encodeBase64String(caseId.getBytes()));
        	String encodeDegree = new String(Base64.encodeBase64String(degree.getBytes()));
        	String encodeBlndel = new String(Base64.encodeBase64String(blndel.getBytes()));
    		String url = wsPath + "/CashFlow/caseid=" + encodeCaseId + "&degree=" + encodeDegree + "&blndel=" + encodeBlndel;
//    		String url = "http://61.218.8.55:8099/CashFlow/caseid=" + encodeCaseId + "&degree=" + encodeDegree + "&blndel=" + encodeBlndel;

    		logger.debug(url);
    		
    		HttpGet httpRequest = new HttpGet(url);
        	HttpClient client = HttpClientBuilder.create().build();
        	HttpResponse httpResponse;
        	try {
        		StringBuffer result = new StringBuffer();
        		httpResponse = client.execute(httpRequest);
    			int responseCode = httpResponse.getStatusLine().getStatusCode();
    
    	    	if(responseCode==200){
    	    		logger.debug("genSimuData responseCode 200.");
    	    		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        			
        	    	String line = "";
        	    	while ((line = rd.readLine()) != null) {
        	    		result.append(line);
        	    	}
        	    	return result.toString();
    	    	} else {
    	    		logger.debug("genSimuData fail.");
    	    		return "fail"; 
    	    	}
  	    	
    		} catch (ClientProtocolException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			logger.debug("full-range-log",e);
    		} catch (UnsupportedOperationException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			logger.debug("full-range-log",e);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			logger.debug("full-range-log",e);
    		}
        	return "fail";
		}
		
		@Override
		public void insertFinsimuData(String caseId, String simulationId, String userId, String f_date, String f_type, Boolean p_action, String amount, int f_kind, String description, String strategy) {
			logger.debug("insertFinsimuData:");
			
    		String insertStr = "INSERT INTO tb_finsimu " + 
    						"(simulation_id, case_id, user_id, f_date, f_type, action, amount, f_kind, description, strategy) " + 
    						"VALUES " +
    						"('"+simulationId+"', '"+ caseId +"', '"+ userId +"', convert('" + f_date + "', date), '" + f_type + "', " + p_action + ", '" + amount + "', '" + f_kind + "', '" + description + "', '" + strategy + "')";

			Connection con = null;
			Statement statement = null; 
			
			try {			   	
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);  
				statement = con.createStatement();
				statement.executeUpdate(insertStr);
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
				// Clean up JDBC resources
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
		}

		@Override
		public void updataFinsimuData(String simulationId, String f_date, String f_type, Boolean p_action, String amount, int f_kind, String description, String strategy) {
			logger.debug("updataFinsimuData:");
			
			String updateStr = "UPDATE tb_finsimu SET " + 
								"f_date = convert('" + f_date + "', date), " +
								"f_type = '" + f_type + "' ," +
								"action = " + p_action + ", " +
								"amount = '" + amount + "', " +
								"f_kind = " + f_kind + ", " +
								"description = '" + description + "', " +
								"strategy = '" + strategy + "' " +
								"WHERE simulation_id = '" + simulationId + "'";

			Connection con = null;
			Statement statement = null; 
			
			try {			   	
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				statement = con.createStatement();
				statement.executeUpdate(updateStr);
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
				// Clean up JDBC resources
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
		}
		
		@Override
		public void deleteFinsimuData(String simulationId) {
			logger.debug("deleteFinsimuData: " + simulationId);
			
    		String delStr = "DELETE FROM tb_finsimu WHERE simulation_id = '" + simulationId + "'";

			Connection con = null;
			Statement statement = null; 
			
			try {				
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				statement = con.createStatement();
				statement.executeUpdate(delStr);
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
				// Clean up JDBC resources
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
		}

		@Override
		public JSONArray queryDataInJsonArray(String queryStr) {
			
			JSONArray jsonArray = new JSONArray();  
			Connection con = null;
			Statement statement = null; 
   
	    	try {				
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				statement = con.createStatement();
	    		ResultSet rs = statement.executeQuery(queryStr);            
	    		ResultSetMetaData rsmeta = rs.getMetaData();          
	    		int cols = rsmeta.getColumnCount();
				while(rs.next()) { 
	    			JSONObject jsonObject = new JSONObject();       
	    			for(int i=1; i<=cols; i++) {
	    				jsonObject.put(rsmeta.getColumnLabel(i), rs.getString(i));
	    			}        
	    			jsonArray.put(jsonObject);          
	    		} 

			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
				// Clean up JDBC resources
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
			return jsonArray;
		}

		@Override
		public String queryDataInStr(String queryStr) {
			logger.debug("queryDataInStr: " + queryStr);
			
			String returnStr = "";  
			Connection con = null;
			Statement statement = null; 
			
	    	try {				
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				statement = con.createStatement();        
	    		ResultSet rs = statement.executeQuery(queryStr);            
	    		ResultSetMetaData rsmeta = rs.getMetaData();          
	    		int cols = rsmeta.getColumnCount();
	        
	    		while(rs.next()) {      
	    			for(int i=1; i<=cols; i++) {
	    				returnStr = rs.getString(i);
	    			}              
	    		}
	    		
	    		logger.debug("returnStr: " + returnStr);
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
				// Clean up JDBC resources
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
			return returnStr;
		}
	}
}