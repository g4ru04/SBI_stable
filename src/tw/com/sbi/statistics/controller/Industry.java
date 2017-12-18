package tw.com.sbi.statistics.controller;

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

import tw.com.sbi.vo.IndustryVO;

public class Industry extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = LogManager.getLogger(Industry.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		StatisticsService statisticsService = null;

		String action = request.getParameter("action");
		
		logger.debug("Action: " + action);
		try {
			if ("getCountry".equals(action)) {

				statisticsService = new StatisticsService();
				List<IndustryVO> list = statisticsService.selectCountry();

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getType".equals(action)) {
				String country = request.getParameter("country");

				IndustryVO paramVO = new IndustryVO();
				paramVO.setCountry(country);

				statisticsService = new StatisticsService();
				List<IndustryVO> list = statisticsService.selectType(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getSource".equals(action)) {
				String country = request.getParameter("country");
				String industryType = request.getParameter("industry_type");

				IndustryVO paramVO = new IndustryVO();
				paramVO.setCountry(country);
				paramVO.setIndustryType(industryType);

				statisticsService = new StatisticsService();
				List<IndustryVO> list = statisticsService.selectSource(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getSubsource".equals(action)) {
				String country = request.getParameter("country");
				String industryType = request.getParameter("industry_type");
				String source = request.getParameter("source");

				IndustryVO paramVO = new IndustryVO();
				paramVO.setCountry(country);
				paramVO.setIndustryType(industryType);
				paramVO.setSource(source);

				statisticsService = new StatisticsService();
				List<IndustryVO> list = statisticsService.selectSubsource(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getCategories".equals(action)) {
				String country = request.getParameter("country");
				String industryType = request.getParameter("industry_type");
				String source = request.getParameter("source");
				String subsource = request.getParameter("subsource");

				IndustryVO paramVO = new IndustryVO();
				paramVO.setCountry(country);
				paramVO.setIndustryType(industryType);
				paramVO.setSource(source);
				paramVO.setSubsource(subsource);

				statisticsService = new StatisticsService();
				List<IndustryVO> list = statisticsService.selectCategories(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getCategoriesYear".equals(action)) {
				String country = request.getParameter("country");
				String industryType = request.getParameter("industry_type");
				String source = request.getParameter("source");
				String subsource = request.getParameter("subsource");
				String categories = request.getParameter("categories");

				IndustryVO paramVO = new IndustryVO();
				paramVO.setCountry(country);
				paramVO.setIndustryType(industryType);
				paramVO.setSource(source);
				paramVO.setSubsource(subsource);
				paramVO.setCategories(categories);

				statisticsService = new StatisticsService();
				List<IndustryVO> list = statisticsService.selectCategoriesYear(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getChart".equals(action)) {
				String country = request.getParameter("country");
				String industryType = request.getParameter("industry_type");
				String source = request.getParameter("source");
				String subsource = request.getParameter("subsource");
				String categories = request.getParameter("categories");
				String categoriesYear = request.getParameter("categories_year");

				IndustryVO paramVO = new IndustryVO();
				paramVO.setCountry(country);
				paramVO.setIndustryType(industryType);
				paramVO.setSource(source);
				paramVO.setSubsource(subsource);
				paramVO.setCategories(categories);
				paramVO.setCategoriesYear(categoriesYear);

				logger.debug("Country :" + country);
				logger.debug("Industry Type :" + industryType);
				logger.debug("Source :" + source);
				logger.debug("Subsource :" + subsource);
				logger.debug("Categories :" + categories);
				logger.debug("Category Year :" + categoriesYear);

				statisticsService = new StatisticsService();
				List<IndustryVO> list = statisticsService.selectChart(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("full-range-log", e);
		}
	}
	
	/*************************** ���平���摩 ****************************************/
	public class StatisticsService {
		private statistics_interface dao;

		public StatisticsService() {
			dao = new StatisticsDAO();
		}
		
		public List<IndustryVO> selectCountry() {
			return dao.selectCountry();
		}

		public List<IndustryVO> selectType(IndustryVO paramVO) {
			return dao.selectType(paramVO);
		}

		public List<IndustryVO> selectSource(IndustryVO paramVO) {
			return dao.selectSource(paramVO);
		}

		public List<IndustryVO> selectSubsource(IndustryVO paramVO) {
			return dao.selectSubsource(paramVO);
		}

		public List<IndustryVO> selectCategories(IndustryVO paramVO) {
			return dao.selectCategories(paramVO);
		}

		public List<IndustryVO> selectCategoriesYear(IndustryVO paramVO) {
			return dao.selectCategoriesYear(paramVO);
		}

		public List<IndustryVO> selectChart(IndustryVO paramVO) {
			return dao.selectChart(paramVO);
		}

	}
	
	/*************************** �摰��瘜� ****************************************/
	interface statistics_interface {
		public List<IndustryVO> selectCountry();
		public List<IndustryVO> selectType(IndustryVO industryVO);
		public List<IndustryVO> selectSource(IndustryVO industryVO);
		public List<IndustryVO> selectSubsource(IndustryVO industryVO);
		public List<IndustryVO> selectCategories(IndustryVO industryVO);
		public List<IndustryVO> selectCategoriesYear(IndustryVO industryVO);
		public List<IndustryVO> selectChart(IndustryVO industryVO);
		
	}
	
	/*************************** �����澈 ****************************************/
	class StatisticsDAO implements statistics_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		// ��蝙����tored procedure
		private static final String sp_get_Industry_country = "call sp_get_Industry_country()";
		private static final String sp_get_Industry_Type = "call sp_get_Industry_Type(?)";
		private static final String sp_get_Industry_Source = "call sp_get_Industry_Source(?,?)";
		private static final String sp_get_Industry_SubSource = "call sp_get_Industry_SubSource(?,?,?)";
		private static final String sp_get_Industry_Categories = "call sp_get_Industry_Categories(?,?,?,?)";
		private static final String sp_get_Industry_CategoriesYear = "call sp_get_Industry_CategoriesYear(?,?,?,?,?)";
		private static final String sp_get_Industry_Chart = "call sp_get_Industry_Chart(?,?,?,?,?,?)";

		@Override
		public List<IndustryVO> selectCountry() {
			List<IndustryVO> list = new ArrayList<IndustryVO>();
			IndustryVO industryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_Industry_country);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					industryVO = new IndustryVO();
					
					
					industryVO.setCountry(rs.getString("Country") == null?"":rs.getString("Country"));
					
					if ( !industryVO.getCountry().equals("") ) {
						list.add(industryVO); // Store the row in the list
					}
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
		public List<IndustryVO> selectType(IndustryVO paramVO) {
			List<IndustryVO> list = new ArrayList<IndustryVO>();
			IndustryVO industryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_Industry_Type);
				pstmt.setString(1, paramVO.getCountry());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					industryVO = new IndustryVO();
					
					industryVO.setIndustryType(rs.getString("IndustryType") == null?"":rs.getString("IndustryType"));
					
					if (! industryVO.getIndustryType().equals("") ) {
						list.add(industryVO); // Store the row in the list
					}
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
		public List<IndustryVO> selectSource(IndustryVO paramVO) {
			List<IndustryVO> list = new ArrayList<IndustryVO>();
			IndustryVO industryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_Industry_Source);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getIndustryType());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					industryVO = new IndustryVO();
					
					industryVO.setSource(rs.getString("Source") == null?"":rs.getString("Source"));
					
					list.add(industryVO); // Store the row in the list
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
		public List<IndustryVO> selectSubsource(IndustryVO paramVO) {
			List<IndustryVO> list = new ArrayList<IndustryVO>();
			IndustryVO industryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_Industry_SubSource);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getIndustryType());
				pstmt.setString(3, paramVO.getSource());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					industryVO = new IndustryVO();
					
					industryVO.setSubsource(rs.getString("SubSource") == null?"":rs.getString("SubSource"));
					
					list.add(industryVO); // Store the row in the list
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
		public List<IndustryVO> selectCategories(IndustryVO paramVO) {
			List<IndustryVO> list = new ArrayList<IndustryVO>();
			IndustryVO industryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_Industry_Categories);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getIndustryType());
				pstmt.setString(3, paramVO.getSource());
				pstmt.setString(4, paramVO.getSubsource());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					industryVO = new IndustryVO();
					
					industryVO.setCategories(rs.getString("Categories") == null?"":rs.getString("Categories"));
					
					list.add(industryVO); // Store the row in the list
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
		public List<IndustryVO> selectCategoriesYear(IndustryVO paramVO) {
			List<IndustryVO> list = new ArrayList<IndustryVO>();
			IndustryVO industryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_Industry_CategoriesYear);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getIndustryType());
				pstmt.setString(3, paramVO.getSource());
				pstmt.setString(4, paramVO.getSubsource());
				pstmt.setString(5, paramVO.getCategories());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					industryVO = new IndustryVO();
					
					industryVO.setCategoriesYear(rs.getString("CategoriesYear") == null?"":rs.getString("CategoriesYear"));
					
					list.add(industryVO); // Store the row in the list
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
		public List<IndustryVO> selectChart(IndustryVO paramVO) {
			List<IndustryVO> list = new ArrayList<IndustryVO>();
			IndustryVO industryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_Industry_Chart);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getIndustryType());
				pstmt.setString(3, paramVO.getSource());
				pstmt.setString(4, paramVO.getSubsource());
				pstmt.setString(5, paramVO.getCategories());
				pstmt.setString(6, paramVO.getCategoriesYear());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					industryVO = new IndustryVO();
					
					industryVO.setCategories(rs.getString("Categories") == null?"":rs.getString("Categories"));
					industryVO.setCategoriesYear(rs.getString("CategoriesYear") == null?"":rs.getString("CategoriesYear"));
					industryVO.setCategoriesData(rs.getString("CategoriesData") == null?"":rs.getString("CategoriesData"));
					industryVO.setUnit(rs.getString("UNIT") == null?"":rs.getString("UNIT"));
					list.add(industryVO); // Store the row in the list
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
