package tw.com.sbi.companyregister.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;

import tw.com.sbi.vo.CompanyRegisterVO;
import tw.com.sbi.vo.DataSHPVO;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;
public class CompanyRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CompanyRegister.class);

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/text");
		String action = null2str(request.getParameter("action"));
		logger.debug("Action: "+action+" (timer start)");
        long time1 = System.currentTimeMillis();

		CompanyRegisterService service = new CompanyRegisterService();
		CompanyRegisterVO requestVO = new CompanyRegisterVO();
		//三種查詢 TYPE DATASET STATISTIC
		//TYPE及DATASET共分三層
		//1.依條件查出各city家數
		//2.city(+上列條件)查有哪些 鄉鎮市區
		//3.鄉鎮市區查公司名及其經緯度
		//定位在地圖上 => 查詢單個公司明細
		//STATISTIC不分層
		//直接select * from 那一個統計TABLE
		try {
			if ("select_company_by_type".equals(action)) {
				//TYPE查詢第一層
				String company_type = null2str(request.getParameter("company_type"));
				String keyword = null2str(request.getParameter("keyword"));
				logger.debug("company_type: "+company_type);
				logger.debug("keyword: "+keyword);
				
				requestVO.setRequest_command("select_company_by_type");
				requestVO.setRequest_type(company_type);
				requestVO.setRequest_keyword(keyword);
				
				List<CompanyRegisterVO> responseList = service.select_company_by(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}else if ("select_company_by_dataset".equals(action)) {
				//DATASET查詢第一層
				String dataset_name = null2str(request.getParameter("dataset_name"));
				String keyword = null2str(request.getParameter("keyword"));
				logger.debug("dataset_name: "+dataset_name);
				logger.debug("keyword: "+keyword);
				
				requestVO.setRequest_command("select_company_by_dataset");
				requestVO.setRequest_dataset(dataset_name);
				requestVO.setRequest_keyword(keyword);
				
				List<CompanyRegisterVO> responseList = service.select_company_by(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}else if ("select_company_by_statistic".equals(action)) {
				//STATISTIC查詢
				String statistic_type = null2str(request.getParameter("statistic_type"));
				logger.debug("statistic_type: "+statistic_type);
				
				requestVO.setRequest_command("select_company_by_statistic");
				requestVO.setRequest_statistic(statistic_type);
				
				List<Object> responseList = service.select_company_by_statistic(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return "+responseList.size()+" of objects like: "+(responseList.size()>1? new Gson().toJson(responseList.get(0)):"[]"));
				response.getWriter().write(jsonStrList);
			}else if ("select_town".equals(action)) {
				//TYPE及DATASET查詢第二層
				String city = null2str(request.getParameter("city"));
				String company_type = null2str(request.getParameter("company_type"));
				company_type = company_type!=""?"公司登記(依營業項目別)-"+company_type:"";
				String dataset_name = null2str(request.getParameter("dataset_name"));
				dataset_name = dataset_name!=""?"公司登記其他-"+dataset_name:"";
				String keyword = null2str(request.getParameter("keyword"));
				logger.debug("city: "+city);
				logger.debug("company_type: "+company_type);
				logger.debug("dataset_name: "+dataset_name);
				logger.debug("keyword: "+keyword);
				
				requestVO.setDistrict_name(city);
				requestVO.setRequest_source(company_type + dataset_name);
				requestVO.setRequest_keyword(keyword);
				List<CompanyRegisterVO> responseList = service.select_district_below(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if ("select_company_by_type_detail".equals(action)) {
				//TYPE第三層
				String city = null2str(request.getParameter("city"));
				String town = null2str(request.getParameter("town"));
				String company_type = null2str(request.getParameter("company_type"));
				String keyword = null2str(request.getParameter("keyword"));
				logger.debug("city: "+city);
				logger.debug("town: "+town);
				logger.debug("company_type: "+company_type);
				logger.debug("keyword: "+keyword);
				
				requestVO.setRequest_command("select_company_detail_by_type");
				
				requestVO.setRequest_city(city);
				requestVO.setRequest_town(town);
				requestVO.setRequest_type(company_type);
				requestVO.setRequest_keyword(keyword);
				
				List<CompanyRegisterVO> responseList = service.select_company_detail_by(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return "+responseList.size()+" of objects like :"+(responseList.size()>1? new Gson().toJson(responseList.get(0)):"[]"));
				response.getWriter().write(jsonStrList);
				
			}else if ("select_company_by_dataset_detail".equals(action)) {
				//DATASET第三層
				String city = null2str(request.getParameter("city"));
				String town = null2str(request.getParameter("town"));
				String dataset_name = null2str(request.getParameter("dataset_name"));
				String keyword = null2str(request.getParameter("keyword"));
				logger.debug("city: "+city);
				logger.debug("town: "+town);
				logger.debug("dataset_name: "+dataset_name);
				logger.debug("keyword: "+keyword);
				
				requestVO.setRequest_command("select_company_detail_by_dataset");
				
				requestVO.setRequest_city(city);
				requestVO.setRequest_town(town);
				requestVO.setRequest_dataset(dataset_name);
				requestVO.setRequest_keyword(keyword);
				
				List<CompanyRegisterVO> responseList = service.select_company_detail_by(requestVO);
				String jsonStrList = new Gson().toJson(responseList);
				
				logger.debug("return "+responseList.size()+" of objects like :"+(responseList.size()>1? new Gson().toJson(responseList.get(0)):"[]"));
				response.getWriter().write(jsonStrList);
				
			}else if ("select_the_choosed_company".equals(action)) {
				//查詢單個公司明細
				String id = null2str(request.getParameter("id"));
				logger.debug("id: "+id);
				requestVO.setRequest_command("select_company_detail_by_id");
				requestVO.setRequest_id(id);
				
				List<CompanyRegisterVO> responseVO = service.select_company_detail_by_id(requestVO);
				String jsonStrList = new Gson().toJson(responseVO);
				logger.debug("return: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if ("page_load_shp".equals(action)) {
				//讀取出所有行政區的SHP
				List<DataSHPVO> responseVO = service.page_load_shp(null);
				String jsonStrList = new Gson().toJson(responseVO);
				logger.debug("return "+responseVO.size()+" of objects like: "+(responseVO.size()>1? new Gson().toJson(responseVO.get(0)):"[]"));
				response.getWriter().write(jsonStrList);
				
			}else{
				logger.debug("[TransToJSP]");
				response.sendRedirect("./companyRegister.jsp");
			}

		} catch (Exception e) {
			logger.error("Exception:".concat(e.getMessage()));
			e.printStackTrace();
			logger.debug("full-range-log",e);
		}
		
		String secStr = (System.currentTimeMillis()-time1)+"";
		String sec = (secStr.length()<3?secStr+" mils":secStr.substring(0,secStr.length()-3)+"."+ secStr.substring(secStr.length()-3,secStr.length())+" sec");
		
		logger.debug("timer stop: (cost "+sec+")");
	}
	public class CompanyRegisterService {
		private CompanyRegisterInterface dao;
		public CompanyRegisterService() {
			dao = new CompanyRegisterDAO();
		}
		
		public List<CompanyRegisterVO> select_company_by(CompanyRegisterVO requestVO) {
			return dao.select_company_by(requestVO);
		}
		public List<Object> select_company_by_statistic(CompanyRegisterVO requestVO) {
			return dao.select_company_by_statistic(requestVO);
		}
		public List<CompanyRegisterVO> select_company_detail_by(CompanyRegisterVO requestVO) {
			return dao.select_company_detail_by(requestVO);
		}
		public List<CompanyRegisterVO> select_district_below(CompanyRegisterVO requestVO) {
			return dao.select_district_below(requestVO);
		}
		public List<CompanyRegisterVO> select_company_detail_by_id(CompanyRegisterVO requestVO) {
			return dao.select_company_detail_by_id(requestVO);
		}
		public List<DataSHPVO> page_load_shp(CompanyRegisterVO requestVO) {
			return dao.page_load_shp(requestVO);
		}
	}
	interface CompanyRegisterInterface {
		public List<CompanyRegisterVO> select_company_by(CompanyRegisterVO requestVO);
		public List<Object> select_company_by_statistic(CompanyRegisterVO requestVO);
		public List<CompanyRegisterVO> select_company_detail_by(CompanyRegisterVO requestVO);
		public List<CompanyRegisterVO> select_district_below(CompanyRegisterVO requestVO);
		public List<CompanyRegisterVO> select_company_detail_by_id(CompanyRegisterVO requestVO);
		public List<DataSHPVO> page_load_shp(CompanyRegisterVO requestVO);
	}
	class CompanyRegisterDAO implements CompanyRegisterInterface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private static final String sp_select_company_by_type = "call sp_select_company_by_type(?,?)";
		private static final String sp_select_company_detail_by_type = "call sp_select_company_detail_by_type(?,?,?)";
		private static final String sp_select_company_by_dataset = "call sp_select_company_by_dataset(?,?)";
		private static final String sp_select_company_detail_by_dataset = "call sp_select_company_detail_by_dataset(?,?,?)";
		private static final String sp_select_company_by_statistic = "call sp_select_company_by_statistic(?)";
		private static final String sp_select_district_below = "call sp_select_district_below(?)";
		private static final String sp_select_company_detail_by_id = "call sp_select_company_detail_by_id(?)";
		private static final String sp_select_data_SHP_taiwan = "call sp_select_data_SHP_taiwan()";
		private static final String sp_select_company_district_by_source = "call sp_select_company_district_by_source(?,?,?)";
		private static final String sp_select_company_register_by_keyword = "call sp_select_company_register_by_keyword(?,'','','')";
		 
		
		public List<Object> select_company_by_statistic(CompanyRegisterVO requestVO) {
			List<Object> list = new ArrayList<Object>();
			JSONObject vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_company_by_statistic);
				pstmt.setString(1, requestVO.getRequest_statistic());
				
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					vo = new JSONObject();
					for (int i = 1; i <= columnCount; i++ ) {
						  vo.put(null2str(rsmd.getColumnName(i)),null2str(rs.getString(i)));
						  
					}
					// = null2str(rs.getString(0));
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
		@Override
		public List<CompanyRegisterVO> select_company_by(CompanyRegisterVO requestVO) {
			List<CompanyRegisterVO> list = new ArrayList<CompanyRegisterVO>();
			CompanyRegisterVO vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				String sp="";
				if("select_company_by_type".equals(requestVO.getRequest_command())){
					sp = sp_select_company_by_type;
					pstmt = con.prepareStatement(sp);
					pstmt.setString(1, requestVO.getRequest_type());
					pstmt.setString(2, requestVO.getRequest_keyword());
					
				}else if("select_company_by_dataset".equals(requestVO.getRequest_command())){
					sp = sp_select_company_by_dataset;
					pstmt = con.prepareStatement(sp);
					pstmt.setString(1, requestVO.getRequest_dataset());
					pstmt.setString(2, requestVO.getRequest_keyword());
				}
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					vo = new CompanyRegisterVO();
					vo.setCity_name(null2str(rs.getString("city_name")));
					vo.setCount_n(null2str(rs.getString("count_n")));
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
		@Override
		public List<CompanyRegisterVO> select_company_detail_by(CompanyRegisterVO requestVO) {
			List<CompanyRegisterVO> list = new ArrayList<CompanyRegisterVO>();
			CompanyRegisterVO vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				String sp="";
				if("select_company_detail_by_type".equals(requestVO.getRequest_command())){
					sp = sp_select_company_detail_by_type;
					pstmt = con.prepareStatement(sp);
					
					pstmt.setString(1, requestVO.getRequest_type());
					pstmt.setString(2, requestVO.getRequest_keyword());
					pstmt.setString(3, requestVO.getRequest_city()+requestVO.getRequest_town());
					
				}else if("select_company_detail_by_dataset".equals(requestVO.getRequest_command())){
					sp = sp_select_company_detail_by_dataset;
					pstmt = con.prepareStatement(sp);
					
					pstmt.setString(1, requestVO.getRequest_dataset());
					pstmt.setString(2, requestVO.getRequest_keyword());
					pstmt.setString(3, requestVO.getRequest_city()+requestVO.getRequest_town());
					
				}else if("select_company_by_statistic".equals(requestVO.getRequest_command())){
					//暫無
					sp = "暫無";
					pstmt = con.prepareStatement(sp);
					pstmt.setString(1, requestVO.getRequest_statistic());
					return null;
				}
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					vo = new CompanyRegisterVO();
					vo.setId(null2str(rs.getString("id")));
					vo.setCompany_name(null2str(rs.getString("company_name")));
					vo.setLat(null2str(rs.getString("lat")));
					vo.setLng(null2str(rs.getString("lng")));
					vo.setStatus(null2str(rs.getString("status")));
					
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
		@Override
		public List<CompanyRegisterVO> select_district_below(CompanyRegisterVO requestVO) {
			List<CompanyRegisterVO> list = new ArrayList<CompanyRegisterVO>();
			CompanyRegisterVO vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);

//				pstmt = con.prepareStatement(sp_select_district_below);
				pstmt = con.prepareStatement(sp_select_company_district_by_source);
				pstmt.setString(1, requestVO.getDistrict_name());
				pstmt.setString(2, requestVO.getRequest_source());
				pstmt.setString(3, requestVO.getRequest_keyword());
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					vo = new CompanyRegisterVO();
					vo.setDistrict_name(null2str(rs.getString("district_name")));
					if(!"".equals(vo.getDistrict_name()))list.add(vo); // Store the row in the list
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
		@Override
		public List<CompanyRegisterVO> select_company_detail_by_id(CompanyRegisterVO requestVO) {
			List<CompanyRegisterVO> list = new ArrayList<CompanyRegisterVO>();
			CompanyRegisterVO vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				String sp="";
				sp = sp_select_company_detail_by_id;
				pstmt = con.prepareStatement(sp);
				pstmt.setString(1, requestVO.getRequest_id());
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					vo = new CompanyRegisterVO();
					vo.setId(null2str(rs.getString("id")));
					vo.setSource(null2str(rs.getString("source")));
					vo.setEIN(null2str(rs.getString("EIN")));
					vo.setCompany_name(null2str(rs.getString("company_name")));
					vo.setAddress(null2str(rs.getString("address")));
					vo.setRepresentative(null2str(rs.getString("representative")));
					vo.setCapital_amount(null2str(rs.getString("capital_amount")));
					vo.setStatus(null2str(rs.getString("status")));
					vo.setLat(null2str(rs.getString("lat")));
					vo.setLng(null2str(rs.getString("lng")));
					vo.setCreation_date(null2str(rs.getString("creation_date")));
					vo.setModification_date(null2str(rs.getString("modification_date")));
					vo.setDisbanded_date(null2str(rs.getString("disbanded_date")));
					vo.setMemo(null2str(rs.getString("memo")));
					vo.setCreation_d(null2str(rs.getString("creation_d")));
					vo.setModification_d(null2str(rs.getString("modification_d")));
					vo.setDisbanded_d(null2str(rs.getString("disbanded_d")));
					vo.setType(null2str(rs.getString("type")));
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
		@Override
		public List<DataSHPVO> page_load_shp(CompanyRegisterVO requestVO) {
			// TODO Auto-generated method stub
			List<DataSHPVO> list = new ArrayList<DataSHPVO>();
			DataSHPVO vo = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_data_SHP_taiwan);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					vo = new DataSHPVO();
					
					vo.setSn(null2str(rs.getString("sn")));
					vo.setLevel(null2str(rs.getString("level")));
					vo.setDistrict_id(null2str(rs.getString("district_id")));
					vo.setDistrict_name(null2str(rs.getString("district_name")));
					vo.setGeom(null2str(rs.getString("geom")));
					vo.setParent_district_id(null2str(rs.getString("parent_district_id")));
					
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
	public String null2str(Object object) {
		return object == null ? "" : object.toString().trim();
	}
}
