package tw.com.sbi.organization.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tw.com.sbi.vo.GroupVO;

public class GroupBackstage extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(GroupBackstage.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		GroupBackstageService service = null;
		GroupVO groupVO = null;

		String action = request.getParameter("action");

		logger.debug("action: " + action);

		String[] actions = { "search", "insert", "update", "delete" };

		int key = Arrays.asList(actions).indexOf(action);

		logger.debug("key: " + key);

		switch (key) {

		case 0: {
			String group_name = request.getParameter("group_name");

			service = new GroupBackstageService();

			groupVO = new GroupVO();
			groupVO.setGroup_name(null2str(group_name));
			
			List<GroupVO> list = service.getGroups(groupVO);

			logger.debug("result count: " + list.size());
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			String jsonStr = gson.toJson(list);
			response.getWriter().write(jsonStr);
			break;
		}
		case 1: {
			String user_id = request.getSession().getAttribute("user_id").toString();
			String group_name = request.getParameter("name");

			String format = "\n" + "user_id : {}\n" + "group_name : {}\n" ;

			logger.debug(format, user_id, group_name);

			groupVO = new GroupVO();
			groupVO.setUser_id(user_id);
			groupVO.setGroup_name(group_name);

			service = new GroupBackstageService();
			service.insert(groupVO);

			break;
		}
		case 2: {
			String user_id = request.getSession().getAttribute("user_id").toString();
			String group_id = request.getParameter("group_id");
			String group_name = request.getParameter("name");
			
			String format = "\n" + "user_id : {}\n" + "group_id : {}\n" + "group_name : {}\n";

			logger.debug(format, user_id, group_id, group_name);

			groupVO = new GroupVO();
			groupVO.setUser_id(user_id);
			groupVO.setGroup_id(group_id);
			groupVO.setGroup_name(group_name);

			service = new GroupBackstageService();
			service.update(groupVO);

			break;
		}
		case 3: {
			String user_id = request.getSession().getAttribute("user_id").toString();
			String[] group_ids = request.getParameter("group_id").split(";");

			String format = "\n" + "user_id : {}\n" + "group_ids : {}\n";

			logger.debug(format, user_id, group_ids);

			for (String group_id : group_ids) {
				groupVO = new GroupVO();
				groupVO.setUser_id(user_id);
				groupVO.setGroup_id(group_id);

				service = new GroupBackstageService();
				service.delete(groupVO);
			}
			break;
		}
		default: {
			String user_id = request.getSession().getAttribute("user_id").toString();
			logger.debug("user_id: " + user_id);
			break;
		}
		}
	}

	class GroupBackstageService {
		private groupBackstage_interface dao;

		public GroupBackstageService() {
			dao = new GroupBackstageDAO();
		}

		public List<GroupVO> getGroups(GroupVO groupVO) {
			return dao.searchDB(groupVO);
		}

		public void insert(GroupVO groupVO) {
			dao.insertDB(groupVO);
		}

		public void update(GroupVO groupVO) {
			dao.updateDB(groupVO);
		}

		public void delete(GroupVO groupVO) {
			dao.deleteDB(groupVO);
		}
	}

	class GroupBackstageDAO implements groupBackstage_interface {

		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");

		// stored procedure
		private static final String sp_get_group = "call sp_get_group(?)";
		private static final String sp_insert_group = "call sp_insert_group(?,?)";
		private static final String sp_update_group = "call sp_update_group(?,?,?)";
		private static final String sp_del_group = "call sp_del_group(?,?)";

		@Override
		public void insertDB(GroupVO groupVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_insert_group);

				pstmt.setString(1, null2str(groupVO.getUser_id()));
				pstmt.setString(2, null2str(groupVO.getGroup_name()));
				
				rs = pstmt.executeQuery();

			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
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
		}

		@Override
		public void updateDB(GroupVO groupVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_update_group);

				pstmt.setString(1, null2str(groupVO.getUser_id()));
				pstmt.setString(2, null2str(groupVO.getGroup_id()));
				pstmt.setString(3, null2str(groupVO.getGroup_name()));
				
				rs = pstmt.executeQuery();

			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
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
		}

		@Override
		public void deleteDB(GroupVO groupVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_del_group);

				pstmt.setString(1, null2str(groupVO.getGroup_id()));
				pstmt.setString(2, null2str(groupVO.getUser_id()));

				rs = pstmt.executeQuery();

			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
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
		}

		@Override
		public List<GroupVO> searchDB(GroupVO groupVO) {
			List<GroupVO> list = new ArrayList<GroupVO>();
			GroupVO result = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_group);

				pstmt.setString(1, null2str(groupVO.getGroup_name()));
				
				rs = pstmt.executeQuery();

				String[] columns = { "group_id", "group_name" };

				while (rs.next()) {
					result = new GroupVO();

					result.setGroup_id(null2str(rs.getString(columns[0])));
					result.setGroup_name(null2str(rs.getString(columns[1])));
					
					list.add(result);
				}
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
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

	public java.sql.Date dateFormat(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date sDate = null;
		try {
			java.util.Date uDate = sdf.parse(s);
			sDate = new java.sql.Date(uDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			logger.debug("full-range-log",e);
		}
		return sDate;
	}
}

interface groupBackstage_interface {

	public void insertDB(GroupVO groupVO);

	public void updateDB(GroupVO groupVO);

	public void deleteDB(GroupVO groupVO);

	public List<GroupVO> searchDB(GroupVO groupVO);
}
