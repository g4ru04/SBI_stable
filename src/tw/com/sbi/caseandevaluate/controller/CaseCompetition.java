package tw.com.sbi.caseandevaluate.controller;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import tw.com.sbi.vo.CaseCompetitionVO;
import tw.com.sbi.vo.CaseVO;
import tw.com.sbi.vo.EvaluateCompetitionVO;
import tw.com.sbi.vo.UserVO;

public class CaseCompetition extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(CaseCompetition.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String groupId = request.getSession().getAttribute("group_id").toString();
		logger.debug("group_id:" + groupId);

		CaseService caseService = null;

		String action = request.getParameter("action");
		logger.debug("Action: " + action);
		try {
			if ("getDecisionCaseFinish".equals(action)) {
				try {
					caseService = new CaseService();
					List<CaseVO> list = caseService.selectCaseFinish(groupId);
					String jsonStrList = new Gson().toJson(list);
					response.getWriter().write(jsonStrList);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("full-range-log", e);
				}
			} else if ("selectAll".equals(action)) {
				try {
					caseService = new CaseService();
					String group_id = request.getSession().getAttribute("group_id").toString();
					List<UserVO> list = caseService.getSearchAllDB(group_id);
					String jsonStrList = new Gson().toJson(list);
					response.getWriter().write(jsonStrList);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("full-range-log", e);
				}
			} else if ("insert".equals(action)) {
				String[] user_rdo_arr = request.getParameterValues("user_rdo_arr[]");
				String[] user_text_arr = request.getParameterValues("user_text_arr[]");
				String decision_case_finish_json_params = request.getParameter("decision_case_finish_json_params");

				caseService = new CaseService();

				CaseVO caseVO = null;
				try {
					decision_case_finish_json_params = decision_case_finish_json_params.replace("$", "\"");
					logger.debug("decision_case_finish_json_params : " + decision_case_finish_json_params);
					logger.debug("===========================================================================");
					caseVO = new Gson().fromJson(decision_case_finish_json_params, CaseVO.class);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					logger.debug("full-range-log", e);
				}
				if (caseVO != null) {
					String case_id = null;
					String group_id = null;
					String bcircle_id = null;
					String industry = null;
					Integer competition_no = null;
					String competition_name = null;
					Integer evaluate_no = null;
					String evaluate = null;
					String evaluate_1_no = null;
					String evaluate_1 = null;
					String result = null;
					try {
						case_id = caseVO.getCase_id() == null ? null : caseVO.getCase_id();
						group_id = request.getSession().getAttribute("group_id").toString();
						industry = request.getParameter("industry") == null ? "" : request.getParameter("industry");
						competition_no = request.getParameter("competition_no") == null ? null
								: Integer.valueOf(request.getParameter("competition_no"));
						competition_name = request.getParameter("competition_name") == null ? null
								: request.getParameter("competition_name");
						evaluate_no = request.getParameter("evaluate_no") == null ? null
								: Integer.valueOf(request.getParameter("evaluate_no"));
						evaluate = request.getParameter("evaluate") == null ? null : request.getParameter("evaluate");
						evaluate_1_no = request.getParameter("evaluate_1_no") == null ? null
								: request.getParameter("evaluate_1_no");
						evaluate_1_no = evaluate_1_no == null ? null : evaluate_1_no.replace("null", "");
						evaluate_1 = request.getParameter("evaluate_1") == null ? null
								: request.getParameter("evaluate_1");

						result = caseVO.getResult() == null ? null : caseVO.getResult();
						result = result == null ? null : result.split(",")[0].trim();
						bcircle_id = result == null ? null : caseService.getDecisionBcircleIdByName(result);

					} catch (NumberFormatException e) {
						e.printStackTrace();
						logger.debug("full-range-log", e);
					}
					logger.debug("===========================================================================");
					for (String string : user_rdo_arr) {
						logger.debug("user_rdo_arr params : " + string);
					}
					logger.debug("user_rdo_arr length : " + user_rdo_arr.length);
					logger.debug("===========================================================================");
					for (String string : user_text_arr) {
						logger.debug("user_text_arr params : " + string);
					}
					logger.debug("user_text_arr length : " + user_text_arr.length);
					logger.debug("===========================================================================");
					logger.debug("case_id : " + case_id);
					logger.debug("groupId : " + group_id);
					logger.debug("bcircle_id : " + bcircle_id);
					logger.debug("industry : " + industry);
					logger.debug("competition_no : " + competition_no);
					logger.debug("competition_name : " + competition_name);
					logger.debug("evaluate_no : " + evaluate_no);
					logger.debug("evaluate : " + evaluate);
					logger.debug("evaluate_1_no : " + evaluate_1_no);
					logger.debug("evaluate_1 : " + evaluate_1);
					logger.debug("result : " + result);
					logger.debug("===========================================================================");
					String competition_id = caseService.addCaseCompetition(case_id, group_id, bcircle_id, industry,
							competition_no, competition_name, evaluate_no, evaluate, evaluate_1_no, evaluate_1);
					logger.debug("competition_id : " + competition_id);
					logger.debug("===========================================================================");

					int loopCount = 0;

					// if (user_rdo_arr.length == user_text_arr.length) {
					Set<String> userIdSet = new HashSet<String>();

					HashMap<String, String> authorityMap = new HashMap<String, String>();

					if (user_rdo_arr.length > 0) {
						loopCount = user_rdo_arr.length;
						for (int i = 0; i < loopCount; i++) {
							String[] userAuthority = user_rdo_arr[i].split(",");
							authorityMap.put(userAuthority[0].trim(), userAuthority[1].trim());
							userIdSet.add(userAuthority[0].trim());
						}
					}

					HashMap<String, String> weightMap = new HashMap<String, String>();

					if (user_text_arr.length > 0) {
						loopCount = user_text_arr.length;
						for (int i = 0; i < loopCount; i++) {
							String[] userWeight = user_text_arr[i].split(",");
							weightMap.put(userWeight[0].trim(), userWeight[1].trim());
							userIdSet.add(userWeight[0].trim());
						}
					}

					if (!userIdSet.isEmpty()) {
						String evaluate_reason = "";
						String evaluate_point = "";
						String evaluate_1_point = "";
						String evaluate_seq = "";

						Iterator<String> it = null;

						it = userIdSet.iterator();

						while (it.hasNext()) {
							String user_id = it.next();
							String weight = weightMap.get(user_id);
							String user_authority = authorityMap.get(user_id);
							logger.debug("user_id : {} / weight : {} / authority : {}", user_id, weight,
									user_authority);
							caseService.addEvaluateCompetition(competition_id, user_id, evaluate_reason, weight,
									user_authority, evaluate_point, evaluate_1_point, evaluate_seq);
						}
						logger.debug("===========================================================================");
					}
					// }
				}
			} else if ("getCase".equals(action)) {
				try {
					caseService = new CaseService();
					List<CaseCompetitionVO> list = caseService.selectCase(groupId);
					String jsonStrList = new Gson().toJson(list);
					response.getWriter().write(jsonStrList);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("full-range-log", e);
				}
			} else if ("getCaseById".equals(action)) {
				try {
					caseService = new CaseService();
					String competition_id = request.getParameter("competition_id");
					List<CaseCompetitionVO> list = caseService.selectCaseById(competition_id);
					String jsonStrList = new Gson().toJson(list);
					logger.debug("jsonStrList: " + jsonStrList);
					response.getWriter().write(jsonStrList);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("full-range-log", e);
				}
			} else if ("getEvaluate".equals(action)) {
				try {
					caseService = new CaseService();
					String competition_id = request.getParameter("competition_id");
					List<EvaluateCompetitionVO> list = caseService.selectEvaluateByCompetitionId(competition_id);
					String jsonStrList = new Gson().toJson(list);
					logger.debug("jsonStrList: " + jsonStrList);
					response.getWriter().write(jsonStrList);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("full-range-log", e);
				}
			} else if ("getEvaluateDetail".equals(action)) {
				try {
					caseService = new CaseService();
					String competition_id = request.getParameter("competition_id");
					String user_id = request.getParameter("user_id");
					List<EvaluateCompetitionVO> list = caseService.selectEvaluateDetailById(competition_id, user_id);
					String jsonStrList = new Gson().toJson(list);
					logger.debug("jsonStrList: " + jsonStrList);
					response.getWriter().write(jsonStrList);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("full-range-log", e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("full-range-log", e);
		}
	}

	/*************************** 處理業務邏輯 ****************************************/
	public class CaseService {
		private case_interface dao;

		public CaseService() {
			dao = new CaseDAO();
		}

		public List<CaseVO> selectCaseFinish(String groupId) {
			return dao.selectCaseFinish(groupId);
		}

		public List<UserVO> getSearchAllDB(String group_id) {
			return dao.searchAllDB(group_id);
		}

		public String getDecisionBcircleIdByName(String bcircle_name) {
			return dao.getDecisionBcircleIdByName(bcircle_name);
		}

		public List<CaseCompetitionVO> selectCase(String groupId) {
			return dao.selectCase(groupId);
		}

		public List<CaseCompetitionVO> selectCaseById(String competition_id) {
			return dao.selectCaseByCompetitionId(competition_id);
		}

		public List<EvaluateCompetitionVO> selectEvaluateByCompetitionId(String competition_id) {
			return dao.selectEvaluateByCompetitionId(competition_id);
		}

		public String addCaseCompetition(String case_id, String group_id, String bcircle_id, String industry, Integer competition_no,
				String competition_name, Integer evaluate_no, String evaluate, String evaluate_1_no,
				String evaluate_1) {

			CaseCompetitionVO competitionVO = new CaseCompetitionVO();

			competitionVO.setCase_id(case_id);
			competitionVO.setGroup_id(group_id);
			competitionVO.setBcircle_id(bcircle_id);
			competitionVO.setIndustry(industry);
			competitionVO.setCompetition_no(competition_no);
			competitionVO.setCompetition_name(competition_name);
			competitionVO.setEvaluate_no(evaluate_no);
			competitionVO.setEvaluate(evaluate);
			competitionVO.setEvaluate_1_no(evaluate_1_no);
			competitionVO.setEvaluate_1(evaluate_1);

			String competition_id = dao.insertCaseCompetition(competitionVO);

			return competition_id;
		}

		public void addEvaluateCompetition(String competition_id, String user_id, String evaluate_reason, String weight,
				String user_authority, String evaluate_point, String evaluate_1_point, String evaluate_seq) {

			EvaluateCompetitionVO evaluateCompetitionVO = new EvaluateCompetitionVO();

			evaluateCompetitionVO.setCompetition_id(competition_id);
			evaluateCompetitionVO.setUser_id(user_id);
			evaluateCompetitionVO.setEvaluate_reason(evaluate_reason);
			evaluateCompetitionVO.setWeight(weight);
			evaluateCompetitionVO.setUser_authority(user_authority);
			evaluateCompetitionVO.setEvaluate_point(evaluate_point);
			evaluateCompetitionVO.setEvaluate_1_point(evaluate_1_point);
			evaluateCompetitionVO.setEvaluate_seq(evaluate_seq);

			dao.insertEvaluateCompetition(evaluateCompetitionVO);
		}

		public List<EvaluateCompetitionVO> selectEvaluateDetailById(String competition_id, String user_id) {
			return dao.selectEvaluateDetailByCompetitionId(competition_id, user_id);
		}
	}

	/*************************** 制定規章方法 ****************************************/
	interface case_interface {
		public List<CaseVO> selectCaseFinish(String groupId);

		public String insertCaseCompetition(CaseCompetitionVO competitionVO);

		public void insertEvaluateCompetition(EvaluateCompetitionVO evaluateCompetitionVO);

		public List<UserVO> searchAllDB(String group_id);

		public String getDecisionBcircleIdByName(String bcircle_name);

		public List<CaseCompetitionVO> selectCase(String groupId);

		public List<CaseCompetitionVO> selectCaseByCompetitionId(String competition_id);

		public List<EvaluateCompetitionVO> selectEvaluateByCompetitionId(String competition_id);

		public List<EvaluateCompetitionVO> selectEvaluateDetailByCompetitionId(String competition_id, String user_id);

	}

	/*************************** 操作資料庫 ****************************************/
	class CaseDAO implements case_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		// private final String wsPath =
		// getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		// 會使用到的Stored procedure
		private static final String sp_get_decision_case_finish = "call sp_get_decision_case_finish(?)";
		private static final String sp_insert_case_competition = "call sp_insert_case_competition(?,?,?,?,?,?,?,?,?,?,?)";
		private static final String sp_insert_evaluate_competition = "call sp_insert_evaluate_competition(?,?,?,?,?,?,?,?)";
		private static final String sp_selectall_user = "call sp_selectall_user(?)";
		private static final String sp_get_decision_BD_by_name = "call sp_get_decision_BD_by_name(?)";
		private static final String sp_get_decision_case_competition = "call sp_get_decision_case_competition(?)";
		private static final String sp_get_decision_case_competition_by_competition_id = "call sp_get_decision_case_competition_by_competition_id(?)";
		private static final String sp_get_decision_evaluate_competition_by_competition_id = "call sp_get_decision_evaluate_competition_by_competition_id(?)";
		private static final String sp_get_decision_evaluate_competition_detail_by_competition_id = "call sp_get_decision_evaluate_competition_detail_by_competition_id(?,?)";

		@Override
		public List<CaseVO> selectCaseFinish(String groupId) {
			List<CaseVO> list = new ArrayList<CaseVO>();
			CaseVO caseVO = null;

			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_case_finish);
				pstmt.setString(1, groupId);
				
				rs = pstmt.executeQuery();

				while (rs.next()) {
					caseVO = new CaseVO();
					caseVO.setCase_id(rs.getString("case_id") == null ? "" : rs.getString("case_id"));
					caseVO.setGroup_id(rs.getString("group_id") == null ? "" : rs.getString("group_id"));
					caseVO.setCity_id(rs.getString("city_id") == null ? "" : rs.getString("city_id"));
					caseVO.setBcircle_id(rs.getString("bcircle_id") == null ? "" : rs.getString("bcircle_id"));
					caseVO.setPreference(rs.getString("preference") == null ? "" : rs.getString("preference"));
					caseVO.setEvaluate_no(rs.getString("evaluate_no") == null ? "" : rs.getString("evaluate_no"));
					caseVO.setEvaluate(rs.getString("evaluate") == null ? "" : rs.getString("evaluate"));
					caseVO.setEvaluate_1_no(rs.getString("evaluate_1_no") == null ? "" : rs.getString("evaluate_1_no"));
					caseVO.setEvaluate_1(rs.getString("evaluate_1") == null ? "" : rs.getString("evaluate_1"));
					caseVO.setEnding_time(rs.getString("ending_time") == null ? "" : rs.getString("ending_time"));

					//優先排序
					StringJoiner joiner = new StringJoiner(",");
					if (rs.getString("result") != null && rs.getString("result") != "") {
						String[] businessDistrictList = rs.getString("result").split(";");
						for (int i = 0; i < businessDistrictList.length; i++ ) {
							String tmp = businessDistrictList[i];
							businessDistrictList[i] = tmp.split(",")[0];
						}
						
						for (String tmp : businessDistrictList) {
						    joiner.add(tmp.toString());
						}
					}
					String result = joiner == null ? "" : joiner.toString();
					
					caseVO.setResult(result);
					caseVO.setIsfinish(rs.getString("isfinish") == null ? "" : rs.getString("isfinish"));
					caseVO.setV_city_name(rs.getString("city_name") == null ? "" : rs.getString("city_name"));
					caseVO.setV_country_id(rs.getString("country_id") == null ? "" : rs.getString("country_id"));
					caseVO.setV_country(rs.getString("country_name") == null ? "" : rs.getString("country_name"));

					list.add(caseVO); // Store the row in the list
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
		public String insertCaseCompetition(CaseCompetitionVO competitionVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			String competition_id = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);

				CallableStatement cs = null;
				cs = con.prepareCall(sp_insert_case_competition);

				cs.setString(1, null2Str(competitionVO.getCase_id()));
				cs.setString(2, null2Str(competitionVO.getGroup_id()));
				cs.setString(3, null2Str(competitionVO.getBcircle_id()));
				cs.setString(4, null2Str(competitionVO.getCompetition_no()));
				cs.setString(5, null2Str(competitionVO.getCompetition_name()));
				cs.setString(6, null2Str(competitionVO.getEvaluate_no()));
				cs.setString(7, null2Str(competitionVO.getEvaluate()));
				cs.setString(8, null2Str(competitionVO.getEvaluate_1_no()));
				cs.setString(9, null2Str(competitionVO.getEvaluate_1()));
				cs.setString(10, null2Str(competitionVO.getIndustry()));

				cs.registerOutParameter(11, Types.VARCHAR);

				cs.execute();
				competition_id = cs.getString(11);

				return competition_id;

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
		public void insertEvaluateCompetition(EvaluateCompetitionVO evaluateCompetitionVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);

				CallableStatement cs = null;
				cs = con.prepareCall(sp_insert_evaluate_competition);

				cs.setString(1, null2Str(evaluateCompetitionVO.getCompetition_id()));
				cs.setString(2, null2Str(evaluateCompetitionVO.getUser_id()));
				cs.setString(3, null2Str(evaluateCompetitionVO.getEvaluate_reason()));
				cs.setString(4, null2Str(evaluateCompetitionVO.getWeight()));
				cs.setString(5, evaluateCompetitionVO.getUser_authority());
				cs.setString(6, null2Str(evaluateCompetitionVO.getEvaluate_point()));
				cs.setString(7, null2Str(evaluateCompetitionVO.getEvaluate_1_point()));
				cs.setString(8, null2Str(evaluateCompetitionVO.getEvaluate_seq()));

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
		public List<UserVO> searchAllDB(String group_id) {
			List<UserVO> list = new ArrayList<UserVO>();
			UserVO UserVO = null;

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
					UserVO = new UserVO();
					UserVO.setUser_id(rs.getString("user_id"));
					UserVO.setGroup_id(rs.getString("group_id"));
					UserVO.setUser_name(rs.getString("user_name"));
					UserVO.setEmail(rs.getString("email"));
					UserVO.setPassword(rs.getString("password"));
					UserVO.setAdministrator(rs.getString("administrator"));
					list.add(UserVO);
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

		@Override
		public String getDecisionBcircleIdByName(String bcircle_name) {

			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String result = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_BD_by_name);
				pstmt.setString(1, bcircle_name);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					result = rs.getString("bcircle_id");
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
			return result == null ? "" : result;
		}

		@Override
		public List<CaseCompetitionVO> selectCase(String groupId) {
			List<CaseCompetitionVO> list = new ArrayList<CaseCompetitionVO>();
			CaseCompetitionVO caseCompetitionVO = null;

			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_case_competition);
				pstmt.setString(1, groupId);
				
				rs = pstmt.executeQuery();

				while (rs.next()) {
					caseCompetitionVO = new CaseCompetitionVO();
					// tb_case_competition
					caseCompetitionVO.setCompetition_id(null2Str(rs.getString("competition_id")));
					caseCompetitionVO.setCase_id(null2Str(rs.getString("case_id")));
					caseCompetitionVO.setGroup_id(null2Str(rs.getString("group_id")));
					caseCompetitionVO.setBcircle_id(null2Str(rs.getString("bcircle_id")));
					caseCompetitionVO.setCompetition_no(null2Int(rs.getInt("competition_no")));
					caseCompetitionVO.setCompetition_name(null2Str(rs.getString("competition_name")));
					caseCompetitionVO.setEvaluate_no(null2Int(rs.getInt("evaluate_no")));
					caseCompetitionVO.setEvaluate(null2Str(rs.getString("evaluate")));
					caseCompetitionVO.setEvaluate_1_no(null2Str(rs.getString("evaluate_1_no")));
					caseCompetitionVO.setEvaluate_1(null2Str(rs.getString("evaluate_1")));
					caseCompetitionVO.setEnding_time(null2Str(rs.getString("ending_time")));
					
					//優先排序
					StringJoiner joiner = new StringJoiner(",");
					if (rs.getString("result") != null && rs.getString("result") != "") {
						String[] businessDistrictList = rs.getString("result").split(";");
						for (int i = 0; i < businessDistrictList.length; i++ ) {
							String tmp = businessDistrictList[i];
							businessDistrictList[i] = tmp.split(",")[0];
						}
						
						for (String tmp : businessDistrictList) {
						    joiner.add(tmp.toString());
						}
					}
					String result = joiner == null ? "" : joiner.toString();
					
					caseCompetitionVO.setResult(null2Str(result));
					caseCompetitionVO.setIsfinish(null2Int(rs.getString("isfinish")));
					// tb_city
					caseCompetitionVO.setCity_city_name(null2Str(rs.getString("city_name")));
					caseCompetitionVO.setCity_country_id(null2Str(rs.getString("country_id")));
					// tb_country
					caseCompetitionVO.setCountry_country_name(null2Str(rs.getString("country_name")));
					// tb_bcircle
					caseCompetitionVO.setBcircle_bcircle_name(null2Str(rs.getString("bcircle_name")));

					list.add(caseCompetitionVO); // Store the row in the list
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
		public List<CaseCompetitionVO> selectCaseByCompetitionId(String competition_id) {
			List<CaseCompetitionVO> list = new ArrayList<CaseCompetitionVO>();
			CaseCompetitionVO caseCompetitionVO = null;

			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_case_competition_by_competition_id);
				pstmt.setString(1, null2Str(competition_id));

				rs = pstmt.executeQuery();
				while (rs.next()) {
					caseCompetitionVO = new CaseCompetitionVO();
					// tb_case_competition
					caseCompetitionVO.setCompetition_id(null2Str(rs.getString("competition_id")));
					caseCompetitionVO.setCase_id(null2Str(rs.getString("case_id")));
					caseCompetitionVO.setGroup_id(null2Str(rs.getString("group_id")));
					caseCompetitionVO.setBcircle_id(null2Str(rs.getString("bcircle_id")));
					caseCompetitionVO.setCompetition_no(null2Int(rs.getInt("competition_no")));
					caseCompetitionVO.setCompetition_name(null2Str(rs.getString("competition_name")));
					caseCompetitionVO.setEvaluate_no(null2Int(rs.getInt("evaluate_no")));
					caseCompetitionVO.setEvaluate(null2Str(rs.getString("evaluate")));
					caseCompetitionVO.setEvaluate_1_no(null2Str(rs.getString("evaluate_1_no")));
					caseCompetitionVO.setEvaluate_1(null2Str(rs.getString("evaluate_1")));
					caseCompetitionVO.setEnding_time(null2Str(rs.getString("ending_time")));
					caseCompetitionVO.setResult(null2Str(rs.getString("result")));
					caseCompetitionVO.setIsfinish(null2Int(rs.getString("isfinish")));
					// tb_city
					caseCompetitionVO.setCity_city_name(null2Str(rs.getString("city_name")));
					caseCompetitionVO.setCity_country_id(null2Str(rs.getString("country_id")));
					caseCompetitionVO.setV_decision_proposal(null2Str(rs.getString("decision_proposal")));
					// tb_country
					caseCompetitionVO.setCountry_country_name(null2Str(rs.getString("country_name")));

					list.add(caseCompetitionVO); // Store the row in the list
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
		public List<EvaluateCompetitionVO> selectEvaluateByCompetitionId(String competition_id) {
			List<EvaluateCompetitionVO> list = new ArrayList<EvaluateCompetitionVO>();
			EvaluateCompetitionVO evaluateCompetitionVO = null;

			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_evaluate_competition_by_competition_id);
				pstmt.setString(1, competition_id);

				rs = pstmt.executeQuery();
				while (rs.next()) {
					evaluateCompetitionVO = new EvaluateCompetitionVO();
					// tb_evaluate_competition
					evaluateCompetitionVO.setEvaluate_competition_id(null2Str(rs.getString("evaluate_competition_id")));
					evaluateCompetitionVO.setCompetition_id(null2Str(rs.getString("competition_id")));
					evaluateCompetitionVO.setUser_id(null2Str(rs.getString("user_id")));
					evaluateCompetitionVO.setEvaluate_reason(null2Str(rs.getString("evaluate_reason")));
					evaluateCompetitionVO.setWeight(null2Str(rs.getString("weight")));
					evaluateCompetitionVO.setUser_authority(null2Str(rs.getString("user_authority")));
					evaluateCompetitionVO.setEvaluate_point(null2Str(rs.getString("evaluate_point")));
					evaluateCompetitionVO.setEvaluate_1_point(null2Str(rs.getString("evaluate_1_point")));
					evaluateCompetitionVO.setEvaluate_seq(null2Str(rs.getString("evaluate_seq")));
					// tb_user
					evaluateCompetitionVO.setUser_name(null2Str(rs.getString("user_name")));
					list.add(evaluateCompetitionVO);
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
		public List<EvaluateCompetitionVO> selectEvaluateDetailByCompetitionId(String competition_id, String user_id) {
			List<EvaluateCompetitionVO> list = new ArrayList<EvaluateCompetitionVO>();
			EvaluateCompetitionVO evaluateCompetitionVO = null;

			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_decision_evaluate_competition_detail_by_competition_id);
				pstmt.setString(1, competition_id);
				pstmt.setString(2, user_id);

				rs = pstmt.executeQuery();
				while (rs.next()) {
					evaluateCompetitionVO = new EvaluateCompetitionVO();
					// tb_evaluate_competition
					evaluateCompetitionVO.setEvaluate_competition_id(null2Str(rs.getString("evaluate_competition_id")));
					evaluateCompetitionVO.setCompetition_id(null2Str(rs.getString("competition_id")));
					evaluateCompetitionVO.setUser_id(null2Str(rs.getString("user_id")));
					evaluateCompetitionVO.setEvaluate_1_point(null2Str(rs.getString("evaluate_1_point")));
					// tb_case_competition
					evaluateCompetitionVO.setEvaluate_no(null2Str(rs.getString("evaluate_no")));
					evaluateCompetitionVO.setEvaluate_1_no(null2Str(rs.getString("evaluate_1_no")));
					evaluateCompetitionVO.setEvaluate(null2Str(rs.getString("evaluate")));
					evaluateCompetitionVO.setEvaluate_1(null2Str(rs.getString("evaluate_1")));
					// tb_user
					evaluateCompetitionVO.setUser_name(null2Str(rs.getString("user_name")));
					list.add(evaluateCompetitionVO);
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

	private String null2Str(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	private Integer null2Int(Object object) {
		String s = object == null ? "0" : String.valueOf(object);
		return object == null ? 0 : Integer.valueOf(s);
	}
}
