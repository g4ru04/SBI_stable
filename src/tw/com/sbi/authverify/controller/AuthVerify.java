package tw.com.sbi.authverify.controller;

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

import tw.com.sbi.vo.AgentAuthVO;

public class AuthVerify extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(AuthVerify.class);
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		AuthVerifyService authVerifyService = null;

		String groupId = request.getSession().getAttribute("group_id").toString();
		String action = request.getParameter("action");
		
		if ("selectByAuthCode".equals(action)) {
			try {								
				authVerifyService = new AuthVerifyService();
				String authCode = request.getParameter("auth_code");
				
				List<AgentAuthVO> list = authVerifyService.selectByGroupIdAndAuthCode(groupId, authCode);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}
	}

	/*************************** ���平���摩 ****************************************/
	public class AuthVerifyService {
		private authVerify_interface dao;

		public AuthVerifyService() {
			dao = new AuthVerifyDAO();
		}

		public List<AgentAuthVO> selectByGroupIdAndAuthCode(String groupId, String authCode) {
			return dao.getAgentAuthByGroupIdAndAuthCode(groupId, authCode);
		}
	}
	
	/*************************** �摰��瘜� ****************************************/
	interface authVerify_interface {
		public List<AgentAuthVO> getAgentAuthByGroupIdAndAuthCode(String groupId, String authCode);
	}
	
	/*************************** �����澈 ****************************************/
	class AuthVerifyDAO implements authVerify_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");

		// ��蝙����tored procedure
		private static final String sp_get_agent_auth_by_group_auth_code = "call sp_get_agent_auth_by_group_auth_code(?, ?)";

		@Override
		public List<AgentAuthVO> getAgentAuthByGroupIdAndAuthCode(String groupId, String authCode) {
			List<AgentAuthVO> list = new ArrayList<AgentAuthVO>();
			AgentAuthVO authVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_agent_auth_by_group_auth_code);
				pstmt.setString(1, groupId);
				pstmt.setString(2, authCode);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					authVO = new AgentAuthVO();
					
					authVO.setProduct_id(rs.getString("product_id"));
					authVO.setAgent_id(rs.getString("agent_id"));
					authVO.setGroup_id(rs.getString("group_id"));
					authVO.setRegion_code(rs.getString("region_code"));
					authVO.setAuth_quantity(rs.getString("auth_quantity"));
					authVO.setSale_quantity(rs.getString("sale_quantity"));
					authVO.setRegister_quantity(rs.getString("register_quantity"));
					authVO.setSeed(rs.getString("seed"));
					authVO.setAuth_code(rs.getString("auth_code"));
					authVO.setAgent_name(rs.getString("agent_name"));
					authVO.setProduct_spec(rs.getString("product_spec"));
					
					list.add(authVO); // Store the row in the list
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