package tw.com.sbi.invoiceStatistic.controller;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import tw.com.sbi.common.controller.CommonMethod; 

import com.google.gson.Gson;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class InvoiceStatistic extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InvoiceStatistic.class);

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/text");
		String action = null2str(request.getParameter("action"));
		logger.debug("Action: "+action);

		InvoiceStatisticService service = new InvoiceStatisticService();
		JSONObject requestVO = new JSONObject();
		//四個input換資料
		//init設置畫面取得select option
		try {
			if ("select_invoice_county_stat".equals(action)) {
				//四個input換資料
				String county_name = null2str(request.getParameter("selectSearchCity"));
				String industry_category = null2str(request.getParameter("selectSearchIndustry"));
				String start_time = null2str(request.getParameter("selectYearMonthStart"));
				String end_time = null2str(request.getParameter("selectYearMonthEnd"));
				String timegroup = null2str(request.getParameter("compare_type"));
				
				logger.debug("county_name: "+county_name);
				logger.debug("industry_category: "+industry_category);
				logger.debug("start_time: "+start_time);
				logger.debug("end_time: "+end_time);
				logger.debug("timegroup: "+timegroup);
				
				requestVO.put("county_name",county_name);
				requestVO.put("industry_category",industry_category);
				requestVO.put("start_time",start_time);
				requestVO.put("end_time",end_time);
				requestVO.put("timegroup",timegroup);
				
				List<Object> responseList = service.select_invoice_county_stat(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if ("select_invoice_dimension".equals(action)) {
				//init設置畫面取得select option
				String dimension_name = null2str(request.getParameter("dimension_name"));
				logger.debug("dimension_name: "+dimension_name);
				
				requestVO.put("dimension_name",dimension_name);
				
				List<Object> responseList = service.select_invoice_dimension(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}else{
				logger.debug("[TransToJSP]");
				response.sendRedirect("./invoiceStatistic.jsp");
			}

		} catch (Exception e) {
			logger.error("Exception:".concat(e.getMessage()));
			e.printStackTrace();
			logger.debug("full-range-log",e);
		}
	}
	
	public class InvoiceStatisticService {
		private InvoiceStatisticInterface dao;
		public InvoiceStatisticService() {
			dao = new InvoiceStatisticDAO();
		}
		
		public List<Object> select_invoice_county_stat(JSONObject requestVO) {
			return dao.select_invoice_county_stat(requestVO);
		}
		public List<Object> select_invoice_dimension(JSONObject requestVO) {
			return dao.select_invoice_dimension(requestVO);
		}
	}
	
	interface InvoiceStatisticInterface {
		public List<Object> select_invoice_county_stat(JSONObject requestVO);
		public List<Object> select_invoice_dimension(JSONObject requestVO);
	}
	
	class InvoiceStatisticDAO implements InvoiceStatisticInterface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private static final String sp_select_invoice_county_stat = "call sp_select_invoice_county_stat(?,?,?,?,?)";
		private static final String sp_select_invoice_dimension = "call sp_select_invoice_dimension(?)";
		
		public List<Object> select_invoice_county_stat(JSONObject requestVO) {
			List<Object> list = new ArrayList<Object>();
			JSONObject vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_invoice_county_stat);
				
				pstmt.setString(1, requestVO.getString("county_name"));
				pstmt.setString(2, requestVO.getString("industry_category"));
				pstmt.setString(3, requestVO.getString("start_time"));
				pstmt.setString(4, requestVO.getString("end_time"));
				pstmt.setString(5, requestVO.getString("timegroup"));
				
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					vo = new JSONObject();
					for (int i = 1; i <= columnCount; i++ ) {
						if( null2str(rsmd.getColumnName(i)).equals("year")
								&& requestVO.getString("start_time").equals(requestVO.getString("end_time"))){
							vo.put(null2str(rsmd.getColumnName(i)),requestVO.getString("start_time").replace("/01",""));
						}else{
							vo.put(null2str(rsmd.getColumnName(i)),null2str(rs.getString(i)));
						}
					}
					list.add(vo);
				}				
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				CommonMethod.cleanSQLConnection(con, pstmt, rs);
			}
			return list;
		}
		@Override
		public List<Object> select_invoice_dimension(JSONObject requestVO) {
			List<Object> list = new ArrayList<Object>();
			JSONObject vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_invoice_dimension);
				pstmt.setString(1, requestVO.getString("dimension_name"));
				
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					vo = new JSONObject();
					for (int i = 1; i <= columnCount; i++ ) {
						vo.put("askDimension",null2str(rs.getString(i)));
					}
					list.add(vo);
				}				
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				CommonMethod.cleanSQLConnection(con, pstmt, rs);
			}
			return list;
		}
	}
	public String null2str(Object object) {
		return object == null ? "" : object.toString().trim();
	}
}