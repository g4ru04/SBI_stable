package tw.com.sbi.organization.controller;

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

public class User extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(User.class);
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.trace("user doPost.");

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		UserService userService = null;
		String action = request.getParameter("action");
		String group_id = request.getSession().getAttribute("group_id").toString();
		String user_id = request.getSession().getAttribute("user_id").toString();
		
		if ("search".equals(action)) {
			try {
				/*************************** 1.��隢�� ****************************************/
				String user_name = request.getParameter("user_name");
				
				/*************************** 2.���閰Ｚ��� ****************************************/
				// ����閰Ｘ�辣嚗��閰Ｗ�
				if (user_name == null || (user_name.trim()).length() == 0) {
					userService = new UserService();
					List<UserBean> list = userService.getSearchAllDB(group_id);
					request.setAttribute("action", "searchResults");
					request.setAttribute("list", list);
					
					Gson gson = new Gson();
					String jsonStrList = gson.toJson(list);
					response.getWriter().write(jsonStrList);
					
					return; 
				}
				
				/*************************** �隞���隤方��� **********************************/
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
				return;
			}
		} else if ("selectAll".equals(action)) {
			try {
				/*************************** 1.��隢�� ****************************************/
				
				/*************************** 2.���閰Ｚ��� ****************************************/
				userService = new UserService();
				List<UserBean> list = userService.getSearchAllDB(group_id);
				request.setAttribute("action", "searchResults");
				request.setAttribute("list", list);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
				return; 

				/*************************** �隞���隤方��� **********************************/
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
				return;
			}
		} else if ("getUserByGroup".equals(action)) {
			try {
				/*************************** 1.��隢�� ****************************************/
				group_id = request.getParameter("group_id");
				
				/*************************** 2.���閰Ｚ��� ****************************************/
				userService = new UserService();
				List<UserBean> list = userService.getSearchAllDB(group_id);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
				return;				
				
				/*************************** �隞���隤方��� **********************************/
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
				return;
			}
		}
	}
	
	/************************* 撠���澈銵冽�撘� **************************************/
	public class UserBean implements java.io.Serializable {
		private String user_id;
		private String group_id;
		private String user_name;
		private String email;
		private String password;
		private String administrator;
		
		public String getUser_id() {
			return user_id;
		}
		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}
		public String getGroup_id() {
			return group_id;
		}
		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}
		public String getUser_name() {
			return user_name;
		}
		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getAdministrator() {
			return administrator;
		}
		public void setAdministrator(String administrator) {
			this.administrator = administrator;
		}
	}

	/*************************** �摰��瘜� ****************************************/
	interface User_interface {

		public void insertDB(UserBean userBean);

		public void updateDB(UserBean userBean);

		public void deleteDB(String user_id,String operation);

		public List<UserBean> searchAllDB(String group_id);
	}

	/*************************** ���平���摩 ****************************************/
	class UserService {
		private User_interface dao;

		public UserService() {
			dao = new UserDAO();
		}

		public UserBean addUser(String group_id, String user_name, String email, String password, String administrator) {
			UserBean userBean = new UserBean();
			
			userBean.setGroup_id(group_id);
			userBean.setUser_name(user_name);
			userBean.setEmail(email);
			userBean.setPassword(password);
			userBean.setAdministrator(administrator);
			
			dao.insertDB(userBean);
			return userBean;
		}

		public UserBean updateUser(String user_id, String group_id, String user_name, String email, String administrator) {
			UserBean userBean = new UserBean();
			
			userBean.setUser_id(user_id);
			userBean.setGroup_id(group_id);
			userBean.setUser_name(user_name);
			userBean.setEmail(email);
			userBean.setAdministrator(administrator);
			
			dao.updateDB(userBean);
			return userBean;
		}

		public void deleteUser(String user_id,String operation) {
			dao.deleteDB(user_id,operation);
		}

		public List<UserBean> getSearchAllDB(String group_id) {
			return dao.searchAllDB(group_id);
		}
	}

	/*************************** �����澈 ****************************************/
	class UserDAO implements User_interface {
		// ��蝙����tored procedure	
		private static final String sp_insert_user = "call sp_insert_user(?, ?, ?, ?, ?)";
		private static final String sp_update_user = "call sp_update_user(?, ?, ?, ?, ?)";
		private static final String sp_del_user = "call sp_del_user(?, ?)";
		private static final String sp_selectall_user = "call sp_selectall_user(?)";

		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL");
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");

		@Override
		public void insertDB(UserBean userBean) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_insert_user);

				pstmt.setString(1, userBean.getGroup_id());
				pstmt.setString(2, userBean.getUser_name());
				pstmt.setString(3, userBean.getEmail());
				pstmt.setString(4, userBean.getPassword());
				pstmt.setString(5, userBean.getAdministrator());
				
				pstmt.executeUpdate();

			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} finally {
				// Clean up JDBC resources
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
		public void updateDB(UserBean userBean) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_update_user);
				
				logger.trace("sp_update_user");
				logger.trace("1:" + userBean.getUser_id());
				logger.trace("2:" + userBean.getGroup_id());
				logger.trace("3:" + userBean.getUser_name());
				logger.trace("4:" + userBean.getEmail());
				logger.trace("5:" + userBean.getAdministrator());
				
				pstmt.setString(1, userBean.getUser_id());
				pstmt.setString(2, userBean.getGroup_id());
				pstmt.setString(3, userBean.getUser_name());
				pstmt.setString(4, userBean.getEmail());
				pstmt.setString(5, userBean.getAdministrator());
				
				pstmt.executeUpdate();
				
			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} finally {
				// Clean up JDBC resources
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
		public void deleteDB(String user_id,String operation) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_del_user);
				pstmt.setString(1, user_id);
				pstmt.setString(2, operation);

				pstmt.executeUpdate();
				
			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} finally {
				// Clean up JDBC resources
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
		public List<UserBean> searchAllDB(String group_id) {
			List<UserBean> list = new ArrayList<UserBean>();
			UserBean userBean = null;

			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_selectall_user);
				pstmt.setString(1, group_id);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					userBean = new UserBean();
					userBean.setUser_id(rs.getString("user_id"));
					userBean.setGroup_id(rs.getString("group_id"));
					userBean.setUser_name(rs.getString("user_name"));
					userBean.setEmail(rs.getString("email"));
					userBean.setPassword(rs.getString("password"));
					userBean.setAdministrator(rs.getString("administrator"));
					list.add(userBean);
				}

				
			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				// Handle ClassNotFoundException errors
				throw new RuntimeException("A ClassNotFoundException error occured. " + cnfe.getMessage());
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
