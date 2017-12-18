package tw.com.sbi.caseandevaluate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import tw.com.sbi.caseandevaluate.controller.Case.CaseService;
import tw.com.sbi.productforecastpoint.controller.ProductForecastPoint.ProductForecastPointBean;
import tw.com.sbi.productforecastpoint.controller.ProductForecastPoint.ProductForecastPointService;
import tw.com.sbi.vo.CaseVO;
import tw.com.sbi.vo.EvaluateVO;

public class Evaluate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = LogManager.getLogger(Evaluate.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		EvaluateService evaluateService = null;

		String action = request.getParameter("action");
		
		logger.debug("Action: " + action);
		
		if ("getEvaluate".equals(action)) {
			try {
				String caseId = request.getParameter("case_id");
				
				evaluateService = new EvaluateService();
				List<EvaluateVO> list = evaluateService.selectEvaluateById(caseId);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				logger.debug(jsonStrList);
				response.getWriter().write(jsonStrList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("getEvaluateDetail".equals(action)) {
			try {
				String caseId = request.getParameter("case_id");
				String userId = request.getParameter("user_id");
				
				evaluateService = new EvaluateService();
				List<EvaluateVO> list = evaluateService.selectEvaluateDetailById(caseId, userId);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				logger.debug(jsonStrList);
				response.getWriter().write(jsonStrList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("insert".equals(action)) {
			try {
				/*************************** 1.接收請求參數 **************************************/
				String evaluateId = request.getParameter("evaluate_id");
				String caseId = request.getParameter("case_id");
				String userId = request.getParameter("user_id");
				String evaluateReason = request.getParameter("evaluate_reason");
				String weight = request.getParameter("weight");
				String userAuthority = request.getParameter("user_authority");
				String evaluatePoint = request.getParameter("evaluate_point");
				String evaluate1Point = request.getParameter("evaluate_1_point");
				String evaluateSeq = request.getParameter("evaluate_seq");
				
				logger.debug("action: Insert");
				logger.debug("evaluate_id:" + evaluateId);
				logger.debug("case_id:" + caseId);
				logger.debug("user_id:" + userId);
				logger.debug("evaluate_reason:" + evaluateReason);
				logger.debug("weight:" + weight);
				logger.debug("user_authority:" + userAuthority);
				logger.debug("evaluate_point:" + evaluatePoint);
				logger.debug("evaluate_1_point:" + evaluate1Point);
				logger.debug("evaluate_seq:" + evaluateSeq);
				
				/*************************** 2.開始新增資料 ***************************************/
				evaluateService = new EvaluateService();
				evaluateService.addEvaluate(evaluateId, caseId, userId, evaluateReason, weight, 
						userAuthority, evaluatePoint, evaluate1Point, evaluateSeq);

				/*************************** 3.新增完成,準備轉交(Send the Success view) ***********/
				EvaluateVO evaluateVO = new EvaluateVO();
				evaluateVO.setMessage("新增成功");
				
				List<EvaluateVO> list = new ArrayList<EvaluateVO>();
				list.add(evaluateVO);
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		} else if ("update".equals(action)) {
			try {
				String userId = request.getSession().getAttribute("user_id").toString();
				
				/*************************** 1.接收請求參數 **************************************/
				String caseId = request.getParameter("case_id");
				String evaluatePoint = request.getParameter("evaluate_point");
				String evaluateReason = request.getParameter("evaluate_reason");
				String evaluate1Point = request.getParameter("evaluate_1_point");
				String evaluateSeq = request.getParameter("evaluate_seq");
				
				logger.debug("action: Update");
				logger.debug("user_id:" + userId);
				logger.debug("case_id:" + caseId);
				logger.debug("evaluate_point:" + evaluatePoint);
				logger.debug("evaluate_reason:" + evaluateReason);
				logger.debug("evaluate_1_point:" + evaluate1Point);
				logger.debug("evaluate_seq:" + evaluateSeq);
				
				/*************************** 2.開始新增資料 ***************************************/
				evaluateService = new EvaluateService();
				evaluateService.updateEvaluate(caseId, userId, evaluatePoint, evaluateReason, evaluate1Point, evaluateSeq);

				/*************************** 3.新增完成,準備轉交(Send the Success view) ***********/
				EvaluateVO evaluateVO = new EvaluateVO();
				evaluateVO.setMessage("新增成功");
				
				List<EvaluateVO> list = new ArrayList<EvaluateVO>();
				list.add(evaluateVO);
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				response.getWriter().write(jsonStrList);
				
				//檢查是否全部填寫完畢
				evaluateService.countCase(caseId);
				
				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}	
	
	}
	
	/*************************** 處理業務邏輯 ****************************************/
	public class EvaluateService {
		private evaluate_interface dao;

		public EvaluateService() {
			dao = new EvaluateDAO();
		}
		
		public List<EvaluateVO> selectEvaluateById(String caseId) {
			return dao.selectEvaluateByCaseId(caseId);
		}
		
		public List<EvaluateVO> selectEvaluateDetailById(String caseId, String userId) {
			return dao.selectEvaluateDetailByCaseId(caseId, userId);
		}
		
		public EvaluateVO addEvaluate(String evaluate_id, String case_id, String user_id, String evaluate_reason, String weight, 
				String user_authority, String evaluate_point, String evaluate_1_point, String evaluate_seq) {
			EvaluateVO evaluateVO = new EvaluateVO();
			
			evaluateVO.setEvaluate_id(evaluate_id);
			evaluateVO.setCase_id(case_id);
			evaluateVO.setUser_id(user_id);
			evaluateVO.setEvaluate_reason(evaluate_reason);
			evaluateVO.setWeight(weight);
			
			evaluateVO.setUser_authority(user_authority);
			evaluateVO.setEvaluate_point(evaluate_point);
			evaluateVO.setEvaluate_1_point(evaluate_1_point);
			evaluateVO.setEvaluate_seq(evaluate_seq);
			
			dao.insertEvaluate(evaluateVO);
		
			return evaluateVO;
		}

		public EvaluateVO updateEvaluate(String case_id, String user_id, String evaluate_point, String evaluate_reason, String evaluate_1_point, 
				String evaluate_seq) {
			EvaluateVO evaluateVO = new EvaluateVO();
			
			evaluateVO.setCase_id(case_id);
			evaluateVO.setUser_id(user_id);
			evaluateVO.setEvaluate_reason(evaluate_reason);
			evaluateVO.setEvaluate_point(evaluate_point);
			evaluateVO.setEvaluate_1_point(evaluate_1_point);
			
			evaluateVO.setEvaluate_seq(evaluate_seq);
			
			dao.updateEvaluate(evaluateVO);
		
			return evaluateVO;
		}
		
		public void countCase(String caseId) {
			dao.countByCaseIdDB(caseId);
		}
	}

	/*************************** 制定規章方法 ****************************************/
	interface evaluate_interface {
		public List<EvaluateVO> selectEvaluateByCaseId(String caseId);
		public List<EvaluateVO> selectEvaluateDetailByCaseId(String caseId, String userId);
		public void insertEvaluate(EvaluateVO evaluateVO);
		public void updateEvaluate(EvaluateVO evaluateVO);
		public void countByCaseIdDB(String case_id);
	}
	
	/*************************** 操作資料庫 ****************************************/
	class EvaluateDAO implements evaluate_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		// 會使用到的Stored procedure
		private static final String sp_get_decision_evaluate_by_case_id = "call sp_get_decision_evaluate_by_case_id(?)";
		private static final String sp_get_decision_evaluate_detail_by_case_id = "call sp_get_decision_evaluate_detail_by_case_id(?, ?)";
		private static final String sp_insert_evaluate = "call sp_insert_evaluate(?,?,?,?,?,?,?,?,?)";
		private static final String sp_update_evaluate = "call sp_update_evaluate(?,?,?,?,?,?)";
		private static final String sp_count_evaluate = "call sp_count_evaluate(?)";
		
		@Override
		public List<EvaluateVO> selectEvaluateByCaseId(String caseId) {
			List<EvaluateVO> list = new ArrayList<EvaluateVO>();
			EvaluateVO evaluateVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_evaluate_by_case_id);
				pstmt.setString(1, caseId);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					evaluateVO = new EvaluateVO();
					
					evaluateVO.setEvaluate_id(rs.getString("evaluate_id") == null?"":rs.getString("evaluate_id"));
					evaluateVO.setCase_id(rs.getString("case_id") == null?"":rs.getString("case_id"));
					evaluateVO.setUser_id(rs.getString("user_id") == null?"":rs.getString("user_id"));
					evaluateVO.setWeight(rs.getString("weight") == null?"":rs.getString("weight"));
					evaluateVO.setUser_authority(rs.getString("user_authority") == null?"":rs.getString("user_authority"));
					
					evaluateVO.setEvaluate_point(rs.getString("evaluate_point") == null?"":rs.getString("evaluate_point"));
					evaluateVO.setEvaluate_reason(rs.getString("evaluate_reason") == null?"":rs.getString("evaluate_reason"));
					evaluateVO.setEvaluate_1_point(rs.getString("evaluate_1_point") == null?"":rs.getString("evaluate_1_point"));
					evaluateVO.setEvaluate_seq(rs.getString("evaluate_seq") == null?"":rs.getString("evaluate_seq"));
					evaluateVO.setV_user_name(rs.getString("user_name") == null?"":rs.getString("user_name"));
					
					list.add(evaluateVO); // Store the row in the list
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
		public List<EvaluateVO> selectEvaluateDetailByCaseId(String caseId, String userId) {
			List<EvaluateVO> list = new ArrayList<EvaluateVO>();
			EvaluateVO evaluateVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_evaluate_detail_by_case_id);
				pstmt.setString(1, caseId);
				pstmt.setString(2, userId);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					evaluateVO = new EvaluateVO();
					
					evaluateVO.setEvaluate_id(rs.getString("evaluate_id") == null?"":rs.getString("evaluate_id"));
					evaluateVO.setCase_id(rs.getString("case_id") == null?"":rs.getString("case_id"));
					evaluateVO.setUser_id(rs.getString("user_id") == null?"":rs.getString("user_id"));
					evaluateVO.setEvaluate_1_point(rs.getString("evaluate_1_point") == null?"":rs.getString("evaluate_1_point"));

					evaluateVO.setUser_id(rs.getString("user_id") == null?"":rs.getString("user_id"));
					evaluateVO.setV_user_name(rs.getString("user_name") == null?"":rs.getString("user_name"));
					evaluateVO.setV_evaluate_no(rs.getString("evaluate_no") == null?"":rs.getString("evaluate_no"));
					evaluateVO.setV_evaluate_1_no(rs.getString("evaluate_1_no") == null?"":rs.getString("evaluate_1_no"));
					evaluateVO.setV_evaluate(rs.getString("evaluate") == null?"":rs.getString("evaluate"));
					evaluateVO.setV_evaluate1(rs.getString("evaluate_1") == null?"":rs.getString("evaluate_1"));
					
					list.add(evaluateVO); // Store the row in the list
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
		public void insertEvaluate(EvaluateVO evaluateVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_insert_evaluate);

				cs.setString(1, evaluateVO.getEvaluate_id());
				cs.setString(2, evaluateVO.getCase_id());
				cs.setString(3, evaluateVO.getUser_id());
				cs.setString(4, evaluateVO.getEvaluate_reason());
				cs.setString(5, evaluateVO.getWeight());
				cs.setString(6, evaluateVO.getUser_authority());
				cs.setString(7, evaluateVO.getEvaluate_point());
				cs.setString(8, evaluateVO.getEvaluate_1_point());
				cs.setString(9, evaluateVO.getEvaluate_seq());
				
				cs.execute();
				
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
		public void updateEvaluate(EvaluateVO evaluateVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_update_evaluate);

				cs.setString(1, evaluateVO.getCase_id());
				cs.setString(2, evaluateVO.getUser_id());
				cs.setString(3, evaluateVO.getEvaluate_point());
				cs.setString(4, evaluateVO.getEvaluate_reason());
				cs.setString(5, evaluateVO.getEvaluate_1_point());
				cs.setString(6, evaluateVO.getEvaluate_seq());
				
				cs.execute();
				
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
		public void countByCaseIdDB(String caseId) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				CallableStatement cs = null;
				cs = con.prepareCall(sp_count_evaluate);

				cs.setString(1, caseId);
				
				ResultSet rs = null;
				
				Integer cnt = 0;
				rs = cs.executeQuery();
				while (rs.next()) {
					cnt = rs.getInt("cnt");
					
					logger.debug("countByCaseIdDB: getCnt" + cnt);
				}
				
				if (cnt == 0) {
					
					String serviceStr = getServletConfig().getServletContext().getInitParameter("pythonwebservice")
							+ "/groupdecision/type="
							+ new String(Base64.encodeBase64String( "1".getBytes()) )
							+ "&case="
							+ new String(Base64.encodeBase64String( caseId.getBytes("UTF-8")) );
					
					logger.debug(serviceStr);
					
					HttpClient client = new HttpClient();
					HttpMethod method = null;
					
					try {
						method = new GetMethod(serviceStr);
						client.executeMethod(method);
					} catch(Exception e) {
						logger.error("WebService Error for:"+e);
						logger.debug("full-range-log",e);
					} finally {
						method.releaseConnection();
					}
					
					
				}
				
			} catch (SQLException se) {
				// Handle any SQL errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException("A database error occured. " + e1.getMessage());
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
	}
}
