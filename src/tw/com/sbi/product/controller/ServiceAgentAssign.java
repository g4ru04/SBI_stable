package tw.com.sbi.product.controller;

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

import tw.com.sbi.agent.controller.Agent.AgentService;
import tw.com.sbi.vo.AgentVO;
import tw.com.sbi.vo.ProductServiceListVO;
import tw.com.sbi.vo.ProductServiceVO;

/**
 * Servlet implementation class ServiceAgentAssign
 */
public class ServiceAgentAssign extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ServiceAgentAssign.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ServiceAgentAssignService serviceAgentAssignService = null;

		String groupId = request.getSession().getAttribute("group_id").toString();
		String action = request.getParameter("action");

		logger.debug("Action: " + action);
		logger.debug("Group ID: " + groupId);
		
		if ("selectAll".equals(action)) {
		} else if ("selectServiceAgentNull".equals(action)) {
			try {								
				serviceAgentAssignService = new ServiceAgentAssignService();
				String productSpec = request.getParameter("product_spec");
				String agentName = request.getParameter("agent_name");
				
				logger.debug("Product Spec: " + productSpec);
				logger.debug("Agent Name: " + agentName);
				
				List<ProductServiceVO> list = serviceAgentAssignService.selectServiceAgentNull(groupId, productSpec, agentName);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
			
		} else if ("selectServiceAgentName".equals(action)) {
			try {								
				serviceAgentAssignService = new ServiceAgentAssignService();
				String productSpec = request.getParameter("product_spec");
				String agentName = request.getParameter("agent_name");
				
				logger.debug("Product Spec: " + productSpec);
				logger.debug("Agent Name: " + agentName);
				
				List<ProductServiceVO> list = serviceAgentAssignService.selectServiceAgentName(groupId, productSpec, agentName);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
			
		} else if ("selectAgentAuth".equals(action)) {
			try {								
				serviceAgentAssignService = new ServiceAgentAssignService();
				String productSpec = request.getParameter("product_spec");
				
				List<ProductServiceVO> list = serviceAgentAssignService.selectAgentAuth(groupId, productSpec);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("selectServiceInfo".equals(action)) {
			try {								
				serviceAgentAssignService = new ServiceAgentAssignService();
				String serviceId = request.getParameter("service_id");
				
				List<ProductServiceVO> list = serviceAgentAssignService.selectServiceInfo(groupId, serviceId);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("update".equals(action)) {
			logger.debug("product service update agent");
			try {				
				String serviceId = request.getParameter("service_id");
				String productSpec = request.getParameter("product_spec");
				String agentName = request.getParameter("agent_name");
				
				logger.debug("Service ID: " + serviceId);
				logger.debug("Product Spec: " + productSpec);
				logger.debug("Agent Name: " + agentName);
				
				serviceAgentAssignService = new ServiceAgentAssignService();
				List<ProductServiceVO> list = serviceAgentAssignService.updateProductService(serviceId, agentName, groupId, productSpec);
				
				List<ProductServiceVO> listAgent = serviceAgentAssignService.selectServiceAgentName(groupId, productSpec, agentName);
				
				ProductServiceListVO psList = new ProductServiceListVO();
				
				psList.setListNull(list);
				psList.setListAgent(listAgent);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(psList);
				response.getWriter().write(jsonStrList);
				logger.debug("jsonStrList: " + jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("autocomplete_name".equals(action)) {
			try {
				String productSpec = request.getParameter("product_spec");
				String term = request.getParameter("term");
				
				logger.debug("Agent Name: " + term);
				logger.debug("Product Spec: " + productSpec);
				
				serviceAgentAssignService = new ServiceAgentAssignService();
				List<AgentVO> list = serviceAgentAssignService.getAgentByAgentName(groupId, term, productSpec);
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
	public class ServiceAgentAssignService {
		private serviceAgentAssign_interface dao;

		public ServiceAgentAssignService() {
			dao = new ServiceAgentAssignDAO();
		}

		public List<ProductServiceVO> selectAgentAuth(String groupId, String productSpec) {
			return dao.selectAgentAuth(groupId, productSpec);
		}

		public List<ProductServiceVO> selectServiceAgentName(String groupId, String productSpec, String agentName) {
			return dao.selectServiceAgentName(groupId, productSpec, agentName);
		}

		public List<ProductServiceVO> selectServiceAgentNull(String groupId, String productSpec, String agentName) {
			return dao.selectServiceAgentNull(groupId, productSpec, agentName);
		}

		public List<ProductServiceVO> selectServiceInfo(String groupId, String serviceId) {
			return dao.selectServiceInfo(groupId, serviceId);
		}

		public List<ProductServiceVO> updateProductService(String service_id, String agentName, String groupId, String productSpec) {
			ProductServiceVO productServiceVO = new ProductServiceVO();
			
			productServiceVO.setService_id(service_id);
			productServiceVO.setAgent_name(agentName);
			
			dao.updateDB(productServiceVO);
			return dao.selectServiceAgentNull(groupId, productSpec, agentName);
		}
		
		public List<AgentVO> getAgentByAgentName(String groupId, String agentName, String productSpec){
			return dao.getAgentByAgentName(groupId, agentName, productSpec);
		}
	}
		
	/*************************** 制定規章方法 ****************************************/
	interface serviceAgentAssign_interface {
		public void updateDB(ProductServiceVO productServcieVO);

		public List<ProductServiceVO> selectServiceAgentNull(String groupId, String productSpec, String agentName);
		
		public List<ProductServiceVO> selectServiceAgentName(String groupId, String productSpec, String agentName);
		
		public List<ProductServiceVO> selectAgentAuth(String groupId, String productSpec);
		
		public List<ProductServiceVO> selectServiceInfo(String groupId, String serviceId);

		public List<AgentVO> getAgentByAgentName(String groupId, String agentName, String productSpec);
	}
		
	/*************************** 操作資料庫 ****************************************/
	class ServiceAgentAssignDAO implements serviceAgentAssign_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		// 會使用到的Stored procedure
		private static final String sp_get_product_service_by_agent_null = "call sp_get_product_service_by_agent_null(?, ?, ?)";
		private static final String sp_get_product_service_by_agent_name = "call sp_get_product_service_by_agent_name(?, ?, ?)";
		private static final String sp_get_agent_auth_by_product_spec = "call sp_get_agent_auth_by_product_spec(?, ?)";
		private static final String sp_get_service_info_by_service_id = "call sp_get_service_info_by_service_id(?, ?)";
		private static final String sp_update_product_service_agent = "call sp_update_product_service_agent(?, ?)";
		private static final String sp_get_agent_by_group_and_product_spec = "call sp_get_agent_by_group_and_product_spec(?, ?, ?)";
		
		@Override
		public List<ProductServiceVO> selectServiceAgentNull(String groupId, String productSpec, String agentName) {
			List<ProductServiceVO> list = new ArrayList<ProductServiceVO>();
			ProductServiceVO productServiceVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_product_service_by_agent_null);
				pstmt.setString(1, groupId);
				pstmt.setString(2, productSpec);
				pstmt.setString(3, agentName);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					productServiceVO = new ProductServiceVO();
					
					productServiceVO.setService_id(rs.getString("service_id") == null?"":rs.getString("service_id"));
					productServiceVO.setAgent_id(rs.getString("agent_id") == null?"":rs.getString("agent_id"));
					productServiceVO.setAgent_name(rs.getString("agent_name") == null?"":rs.getString("agent_name"));
					
					list.add(productServiceVO); // Store the row in the list
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
		public List<ProductServiceVO> selectServiceAgentName(String groupId, String productSpec, String agentName) {
			List<ProductServiceVO> list = new ArrayList<ProductServiceVO>();
			ProductServiceVO productServiceVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_product_service_by_agent_name);
				pstmt.setString(1, groupId);
				pstmt.setString(2, productSpec);
				pstmt.setString(3, agentName);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					productServiceVO = new ProductServiceVO();
					
					productServiceVO.setService_id(rs.getString("service_id") == null?"":rs.getString("service_id"));
					productServiceVO.setAgent_id(rs.getString("agent_id") == null?"":rs.getString("agent_id"));
					productServiceVO.setAgent_name(rs.getString("agent_name") == null?"":rs.getString("agent_name"));
					
					list.add(productServiceVO); // Store the row in the list
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
		public List<ProductServiceVO> selectAgentAuth(String groupId, String productSpec) {
			List<ProductServiceVO> list = new ArrayList<ProductServiceVO>();
			ProductServiceVO productServiceVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_agent_auth_by_product_spec);
				pstmt.setString(1, groupId);
				pstmt.setString(2, productSpec);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					productServiceVO = new ProductServiceVO();
					
					productServiceVO.setAgent_id(rs.getString("agent_id") == null?"":rs.getString("agent_id"));
					productServiceVO.setAgent_name(rs.getString("agent_name") == null?"":rs.getString("agent_name"));
					
					list.add(productServiceVO); // Store the row in the list
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
		public List<ProductServiceVO> selectServiceInfo(String groupId, String serviceId) {
			List<ProductServiceVO> list = new ArrayList<ProductServiceVO>();
			ProductServiceVO productServiceVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_service_info_by_service_id);
				pstmt.setString(1, groupId);
				pstmt.setString(2, serviceId);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					productServiceVO = new ProductServiceVO();
					
					productServiceVO.setService_id(rs.getString("service_id") == null?"":rs.getString("service_id"));
					productServiceVO.setAgent_id(rs.getString("agent_id") == null?"":rs.getString("agent_id"));
					productServiceVO.setAgent_name(rs.getString("agent_name") == null?"":rs.getString("agent_name"));
					productServiceVO.setProduct_id(rs.getString("product_id") == null?"":rs.getString("product_id"));
					productServiceVO.setProduct_spec(rs.getString("product_spec") == null?"":rs.getString("product_spec"));
					
					list.add(productServiceVO); // Store the row in the list
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
		public void updateDB(ProductServiceVO productServcieVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_update_product_service_agent);

				cs.setString(1, productServcieVO.getService_id());
				cs.setString(2, productServcieVO.getAgent_name());
				
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
		public List<AgentVO> getAgentByAgentName(String groupId, String agentName, String productSpec) {
			List<AgentVO> list = new ArrayList<AgentVO>();
			AgentVO agentVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_agent_by_group_and_product_spec);
				pstmt.setString(1, groupId);
				pstmt.setString(2, agentName);
				pstmt.setString(3, productSpec);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					agentVO = new AgentVO();
					
					agentVO.setAgent_id(rs.getString("agent_id"));
					agentVO.setAgent_name(rs.getString("agent_name"));
					
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
