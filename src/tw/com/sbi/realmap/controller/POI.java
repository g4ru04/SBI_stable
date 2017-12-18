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

public class POI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(POI.class);
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/text");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String action = null2Str(request.getParameter("action"));
		JSONObject requestVO = null;
		PoiService poiService = new PoiService();
		logger.debug("action: "+action);
		try {
			//差一個讀poi_icon圖片的action
			if("select_POI_by_typelist_bounds".equals(action)){
				String typelist = null2Str(request.getParameter("typelist"));
				String bound_north = null2Str(request.getParameter("bound_north"));
				String bound_east = null2Str(request.getParameter("bound_east"));
				String bound_south = null2Str(request.getParameter("bound_south"));
				String bound_west = null2Str(request.getParameter("bound_west"));
				logger.debug("typelist: "+typelist);
				logger.debug("bound_north: "+bound_north);
				logger.debug("bound_east: "+bound_east);
				logger.debug("bound_south: "+bound_south);
				logger.debug("bound_west: "+bound_west);
				
				requestVO = new JSONObject();
				requestVO.put("typelist",typelist);
				requestVO.put("bound_north",bound_north);
				requestVO.put("bound_east",bound_east);
				requestVO.put("bound_south",bound_south);
				requestVO.put("bound_west",bound_west);
				
				List<Object> responseList = poiService.select_POI_by_typelist_bounds(requestVO);
				
				String jsonStrList = new Gson().toJson(responseList);
				logger.debug("return "+responseList.size()+" of objects like: "+(responseList.size()>1? new Gson().toJson(responseList.get(0)):"[]"));
				response.getWriter().write(jsonStrList);
			}else if("sp_select_MRT_flow".equals(action)){
				//這是平均的 return的就是平日假日的 00~23
				String station_name = null2Str(request.getParameter("station_name"));
				logger.debug("station_name: "+station_name);
				
				requestVO = new JSONObject();
				requestVO.put("station_name",station_name);
				
				List<Object> responseList = poiService.select_MRT_flow(requestVO);
				
				String jsonStrList = new Gson().toJson(responseList);
				logger.debug("return "+responseList.size()+" of objects like: "+(responseList.size()>1? new Gson().toJson(responseList.get(0)):"[]"));
				response.getWriter().write(jsonStrList);
			}else if("select_flow_metro".equals(action)){
				//這是單一lastweek的00~23 比照天氣
				String metro_name = CommonMethod.null2str(request.getParameter("metro_name"));
				logger.debug("metro_name: "+metro_name);
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(metro_name);
				String outcome = new CommonMethod(getServletConfig())
						.useMysqlDBString("call sp_select_flow_metro(?)",request_VO);
				response.getWriter().write(outcome);
				
			}else if("select_flow_interchange".equals(action)){
				//這是每天的 return 前台處理掉時間 只剩一週 就是一~七 *00~23
				String interchange_name = CommonMethod.null2str(request.getParameter("interchange_name"));
				logger.debug("interchange_name: "+interchange_name);
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(interchange_name);
				String outcome = new CommonMethod(getServletConfig())
						.useMysqlDBString("call sp_select_flow_interchange(?)",request_VO);
				response.getWriter().write(outcome);
				
			}else if("select_poi_statistic_circle".equals(action)){
				String subtypelist = null2Str(request.getParameter("subtypelist"));
				String center_lat = null2Str(request.getParameter("center_lat"));
				String center_lng = null2Str(request.getParameter("center_lng"));
				String radius = null2Str(request.getParameter("radius"));
				
				logger.debug("typelist: "+subtypelist);
				logger.debug("center_lat: "+center_lat);
				logger.debug("center_lng: "+center_lng);
				logger.debug("radius: "+radius);
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(subtypelist);
				request_VO.put(center_lat);
				request_VO.put(center_lng);
				request_VO.put(radius);
				
				long time1 = System.currentTimeMillis();
				
				String outcome = new CommonMethod(getServletConfig())
						.useMysqlDBString("call sp_select_POI_statistic_circle(?,?,?,?)",request_VO);
				response.getWriter().write(outcome);
				
				String secStr = (System.currentTimeMillis()-time1)+"";
				String sec = (secStr.length()<3?secStr+" mils":secStr.substring(0,secStr.length()-3)+"."+ secStr.substring(secStr.length()-3,secStr.length())+" sec");
				
				logger.debug("timer stop: (cost "+sec+")");
				
			}else if("select_poi_statistic_circle_detail".equals(action)){
				String typelist = null2Str(request.getParameter("typelist"));
				String center_lat = null2Str(request.getParameter("center_lat"));
				String center_lng = null2Str(request.getParameter("center_lng"));
				String radius = null2Str(request.getParameter("radius"));
				
				logger.debug("typelist: "+typelist);
				logger.debug("center_lat: "+center_lat);
				logger.debug("center_lng: "+center_lng);
				logger.debug("radius: "+radius);
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(typelist);
				request_VO.put(center_lat);
				request_VO.put(center_lng);
				request_VO.put(radius);
				
				long time1 = System.currentTimeMillis();
				
				String outcome = new CommonMethod(getServletConfig())
						.useMysqlDBString("call sp_select_POI_statistic_circle_detail(?,?,?,?)",request_VO);
				response.getWriter().write(outcome);
				
				String secStr = (System.currentTimeMillis()-time1)+"";
				String sec = (secStr.length()<3?secStr+" mils":secStr.substring(0,secStr.length()-3)+"."+ secStr.substring(secStr.length()-3,secStr.length())+" sec");
				
				logger.debug("timer stop: (cost "+sec+")");
			}else if("select_poi_of_metro".equals(action)){
				
				Map<String, String> requestMap = CommonMethod.requestParameter(request);
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(CommonMethod.null2str(requestMap.get("metro_type")));
				request_VO.put(CommonMethod.null2str(requestMap.get("metro_name")));
				String outcome = new CommonMethod(getServletConfig()) 
						.useMysqlDBString("call sp_select_poi_of_metro(?,?)",request_VO);
				response.getWriter().write(outcome);
				
			}else if("select_poi_statistic_circle_villageinfo".equals(action)){
				Map<String, String> mapRequestPara = CommonMethod.requestParameter(request);
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(mapRequestPara.get("center_lat"));
				request_VO.put(mapRequestPara.get("center_lng"));
				request_VO.put(mapRequestPara.get("radius"));
				
				long time1 = System.currentTimeMillis();
				
				String outcome = new CommonMethod(getServletConfig())
						.useMysqlDBString("call sp_select_POI_statistic_circle_villageinfo(?,?,?)",request_VO);
				response.getWriter().write(outcome);
				
				String secStr = (System.currentTimeMillis()-time1)+"";
				String sec = (secStr.length()<3?secStr+" mils":secStr.substring(0,secStr.length()-3)+"."+ secStr.substring(secStr.length()-3,secStr.length())+" sec");
				
				logger.debug("timer stop: (cost "+sec+")");
			}else{
				logger.debug("[TransToJSP]");
				response.sendRedirect("./POI.jsp");
			}
		} catch (Exception e) {
			response.getWriter().write("fail!");
			e.printStackTrace(System.err);
			logger.debug("full-range-log",e);
		}	
	}
	/*************************** 處理業務邏輯 ****************************************/
	
	public class PoiService {
		private Poi_interface dao;
		public PoiService() {
			dao = new PoiDAO();
		}
		public List<Object> select_POI_by_typelist_bounds(JSONObject requestVO) {
			return dao.select_POI_by_typelist_bounds(requestVO);
		}
		public List<Object> select_MRT_flow(JSONObject requestVO) {
			return dao.select_MRT_flow(requestVO);
		}
	}

	/*************************** 制定規章方法 ****************************************/
	
	interface Poi_interface {
		public List<Object> select_POI_by_typelist_bounds(JSONObject requestVO);
		public List<Object> select_MRT_flow(JSONObject requestVO);
	}
	
	/*************************** 操作資料庫 ****************************************/
	class PoiDAO implements Poi_interface{
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		
		private static final String sp_select_POI_by_typelist_bounds = "call sp_select_POI_by_typelist_bounds(?,?,?,?,?)";
		private static final String sp_select_MRT_flow = "call sp_select_MRT_flow(?)";
		
		public List<Object> select_POI_by_typelist_bounds(JSONObject requestVO) {
			List<Object> list = new ArrayList<Object>();
			JSONObject vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_POI_by_typelist_bounds);
				pstmt.setString(1, null2Str(requestVO.get("typelist")));
				pstmt.setString(2, null2Str(requestVO.get("bound_north")));
				pstmt.setString(3, null2Str(requestVO.get("bound_east")));
				pstmt.setString(4, null2Str(requestVO.get("bound_south")));
				pstmt.setString(5, null2Str(requestVO.get("bound_west")));
				
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					vo = new JSONObject();
					for (int i = 1; i <= columnCount; i++ ) {
						  vo.put(null2Str(rsmd.getColumnName(i)),null2Str(rs.getString(i)));
					}
					list.add(vo); // Store the row in the list
				}				
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
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
		public List<Object> select_MRT_flow(JSONObject requestVO) {
			List<Object> list = new ArrayList<Object>();
			JSONObject vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_MRT_flow);
				pstmt.setString(1, null2Str(requestVO.get("station_name")));
				
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					vo = new JSONObject();
					for (int i = 1; i <= columnCount; i++ ) {
						  vo.put(null2Str(rsmd.getColumnName(i)),null2Str(rs.getString(i)));
					}
					list.add(vo); // Store the row in the list
				}				
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
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
	}

	private String null2Str(Object object) {
		if (object instanceof Timestamp)
			return object == null ? "" : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(object);
		return object == null ? "" : object.toString().trim();
	}
}