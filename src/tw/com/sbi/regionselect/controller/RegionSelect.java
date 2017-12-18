package tw.com.sbi.regionselect.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class RegionSelect extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(RegionSelect.class);
	private static final long serialVersionUID = 1L;
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/text");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String action = request.getParameter("action") == null?"":request.getParameter("action");
				
		logger.debug("Action:" + action);
		
		if("insert_QA".equals(request.getParameter("action"))){
			try {
				String QA_name = request.getParameter("QA_name");
				String QA_propost = request.getParameter("QA_propost");
				String QA_taxid = request.getParameter("QA_taxid");
				String QA_email = request.getParameter("QA_email");
				String QA_investcountry = request.getParameter("QA_investcountry");
				String QA_industry = request.getParameter("QA_industry");
				String QA_industry_item = request.getParameter("QA_industry_item");
				String QA_invest_industry = request.getParameter("QA_invest_industry");
				String QA_invest_industry_item = request.getParameter("QA_invest_industry_item");
				String QA_invest_brand = request.getParameter("QA_invest_brand");
				String QA_invest_pattern = request.getParameter("QA_invest_pattern");
				String QA_invest_type = request.getParameter("QA_invest_type");
				String QA_invest_amount = request.getParameter("QA_invest_amount");
				String QA_recommendation = request.getParameter("QA_recommendation");

				logger.debug("QA_name:" + QA_name);
				logger.debug("QA_propost:" + QA_propost);
				logger.debug("QA_taxid:" + QA_taxid);
				logger.debug("QA_email:" + QA_email);
				logger.debug("QA_investcountry:" + QA_investcountry);
				logger.debug("QA_industry:" + QA_industry);
				logger.debug("QA_industry_item:" + QA_industry_item);
				logger.debug("QA_invest_industry:" + QA_invest_industry);
				logger.debug("QA_invest_industry_item:" + QA_invest_industry_item);
				logger.debug("QA_invest_brand:" + QA_invest_brand);
				logger.debug("QA_invest_pattern:" + QA_invest_pattern);
				logger.debug("QA_invest_type:" + QA_invest_type);
				logger.debug("QA_invest_amount:" + QA_invest_amount);
				logger.debug("QA_recommendation:" + QA_recommendation);
				
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("call sp_insert_RegionSelect(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pstmt.setString(1, request.getParameter("QA_name"));
				pstmt.setString(2, request.getParameter("QA_propost"));
				pstmt.setString(3, request.getParameter("QA_taxid"));
				pstmt.setString(4, request.getParameter("QA_email"));
				pstmt.setString(5, request.getParameter("QA_investcountry"));
				pstmt.setString(6, request.getParameter("QA_industry"));
				pstmt.setString(7, request.getParameter("QA_industry_item"));
				pstmt.setString(8, request.getParameter("QA_invest_industry"));
				pstmt.setString(9, request.getParameter("QA_invest_industry_item"));
				pstmt.setString(10, request.getParameter("QA_invest_brand"));
				pstmt.setString(11, request.getParameter("QA_invest_pattern"));
				pstmt.setString(12, request.getParameter("QA_invest_type"));
				pstmt.setString(13, request.getParameter("QA_invest_amount"));
				pstmt.setString(14, request.getParameter("QA_recommendation"));
				pstmt.setString(15, request.getSession().getAttribute("user_id").toString());
				
				rs = pstmt.executeQuery();
				response.getWriter().write("success");
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
		}else if("insert_Regionselect".equals(request.getParameter("action"))){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("call sp_insert_ANAL_Regionselect(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				String tmp = "餐飲業".equals(request.getParameter("industry"))?"2":"1";
				pstmt.setString(1, request.getParameter("country"));
				pstmt.setString(2, request.getParameter("region"));
				pstmt.setString(3, tmp);
				pstmt.setString(4, request.getParameter("check1"));
				pstmt.setString(5, request.getParameter("check2"));
				pstmt.setString(6, request.getParameter("check3"));
				pstmt.setString(7, request.getParameter("check4"));
				pstmt.setString(8, request.getParameter("check5"));
				pstmt.setString(9, request.getParameter("check6"));
				pstmt.setString(10, request.getParameter("rs1"));
				pstmt.setString(11, request.getParameter("rs2"));
				pstmt.setString(12, request.getParameter("rs3"));
				pstmt.setString(13, request.getParameter("score"));
				pstmt.setString(14, request.getSession().getAttribute("user_id").toString());
//				logger.debug("len: "+request.getParameter("score").length());
				rs = pstmt.executeQuery();
				response.getWriter().write("success");
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
		}else if("call_web_service".equals(request.getParameter("action"))){
			String url = getServletConfig().getServletContext().getInitParameter("pythonwebservice")+"/selectregion/"
					+"type="+ new String(Base64.encodeBase64String(request.getParameter("industry").toString().getBytes("UTF-8")))
					+"&area="+ new String(Base64.encodeBase64String(request.getParameter("country").toString().getBytes("UTF-8")))
					+"&city="+ new String(Base64.encodeBase64String(request.getParameter("region").toString().getBytes("UTF-8")))
					+"&ch1a="+ new String(Base64.encodeBase64String(request.getParameter("check1").getBytes()))
					+"&ch1b="+ new String(Base64.encodeBase64String(request.getParameter("check2").getBytes()))
					+"&ch2a="+ new String(Base64.encodeBase64String(request.getParameter("check3").getBytes()))
					+"&ch2b="+ new String(Base64.encodeBase64String(request.getParameter("check4").getBytes()))
					+"&ch3a="+ new String(Base64.encodeBase64String(request.getParameter("check5").getBytes()))
					+"&ch3b="+ new String(Base64.encodeBase64String(request.getParameter("check6").getBytes()))
					+"&per1="+ new String(Base64.encodeBase64String(request.getParameter("rs1").getBytes()))
					+"&per2="+ new String(Base64.encodeBase64String(request.getParameter("rs2").getBytes()))
					+"&per3="+ new String(Base64.encodeBase64String(request.getParameter("rs3").getBytes()));
			
			logger.debug("url:" + url);
			
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
        	    	Gson gson = new Gson();
//        	    	CBDrank [] r =new CBDrank[10];
//        	    	CBDrank json = gson.fromJson(result.toString(),CBDrank.class);
        	    	
//        	    	for(CBDrank cbd:jsonobj){
//        				System.out.println(cbd.City+" "+cbd.Score);
//        			}
        	    	CBDrank[] jsonobj = gson.fromJson(result.toString(), CBDrank[].class);
        	    	String jsonList = gson.toJson(jsonobj);
    	    		response.getWriter().write(jsonList);
    	    	} else {
    	    		System.out.println("responseCode: " + responseCode);
    	    		System.out.println("fail to get data");
    	    	} 	
    		}catch (Exception e){
    			logger.debug("full-range-log",e);
    			System.out.println(e.toString());
    		}
		}else if("select_area".equals(request.getParameter("action"))){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("call sp_select_region_area()");
//				pstmt.setString(1, request.getParameter("service_id"));
				rs = pstmt.executeQuery();
				int count; if (rs.last()){count = rs.getRow();}else{count = 0;}rs.beforeFirst();
			    
				String[] list= new String[count];
			    count=0;
				while (rs.next()) {
					list[count++]=(rs.getString("Country"));
				}
				Gson gson = new Gson();
				response.getWriter().write(gson.toJson(list));
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
		
		}else if("select_city".equals(request.getParameter("action"))){
			//call sp_select_region_city (?)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("call sp_select_region_city(?)");
//				pstmt.setString(1, request.getParameter("台灣"));
				pstmt.setString(1, request.getParameter("area"));
				rs = pstmt.executeQuery();
				int count; 
				if (rs.last()){
					count = rs.getRow();
				}else{
					count = 0;
				}
				rs.beforeFirst();
			    
				String[] list= new String[count];
			    count=0;
				while (rs.next()) {
					list[count++]=(rs.getString("City"));
				}
				Gson gson = new Gson();
				response.getWriter().write(gson.toJson(list));
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
		}else if("select_CBD".equals(request.getParameter("action"))){
			//call sp_select_region_CBD (?,?)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("call sp_select_region_CBD(?,?)");
				pstmt.setString(1, request.getParameter("area"));
				logger.debug(request.getParameter("city"));
				pstmt.setString(2, request.getParameter("city"));
				rs = pstmt.executeQuery();
				int count; if (rs.last()){count = rs.getRow();}else{count = 0;}rs.beforeFirst();
				CBD[] list = new CBD[count];
//			    System.out.println(count);
				count=0;
			    
				while (rs.next()) {
					list[count] = new CBD();
					list[count].name = rs.getString("CBD");
					list[count].lng = rs.getString("Longitude");
					list[count].lat = rs.getString("Latitude");
					count++;
				}
				Gson gson = new Gson();
				response.getWriter().write(gson.toJson(list));
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
			
		}
		//#############
	}
	
	class CBD {
		public String name;
		public String lng;
		public String lat;
	}
	
	class CBDrank {
		public String City;
		public String Score;
	}
}
