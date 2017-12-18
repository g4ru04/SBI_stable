package tw.com.sbi.costliving.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import tw.com.sbi.vo.CostLivingVO;

public class CostLiving extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(CostLiving.class);
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		CostLivingService costLivingService = null;

		String action = request.getParameter("action");
		
		logger.debug("Action: " + action);
		
		if ("getCountry".equals(action)) {
			try {								
				costLivingService = new CostLivingService();
				List<CostLivingVO> list = costLivingService.selectCountry();
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("getCity".equals(action)) {
			try {
				String country = request.getParameter("country");
				
				costLivingService = new CostLivingService();
				List<CostLivingVO> list = costLivingService.selectCity(country);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("getBusinessType".equals(action)) {
			try {
				String country = request.getParameter("country");
				String city = request.getParameter("city");
				
				costLivingService = new CostLivingService();
				List<CostLivingVO> list = costLivingService.selectBusinessType(country, city);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("getSubType".equals(action)) {
			try {
				String country = request.getParameter("country");
				String city = request.getParameter("city");
				String businessType = request.getParameter("business_type");
				
				costLivingService = new CostLivingService();
				List<CostLivingVO> list = costLivingService.selectSubType(country, city, businessType);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("getProduct".equals(action)) {
			try {
				String country = request.getParameter("country");
				String city = request.getParameter("city");
				String businessType = request.getParameter("business_type");
				String subType = request.getParameter("sub_type");
				
				costLivingService = new CostLivingService();
				List<CostLivingVO> list = costLivingService.selectProduct(country, city, businessType, subType);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("getChart".equals(action)) {
			try {
				String country = request.getParameter("country");
				String city = request.getParameter("city");
				String businessType = request.getParameter("business_type");
				String subType = request.getParameter("sub_type");
				String fieldName = request.getParameter("field_name");
				String productId = request.getParameter("product_id");
				
				logger.debug("Country :" + country);
				logger.debug("City :" + city);
				logger.debug("Business Type :" + businessType);
				logger.debug("Sub Type :" + subType);
				logger.debug("Field Name :" + fieldName);
				logger.debug("Product ID :" + productId);
				
				costLivingService = new CostLivingService();
				List<CostLivingVO> list = costLivingService.selectChart(country, city, businessType, subType, fieldName, productId);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}
	}
	
	/*************************** 處理業務邏輯 ****************************************/
	public class CostLivingService {
		private costliving_interface dao;

		public CostLivingService() {
			dao = new CostLivingDAO();
		}

		public List<CostLivingVO> selectCountry() {
			return dao.selectCountry();
		}

		public List<CostLivingVO> selectCity(String country) {
			return dao.selectCity(country);
		}

		public List<CostLivingVO> selectBusinessType(String country, String city) {
			return dao.selectBusinessType(country, city);
		}

		public List<CostLivingVO> selectSubType(String country, String city, String businessType) {
			return dao.selectSubType(country, city, businessType);
		}

		public List<CostLivingVO> selectProduct(String country, String city, String businessType, String subType) {
			return dao.selectProduct(country, city, businessType, subType);
		}

		public List<CostLivingVO> selectChart(String country, String city, String businessType, String subType, String fieldName, String productId) {
			return dao.selectChart(country, city, businessType, subType, fieldName, productId);
		}
	}
	
	/*************************** 制定規章方法 ****************************************/
	interface costliving_interface {
		public List<CostLivingVO> selectCountry();
		public List<CostLivingVO> selectCity(String country);
		public List<CostLivingVO> selectBusinessType(String country, String city);
		public List<CostLivingVO> selectSubType(String country, String city, String businessType);
		public List<CostLivingVO> selectProduct(String country, String city, String businessType, String subType);
		public List<CostLivingVO> selectChart(String country, String city, String businessType, String subType, String fieldName, String productId);
	}
	
	/*************************** 操作資料庫 ****************************************/
	class CostLivingDAO implements costliving_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		// 會使用到的Stored procedure
		private static final String sp_select_STAT_Cost_Living_country = "call sp_select_STAT_Cost_Living_country()";
		private static final String sp_select_STAT_Cost_Living_city = "call sp_select_STAT_Cost_Living_city(?)";
		private static final String sp_select_STAT_Cost_Living_BusinessType = "call sp_select_STAT_Cost_Living_BusinessType(?,?)";
		private static final String sp_select_STAT_Cost_Living_SubType = "call sp_select_STAT_Cost_Living_SubType(?,?,?)";
		private static final String sp_select_STAT_Cost_Living_Product = "call sp_select_STAT_Cost_Living_Product(?,?,?,?)";
		private static final String sp_select_STAT_Cost_Living_Chart = "call sp_select_STAT_Cost_Living_Chart(?,?,?,?,?,?)";

		@Override
		public List<CostLivingVO> selectCountry() {
			List<CostLivingVO> list = new ArrayList<CostLivingVO>();
			CostLivingVO costLivingVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_STAT_Cost_Living_country);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					costLivingVO = new CostLivingVO();
					
					costLivingVO.setCountry(rs.getString("country") == null?"":rs.getString("country"));
					
					list.add(costLivingVO); // Store the row in the list
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
		public List<CostLivingVO> selectCity(String country) {
			List<CostLivingVO> list = new ArrayList<CostLivingVO>();
			CostLivingVO costLivingVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_STAT_Cost_Living_city);
				pstmt.setString(1, country);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					costLivingVO = new CostLivingVO();
					
					costLivingVO.setCity(rs.getString("city") == null?"":rs.getString("city"));
					costLivingVO.setUnit(rs.getString("unit") == null?"":rs.getString("unit"));
					
					list.add(costLivingVO); // Store the row in the list
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
		public List<CostLivingVO> selectBusinessType(String country, String city) {
			List<CostLivingVO> list = new ArrayList<CostLivingVO>();
			CostLivingVO costLivingVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_STAT_Cost_Living_BusinessType);
				pstmt.setString(1, country);
				pstmt.setString(2, city);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					costLivingVO = new CostLivingVO();
					
					costLivingVO.setBusinessType(rs.getString("Business_Type") == null?"":rs.getString("Business_Type"));
					
					list.add(costLivingVO); // Store the row in the list
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
		public List<CostLivingVO> selectSubType(String country, String city, String businessType) {
			List<CostLivingVO> list = new ArrayList<CostLivingVO>();
			CostLivingVO costLivingVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_STAT_Cost_Living_SubType);
				pstmt.setString(1, country);
				pstmt.setString(2, city);
				pstmt.setString(3, businessType);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					costLivingVO = new CostLivingVO();
					
					costLivingVO.setSubType(rs.getString("Sub_Type") == null?"":rs.getString("Sub_Type"));
					
					list.add(costLivingVO); // Store the row in the list
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
		public List<CostLivingVO> selectProduct(String country, String city, String businessType, String subType) {
			List<CostLivingVO> list = new ArrayList<CostLivingVO>();
			CostLivingVO costLivingVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_STAT_Cost_Living_Product);
				pstmt.setString(1, country);
				pstmt.setString(2, city);
				pstmt.setString(3, businessType);
				pstmt.setString(4, subType);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					costLivingVO = new CostLivingVO();
					
					costLivingVO.setProductName(rs.getString("Product_Name") == null?"":rs.getString("Product_Name"));
					costLivingVO.setProductId(rs.getString("Product_ID") == null?"":rs.getString("Product_ID"));
					
					list.add(costLivingVO); // Store the row in the list
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
		public List<CostLivingVO> selectChart(String country, String city, String businessType, String subType, String fieldName, String productId) {
			List<CostLivingVO> list = new ArrayList<CostLivingVO>();
			CostLivingVO costLivingVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_STAT_Cost_Living_Chart);
				pstmt.setString(1, country);
				pstmt.setString(2, city);
				pstmt.setString(3, businessType);
				pstmt.setString(4, subType);
				pstmt.setString(5, fieldName);
				pstmt.setString(6, productId);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					costLivingVO = new CostLivingVO();
					
					costLivingVO.setProductName(rs.getString("Product_Name") == null?"":rs.getString("Product_Name"));
					costLivingVO.setResult(rs.getString("result") == null?"":rs.getString("result"));
					costLivingVO.setUnit(rs.getString("UNIT") == null?"":rs.getString("UNIT"));
					
					list.add(costLivingVO); // Store the row in the list
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

}
