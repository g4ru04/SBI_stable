package tw.com.sbi.productverify.controller;

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

import tw.com.sbi.vo.ProductVO;

public class ProductVerify extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(ProductVerify.class);
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ProductVerifyService productVerifyService = null;

		String groupId = request.getSession().getAttribute("group_id").toString();
		String action = request.getParameter("action");
		
		if ("selectByIdentityId".equals(action)) {
			try {								
				productVerifyService = new ProductVerifyService();
				String identityId = request.getParameter("identity_id");
				
				List<ProductVO> list = productVerifyService.selectByGroupIdAndIdentityId(groupId, identityId);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}
	}

	public class ProductVerifyService {
		private productVerify_interface dao;

		public ProductVerifyService() {
			dao = new ProductVerifyDAO();
		}

		public List<ProductVO> selectByGroupIdAndIdentityId(String groupId, String identityId) {
			return dao.selectByGroupIdAndIdentityId(groupId, identityId);
		}
	}
	
	interface productVerify_interface {
		public List<ProductVO> selectByGroupIdAndIdentityId(String groupId, String identityId);
	}
	
	class ProductVerifyDAO implements productVerify_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");

		private static final String sp_get_product_by_group_identity = "call sp_get_product_by_group_identity(?, ?)";

		@Override
		public List<ProductVO> selectByGroupIdAndIdentityId(String groupId, String identityId) {
			List<ProductVO> list = new ArrayList<ProductVO>();
			ProductVO productVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_product_by_group_identity);
				pstmt.setString(1, groupId);
				pstmt.setString(2, identityId);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					productVO = new ProductVO();
					
					productVO.setGroup_id(groupId);
					productVO.setPhoto(rs.getString("photo") == null?"":rs.getString("photo"));
					productVO.setProduct_id(rs.getString("product_id") == null?"":rs.getString("product_id"));
					productVO.setProduct_spec(rs.getString("product_spec") == null?"":rs.getString("product_spec"));
					productVO.setSeed(rs.getString("seed") == null?"":rs.getString("seed"));
					
					list.add(productVO); // Store the row in the list
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