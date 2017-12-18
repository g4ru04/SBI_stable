package tw.com.sbi.product.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.google.gson.Gson;

import tw.com.sbi.productverify.controller.ProductVerify.ProductVerifyService;
import tw.com.sbi.vo.ProductServiceVO;
import tw.com.sbi.vo.ProductVO;

public class Service extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(Service.class);
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ServiceService serviceService = null;

		String groupId = request.getSession().getAttribute("group_id").toString();
		String action = request.getParameter("action");

		logger.debug("Action: " + action);
		
		if ("selectAll".equals(action)) {
			
		} else if ("selectByProductId".equals(action)) {
			try {								
				serviceService = new ServiceService();
				String product_id = request.getParameter("product_id");
				
				List<ProductServiceVO> list = serviceService.selectByProductId(product_id);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("selectByProductSpec".equals(action)) {
			try {								
				serviceService = new ServiceService();
				String product_spec = request.getParameter("product_spec");
				
				List<ProductServiceVO> list = serviceService.selectByProductSpec(product_spec);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("genService".equals(action)) {
			try {
				String productId = request.getParameter("product_id");
				String quantity = request.getParameter("quantity");
				
				logger.debug("Product ID: " + productId);
				logger.debug("Quantity: " + quantity);
				
				serviceService = new ServiceService();
				
				List<ProductServiceVO> list = serviceService.genServiceID(groupId, productId, quantity);

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
	public class ServiceService {
		private service_interface dao;

		public ServiceService() {
			dao = new ServiceDAO();
		}

		public List<ProductServiceVO> selectByProductId(String product_id) {
			return dao.selectByProductId(product_id);
		}

		public List<ProductServiceVO> selectByProductSpec(String product_spec) {
			return dao.selectByProductSpec(product_spec);
		}
		
		public List<ProductServiceVO> addProductService(String service_id, String product_id, String agent_id, String group_id, String register) {
			ProductServiceVO productServiceVO = new ProductServiceVO();
			
			productServiceVO.setService_id(service_id);
			productServiceVO.setProduct_id(product_id);
			productServiceVO.setAgent_id(agent_id);
			productServiceVO.setGroup_id(group_id);
			productServiceVO.setRegister(register);

			dao.insertDB(productServiceVO);
			return dao.selectByProductId(product_id);
		}
		
		public List<ProductServiceVO> genServiceID(String groupId, String productId, String quantity){
			dao.genServiceID(groupId, productId, quantity);
			return dao.selectByProductId(groupId);
		}
	}
		
	/*************************** 制定規章方法 ****************************************/
	interface service_interface {
		public void insertDB(ProductServiceVO productServiceVO);

		public List<ProductServiceVO> selectByProductId(String productId);
		
		public List<ProductServiceVO> selectByProductSpec(String productId);
		
		public void genServiceID(String groupId, String productId, String quantity);
	}
	
	/*************************** 操作資料庫 ****************************************/
	class ServiceDAO implements service_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		// 會使用到的Stored procedure
		private static final String sp_get_product_service_by_product_id = "call sp_get_product_service_by_product_id(?)";
		private static final String sp_get_product_service_by_product_spec = "call sp_get_product_service_by_product_spec(?)";
		private static final String sp_insert_product_service = "call sp_insert_product_service(?,?,?,?,?)";
		
		@Override
		public List<ProductServiceVO> selectByProductId(String productId) {
			List<ProductServiceVO> list = new ArrayList<ProductServiceVO>();
			ProductServiceVO productServiceVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_product_service_by_product_id);
				pstmt.setString(1, productId);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					productServiceVO = new ProductServiceVO();
					
					productServiceVO.setService_id(rs.getString("service_id") == null?"":rs.getString("service_id"));
					productServiceVO.setProduct_id(rs.getString("product_id") == null?"":rs.getString("product_id"));
					productServiceVO.setAgent_id(rs.getString("agent_id") == null?"":rs.getString("agent_id"));
					productServiceVO.setGroup_id(rs.getString("group_id") == null?"":rs.getString("group_id"));
					productServiceVO.setRegister(rs.getString("register") == null?"":rs.getString("register"));
					productServiceVO.setAgent_name(rs.getString("agent_name") == null?"":rs.getString("agent_name"));
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
		public List<ProductServiceVO> selectByProductSpec(String productSpec) {
			List<ProductServiceVO> list = new ArrayList<ProductServiceVO>();
			ProductServiceVO productServiceVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_product_service_by_product_spec);
				pstmt.setString(1, productSpec);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					productServiceVO = new ProductServiceVO();
					
					productServiceVO.setService_id(rs.getString("service_id") == null?"":rs.getString("service_id"));
					productServiceVO.setProduct_id(rs.getString("product_id") == null?"":rs.getString("product_id"));
					productServiceVO.setAgent_id(rs.getString("agent_id") == null?"":rs.getString("agent_id"));
					productServiceVO.setGroup_id(rs.getString("group_id") == null?"":rs.getString("group_id"));
					productServiceVO.setRegister(rs.getString("register") == null?"":rs.getString("register"));
					productServiceVO.setAgent_name(rs.getString("agent_name") == null?"":rs.getString("agent_name"));
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
		public void insertDB(ProductServiceVO productServcieVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_insert_product_service);

				cs.setString(1, productServcieVO.getService_id());
				cs.setString(2, productServcieVO.getProduct_id());
				cs.setString(3, productServcieVO.getAgent_id());
				cs.setString(4, productServcieVO.getGroup_id());
				cs.setString(5, productServcieVO.getRegister());

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
		public void genServiceID(String groupId, String productId, String quantity) {
			
			String encodeQuantity = new String(Base64.encodeBase64String(quantity.getBytes()));
    		String url = wsPath + "/license/type=U2VydmljZQ==&quty=" + encodeQuantity;

    		HttpGet httpRequest = new HttpGet(url);
        	HttpClient client = HttpClientBuilder.create().build();
        	HttpResponse httpResponse;
        	try {
        		StringBuffer result = new StringBuffer();
        		httpResponse = client.execute(httpRequest);
    			int responseCode = httpResponse.getStatusLine().getStatusCode();
    
    	    	if(responseCode==200){
    	    		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

        	    	String line = "";
        	    	while ((line = rd.readLine()) != null) {
        	    		result.append(line);
        	    	}
        	    	
    	    		logger.debug("webservice result: " + result.toString()); 
    	    		JSONArray jsonArray = new JSONArray(result.toString());
    	    		
//    	    		logger.debug(jsonArray.getJSONObject(0).get("ServiceID"));
    	    		
    	    		int total = jsonArray.length();
    	    		
    	    		Connection con = null;
    				PreparedStatement pstmt = null;
    				String serviceId = (String) jsonArray.getJSONObject(0).get("ServiceID");
    				
    				logger.debug("Service ID: " + serviceId);
    				try {
    					Class.forName("com.mysql.jdbc.Driver");
    					con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
    					CallableStatement cs = null;
    					
    					for (int i = 0; i < total; i++){
        	    			logger.debug(jsonArray.getJSONObject(i).get("ServiceID"));
        	    			ProductServiceVO productServiceVO = new ProductServiceVO();
        	    			
        	    			String service_id = (String) jsonArray.getJSONObject(i).get("ServiceID");
        	    			
        					productServiceVO.setService_id(service_id);
        					productServiceVO.setProduct_id(productId);
        					productServiceVO.setAgent_id(null);
        					productServiceVO.setGroup_id(groupId);
        					productServiceVO.setRegister(null);
        					
        					insertDB(productServiceVO);
    					}
    				
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
    	    	else {
    	    		logger.debug("webservice fail"); 
    	    	}
  	    	
    		} catch (ClientProtocolException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			logger.debug("full-range-log",e);
    		} catch (UnsupportedOperationException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			logger.debug("full-range-log",e);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			logger.debug("full-range-log",e);
    		}
		}
	}
}
