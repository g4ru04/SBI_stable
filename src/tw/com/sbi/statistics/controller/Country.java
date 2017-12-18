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

import tw.com.sbi.vo.CountryVO;

public class Country extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = LogManager.getLogger(Country.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		CountryService countryService = null;

		String action = request.getParameter("action");
		
		logger.debug("Action: " + action);
		try {
			if ("getCountry".equals(action)) {

				countryService = new CountryService();
				List<CountryVO> list = countryService.selectCountry();

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getStructure".equals(action)) {
				String country = request.getParameter("country");

				CountryVO paramVO = new CountryVO();
				paramVO.setCountry(country);

				countryService = new CountryService();
				List<CountryVO> list = countryService.selectStructure(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getDimensions".equals(action)) {
				String country = request.getParameter("country");
				String structure = request.getParameter("structure");

				CountryVO paramVO = new CountryVO();
				paramVO.setCountry(country);
				paramVO.setStructure(structure);

				countryService = new CountryService();
				List<CountryVO> list = countryService.selectDimensions(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getTarget".equals(action)) {
				String country = request.getParameter("country");
				String structure = request.getParameter("structure");
				String dimensions = request.getParameter("dimensions");

				CountryVO paramVO = new CountryVO();
				paramVO.setCountry(country);
				paramVO.setStructure(structure);
				paramVO.setDimensions(dimensions);

				countryService = new CountryService();
				List<CountryVO> list = countryService.selectTarget(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getSecondTarget".equals(action)) {
				String country = request.getParameter("country");
				String structure = request.getParameter("structure");
				String dimensions = request.getParameter("dimensions");
				String target = request.getParameter("target");

				CountryVO paramVO = new CountryVO();
				paramVO.setCountry(country);
				paramVO.setStructure(structure);
				paramVO.setDimensions(dimensions);
				paramVO.setTarget(target);

				countryService = new CountryService();
				List<CountryVO> list = countryService.selectSecondTarget(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getType".equals(action)) {
				String country = request.getParameter("country");
				String structure = request.getParameter("structure");
				String dimensions = request.getParameter("dimensions");
				String target = request.getParameter("target");
				String secondTarget = request.getParameter("second_target");

				CountryVO paramVO = new CountryVO();
				paramVO.setCountry(country);
				paramVO.setStructure(structure);
				paramVO.setDimensions(dimensions);
				paramVO.setTarget(target);
				paramVO.setSecondTarget(secondTarget);

				countryService = new CountryService();
				List<CountryVO> list = countryService.selectType(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);

			} else if ("getChart".equals(action)) {
				String country = request.getParameter("country");
				String structure = request.getParameter("structure");
				String dimensions = request.getParameter("dimensions");
				String target = request.getParameter("target");
				String secondTarget = request.getParameter("second_target");
				String type = request.getParameter("type");

				CountryVO paramVO = new CountryVO();
				paramVO.setCountry(country);
				paramVO.setStructure(structure);
				paramVO.setDimensions(dimensions);
				paramVO.setTarget(target);
				paramVO.setSecondTarget(secondTarget);
				paramVO.setType(type);

				logger.debug("Country :" + country);
				logger.debug("Structure :" + structure);
				logger.debug("Dimensions :" + dimensions);
				logger.debug("Target :" + target);
				logger.debug("Second Target :" + secondTarget);
				logger.debug("Type :" + type);

				countryService = new CountryService();
				List<CountryVO> list = countryService.selectChart(paramVO);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				logger.debug("jsonStrList :" + jsonStrList);
				response.getWriter().write(jsonStrList);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("full-range-log", e);
		}
	}
	
	/*************************** ���平���摩 ****************************************/
	public class CountryService {
		private country_interface dao;

		public CountryService() {
			dao = new CountryDAO();
		}
		
		public List<CountryVO> selectCountry() {
			return dao.selectCountry();
		}

		public List<CountryVO> selectStructure(CountryVO paramVO) {
			return dao.selectStructure(paramVO);
		}

		public List<CountryVO> selectDimensions(CountryVO paramVO) {
			return dao.selectDimensions(paramVO);
		}

		public List<CountryVO> selectTarget(CountryVO paramVO) {
			return dao.selectTarget(paramVO);
		}

		public List<CountryVO> selectSecondTarget(CountryVO paramVO) {
			return dao.selectSecondTarget(paramVO);
		}

		public List<CountryVO> selectType(CountryVO paramVO) {
			return dao.selectType(paramVO);
		}

		public List<CountryVO> selectChart(CountryVO paramVO) {
			return dao.selectChart(paramVO);
		}

	}
	
	/*************************** �摰��瘜� ****************************************/
	interface country_interface {
		public List<CountryVO> selectCountry();
		public List<CountryVO> selectStructure(CountryVO paramVO);
		public List<CountryVO> selectDimensions(CountryVO paramVO);
		public List<CountryVO> selectTarget(CountryVO paramVO);
		public List<CountryVO> selectSecondTarget(CountryVO paramVO);
		public List<CountryVO> selectType(CountryVO paramVO);
		public List<CountryVO> selectChart(CountryVO paramVO);
		
	}
	
	/*************************** �����澈 ****************************************/
	class CountryDAO implements country_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		// ��蝙����tored procedure
		private static final String sp_STAT_country = "call sp_STAT_country()";
		private static final String sp_STAT_country_Structure = "call sp_STAT_country_Structure(?)";
		private static final String sp_STAT_country_Dimensions = "call sp_STAT_country_Dimensions(?,?)";
		private static final String sp_STAT_country_Target = "call sp_STAT_country_Target(?,?,?)";
		private static final String sp_STAT_country_SecondTarget = "call sp_STAT_country_SecondTarget(?,?,?,?)";
		private static final String sp_STAT_country_TYPE = "call sp_STAT_country_TYPE(?,?,?,?,?)";
		private static final String sp_STAT_Country_Chart = "call sp_STAT_Country_Chart(?,?,?,?,?,?)";

		@Override
		public List<CountryVO> selectCountry() {
			List<CountryVO> list = new ArrayList<CountryVO>();
			CountryVO countryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_STAT_country);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					countryVO = new CountryVO();
					
					countryVO.setCountry(rs.getString("Country") == null?"":rs.getString("Country"));
					
					list.add(countryVO); // Store the row in the list
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
		public List<CountryVO> selectStructure(CountryVO paramVO) {
			List<CountryVO> list = new ArrayList<CountryVO>();
			CountryVO countryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_STAT_country_Structure);
				pstmt.setString(1, paramVO.getCountry());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					countryVO = new CountryVO();
					
					countryVO.setStructure(rs.getString("Structure") == null?"":rs.getString("Structure"));
					
					list.add(countryVO); // Store the row in the list
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
		public List<CountryVO> selectDimensions(CountryVO paramVO) {
			List<CountryVO> list = new ArrayList<CountryVO>();
			CountryVO countryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_STAT_country_Dimensions);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getStructure());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					countryVO = new CountryVO();
					
					countryVO.setDimensions(rs.getString("Dimensions") == null?"":rs.getString("Dimensions"));
					
					list.add(countryVO); // Store the row in the list
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
		public List<CountryVO> selectTarget(CountryVO paramVO) {
			List<CountryVO> list = new ArrayList<CountryVO>();
			CountryVO countryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_STAT_country_Target);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getStructure());
				pstmt.setString(3, paramVO.getDimensions());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					countryVO = new CountryVO();
					
					countryVO.setTarget(rs.getString("Target") == null?"":rs.getString("Target"));
					
					list.add(countryVO); // Store the row in the list
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
		public List<CountryVO> selectSecondTarget(CountryVO paramVO) {
			List<CountryVO> list = new ArrayList<CountryVO>();
			CountryVO countryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_STAT_country_SecondTarget);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getStructure());
				pstmt.setString(3, paramVO.getDimensions());
				pstmt.setString(4, paramVO.getTarget());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					countryVO = new CountryVO();
					
					countryVO.setSecondTarget(rs.getString("Second_Target") == null?"":rs.getString("Second_Target"));
					
					list.add(countryVO); // Store the row in the list
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
		public List<CountryVO> selectType(CountryVO paramVO) {
			List<CountryVO> list = new ArrayList<CountryVO>();
			CountryVO countryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_STAT_country_TYPE);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getStructure());
				pstmt.setString(3, paramVO.getDimensions());
				pstmt.setString(4, paramVO.getTarget());
				pstmt.setString(5, paramVO.getSecondTarget());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					countryVO = new CountryVO();
					
					countryVO.setType(rs.getString("TYPE") == null?"":rs.getString("TYPE"));
					
					list.add(countryVO); // Store the row in the list
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
		public List<CountryVO> selectChart(CountryVO paramVO) {
			List<CountryVO> list = new ArrayList<CountryVO>();
			CountryVO countryVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_STAT_Country_Chart);
				pstmt.setString(1, paramVO.getCountry());
				pstmt.setString(2, paramVO.getStructure());
				pstmt.setString(3, paramVO.getDimensions());
				pstmt.setString(4, paramVO.getTarget());
				pstmt.setString(5, paramVO.getSecondTarget());
				pstmt.setString(6, paramVO.getType());
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					countryVO = new CountryVO();
					
					countryVO.setSecondTarget(rs.getString("Second_Target") == null?"":rs.getString("Second_Target"));
					countryVO.setType(rs.getString("Type") == null?"":rs.getString("Type"));
					countryVO.setData(rs.getString("Data") == null?"":rs.getString("Data"));
					countryVO.setUnit(rs.getString("UNIT") == null?"":rs.getString("UNIT"));
					
					list.add(countryVO); // Store the row in the list
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
