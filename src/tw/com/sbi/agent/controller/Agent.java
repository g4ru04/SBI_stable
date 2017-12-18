package tw.com.sbi.agent.controller;

import java.io.IOException;
import java.sql.CallableStatement;
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

import tw.com.sbi.vo.AgentVO;

public class Agent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Agent.class);
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		AgentService agentService = null;

		String groupId = request.getSession().getAttribute("group_id").toString();
		String action = request.getParameter("action");
		logger.debug("Action:" + action);
		
		if ("selectAll".equals(action)) {
			try {								
				agentService = new AgentService();
				List<AgentVO> list = agentService.selectByGroupId(groupId);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("search".equals(action)) {
			try {			
				String agentName = request.getParameter("agent_name");
				
				logger.debug("agentName:" + agentName);
				
				agentService = new AgentService();
				List<AgentVO> list = agentService.getAgentByAgentName(groupId, agentName);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("insert".equals(action)) {
			try {
				String agentName = request.getParameter("agent_name");
				String website = request.getParameter("web_site");
				String regionCode = request.getParameter("region_code");
				String email = request.getParameter("contact_mail");
				String phone = request.getParameter("contact_phone");
				String seed = request.getParameter("seed");
				
				logger.debug("agentName:" + agentName);
				logger.debug("website:" + website);
				logger.debug("regionCode:" + regionCode);
				logger.debug("email:" + email);
				logger.debug("phone:" + phone);
				logger.debug("seed:" + seed);
								
				agentService = new AgentService();
				List<AgentVO> list = agentService.addAgent(groupId, agentName, website, regionCode, email, phone, seed);
			
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("update".equals(action)) {
			try {				
				String agentId = request.getParameter("agent_id");
				String agentName = request.getParameter("agent_name");
				String website = request.getParameter("web_site");
				String regionCode = request.getParameter("region_code");
				String email = request.getParameter("contact_mail");
				String phone = request.getParameter("contact_phone");
				String seed = request.getParameter("seed");
				
				logger.debug("agentId:" + agentId);
				logger.debug("agentName:" + agentName);
				logger.debug("website:" + website);
				logger.debug("regionCode:" + regionCode);
				logger.debug("email:" + email);
				logger.debug("phone:" + phone);
				logger.debug("seed:" + seed);
				
				agentService = new AgentService();
				
				List<AgentVO> list = agentService.updateAgent(groupId, agentId, agentName, website, regionCode, email, phone, seed);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("delete".equals(action)) {
			try {
				String agentId = request.getParameter("agent_id");
				
				logger.debug("agentId:" + agentId);
				
				agentService = new AgentService();
				
				List<AgentVO> list = agentService.deleteAgent(groupId, agentId);

				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("autocomplete_name".equals(action)) {
			try {
				String term = request.getParameter("term");
				
				logger.debug("term:" + term);
				
				agentService = new AgentService();
				List<AgentVO> list = agentService.getAgentByAgentName(groupId, term);
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
	public class AgentService {
		private agent_interface dao;

		public AgentService() {
			dao = new AgentDAO();
		}

		public List<AgentVO> selectByGroupId(String groupId) {
			return dao.selectByGroupId(groupId);
		}
		
		public List<AgentVO> addAgent(String groupId, String agentName, String website, String regionCode, String email, String phone, String seed) {
			AgentVO agentVO = new AgentVO();
			
			agentVO.setGroup_id(groupId);
			agentVO.setAgent_name(agentName);
			agentVO.setWeb_site(website);
			agentVO.setRegion_code(regionCode);
			agentVO.setContact_mail(email);
			agentVO.setContact_phone(phone);
			agentVO.setSeed(seed);

			dao.insertDB(agentVO);
			return dao.selectByGroupId(groupId);
		}
		
		public List<AgentVO> updateAgent(String groupId, String agentId, String agentName, String website, String regionCode, String email, String phone, String seed){
			AgentVO agentVO = new AgentVO();

			agentVO.setGroup_id(groupId);
			agentVO.setAgent_id(agentId);
			agentVO.setAgent_name(agentName);
			agentVO.setWeb_site(website);
			agentVO.setRegion_code(regionCode);
			agentVO.setContact_mail(email);
			agentVO.setContact_phone(phone);
			agentVO.setSeed(seed);
			
			dao.updateDB(agentVO);
			return dao.selectByGroupId(groupId);
		}
		
		public List<AgentVO> deleteAgent(String groupId, String agentId){
			dao.deleteDB(groupId, agentId);
			return dao.selectByGroupId(groupId);
		}
		
		public List<AgentVO> getAgentByAgentName(String groupId, String agentName){
			return dao.getAgentByAgentName(groupId, agentName);
		}
	}
	
	/*************************** 制定規章方法 ****************************************/
	interface agent_interface {
		public void insertDB(AgentVO agentVO);

		public void updateDB(AgentVO agentVO);

		public void deleteDB(String groupId, String agentId);
		
		public List<AgentVO> selectByGroupId(String groupId);
		
		public List<AgentVO> getAgentByAgentName(String groupId, String agentName);
	}
	
	/*************************** 操作資料庫 ****************************************/
	class AgentDAO implements agent_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");

		// 會使用到的Stored procedure
		private static final String sp_get_agent_by_group = "call sp_get_agent_by_group(?)";
		private static final String sp_insert_agent = "call sp_insert_agent(?,?,?,?,?,?,?)";
		private static final String sp_update_agent = "call sp_update_agent(?,?,?,?,?,?,?,?)";
		private static final String sp_delete_agent = "call sp_delete_agent(?,?)";
		private static final String sp_get_agent_by_group_and_agent_name = "call sp_get_agent_by_group_and_agent_name(?,?)";

		@Override
		public List<AgentVO> selectByGroupId(String groupId) {
			List<AgentVO> list = new ArrayList<AgentVO>();
			AgentVO agentVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_agent_by_group);
				pstmt.setString(1, groupId);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					agentVO = new AgentVO();
					
					agentVO.setGroup_id(groupId);
					agentVO.setAgent_id(rs.getString("agent_id"));
					agentVO.setAgent_name(rs.getString("agent_name"));
					agentVO.setWeb_site(rs.getString("web_site"));
					agentVO.setRegion_code(rs.getString("region_code"));
					agentVO.setContact_mail(rs.getString("contact_mail"));
					agentVO.setContact_phone(rs.getString("contact_phone"));
					agentVO.setSeed(rs.getString("seed"));
					
					list.add(agentVO); // Store the row in the list
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
		public void insertDB(AgentVO agentVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_insert_agent);

				cs.setString(1, agentVO.getGroup_id());
				cs.setString(2, agentVO.getWeb_site());
				cs.setString(3, agentVO.getRegion_code());
				cs.setString(4, agentVO.getAgent_name());
				cs.setString(5, agentVO.getContact_mail());
				cs.setString(6, agentVO.getContact_phone());
				cs.setString(7, agentVO.getSeed());

				cs.execute();
			
			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
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
		public void updateDB(AgentVO agentVO) {
			logger.debug("enter updateDB method");
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_update_agent);

				cs.setString(1, agentVO.getAgent_id());
				cs.setString(2, agentVO.getGroup_id());
				cs.setString(3, agentVO.getWeb_site());
				cs.setString(4, agentVO.getRegion_code());
				cs.setString(5, agentVO.getAgent_name());
				cs.setString(6, agentVO.getContact_mail());
				cs.setString(7, agentVO.getContact_phone());
				cs.setString(8, agentVO.getSeed());

				cs.execute();
			
			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
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
		public void deleteDB(String groupId, String agentId) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_delete_agent);

				cs.setString(1, groupId);
				cs.setString(2, agentId);

				cs.execute();
			
			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
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
		public List<AgentVO> getAgentByAgentName(String groupId, String agentName) {
			List<AgentVO> list = new ArrayList<AgentVO>();
			AgentVO agentVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_agent_by_group_and_agent_name);
				pstmt.setString(1, groupId);
				pstmt.setString(2, agentName);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					agentVO = new AgentVO();
					
					agentVO.setGroup_id(groupId);
					agentVO.setAgent_id(rs.getString("agent_id"));
					agentVO.setAgent_name(rs.getString("agent_name"));
					agentVO.setWeb_site(rs.getString("web_site"));
					agentVO.setRegion_code(rs.getString("region_code"));
					agentVO.setContact_mail(rs.getString("contact_mail"));
					agentVO.setContact_phone(rs.getString("contact_phone"));
					agentVO.setSeed(rs.getString("seed"));
					
					list.add(agentVO); // Store the row in the list
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