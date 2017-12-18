package tw.com.sbi.scenariojob.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tw.com.sbi.common.controller.CommonMethod;
import tw.com.sbi.common.controller.RecordMsg;
import tw.com.sbi.vo.ScenarioJobVO;
import tw.com.sbi.vo.ScenarioResultVO;

public class ScenarioJob extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = LogManager.getLogger(ScenarioJob.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/text");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		ScenarioService caseService = null;

		String action = null2Str(request.getParameter("action"));
		
		if(!"get_session".equals(action)){
			logger.debug("=========== Action: " + action+" ===========");
		}
		
		if ("getJobInfo".equals(action)) {
			try {
				String group_id = request.getSession().getAttribute("group_id").toString();
				caseService = new ScenarioService();
				List<ScenarioJobVO> list = caseService.get_all_job(group_id);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(list);
				logger.debug("[Output]: "+subs(jsonStrList,60));
				response.getWriter().write(jsonStrList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}else if("select_all_scenario".equals(action)){
			try {
				caseService = new ScenarioService();
				List<ScenarioJobVO> list = caseService.select_all_scenario();
				int i=0;
				for(i=0;i<list.size();i++){
					list.get(i).setChild(caseService.get_scenario_child(list.get(i).getScenario_id()));
				}
				String jsonStrList = new Gson().toJson(list);
				logger.debug("[Output]: "+subs(jsonStrList,30));
				response.getWriter().write(jsonStrList);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}else if("insert_job".equals(action)){
			try {
				caseService = new ScenarioService();
				String group_id = request.getSession().getAttribute("group_id").toString();
				String scenario_id = request.getParameter("scenario_id");
				String job_name = request.getParameter("job_name");
				logger.debug("scenario_id:" + scenario_id);
				logger.debug("job_name:" + job_name);
				
				caseService.insert_job(group_id,scenario_id,job_name);
				List<ScenarioJobVO> list = caseService.get_all_job(group_id);
				String jsonStrList = new Gson().toJson(list);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}else if("delete_job".equals(action)){
			try {
				caseService = new ScenarioService();
				String group_id = request.getSession().getAttribute("group_id").toString();
				String job_id = request.getParameter("job_id");
				logger.debug("job_id:" + job_id);
				
				caseService.delete_job(job_id);
				List<ScenarioJobVO> list = caseService.get_all_job(group_id);
				String jsonStrList = new Gson().toJson(list);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}else if("update_job".equals(action)){
			try {
				caseService = new ScenarioService();
				String group_id = request.getSession().getAttribute("group_id").toString();
				String job_id = request.getParameter("job_id");
				String job_name = request.getParameter("job_name");
				logger.debug("job_id:" + job_id);
				logger.debug("job_name:" + job_name);
				
				caseService.update_job(job_id,job_name);
				
				List<ScenarioJobVO> list = caseService.get_all_job(group_id);
				String jsonStrList = new Gson().toJson(list);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}else if("click_next_step".equals(action)){
			HttpSession session = request.getSession(true);
			String scenario_job_id = request.getParameter("scenario_job_id");
			String scenario_job_page = request.getParameter("scenario_job_page");
			session.setAttribute("scenario_job_id", scenario_job_id);
			session.setAttribute("scenario_job_page", scenario_job_page);
			logger.debug("[Session]job_id: "+scenario_job_id);
			logger.debug("[Session]job_page: "+scenario_job_page);
			logger.debug("[Output]: success");
//			response.getWriter().write("{\"message\":\"success\",\"scenario_job_id\":\""+scenario_job_id+"\"}");
			response.getWriter().write("success");
			return;
			
		}else if("get_session".equals(action)){
//			CommonMethod.requestHeader(request);
			Enumeration<?> paramNames = request.getHeaderNames();
			while (paramNames.hasMoreElements()) {
				String paramName = (String) paramNames.nextElement();
				if("referer".equals(paramName)){
					RecordMsg.recordANAL(request,"refer",request.getHeader(paramName));
				}
			}
			
			String scenario_job_id=null2Str(request.getSession().getAttribute("scenario_job_id"));
			String scenario_job_page=null2Str(request.getSession().getAttribute("scenario_job_page"));
			if(scenario_job_id.length()>2){
				logger.debug("[Output]: scenario_job_id: " +scenario_job_id);
				logger.debug("[Output]: scenario_job_page: " +scenario_job_page);
			}
			response.getWriter().write("{\"scenario_job_id\":\""+scenario_job_id+"\",\"scenario_job_page\":\""+scenario_job_page+"\"}");
			return;
		}else if("clear_session".equals(action)){
			request.getSession().setAttribute("scenario_job_id","");
			request.getSession().setAttribute("scenario_job_page","");
			logger.debug("[Output]: clear_scenario_session ");
			response.getWriter().write("success");
			return;
		}else if("over_scenario".equals(action)){
			String scenario_job_id=null2Str(request.getSession().getAttribute("scenario_job_id"));
			String group_id = request.getSession().getAttribute("group_id").toString();

			caseService = new ScenarioService();
			ScenarioJobVO current_job = null;
			List<ScenarioJobVO> list = caseService.get_all_job(group_id);
			for(int i = 0;i < list.size(); i ++){
				if(scenario_job_id.equals(list.get(i).getJob_id())){
					current_job = list.get(i);
				}
	        }
			
			caseService.over_step(scenario_job_id,
					current_job.getNext_flow_id(),
					current_job.getFlow_seq(),
					"1",new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
			logger.debug("[Output]: over_it");
			response.getWriter().write("success");
			
			logger.debug("Clear session: scenario_job_id");
			logger.debug("Clear session: scenario_job_page");
			
			request.getSession().setAttribute("scenario_job_id","");
			request.getSession().setAttribute("scenario_job_page","");
			return;
		}else if("set_scenario_result".equals(action)){
			String group_id = request.getSession().getAttribute("group_id").toString();
			String scenario_job_id=null2Str(request.getSession().getAttribute("scenario_job_id"));
			String scenario_job_page=null2Str(request.getSession().getAttribute("scenario_job_page"));
			String current_page = null2Str(request.getParameter("current_page"));
			String category = null2Str(request.getParameter("category"));
			String result = null2Str(request.getParameter("result"));
			String png_name = null2Str(request.getParameter("png_name"));
			logger.debug("current_page:" + current_page);
			logger.debug("category:" + category);
			logger.debug("result:" + result);
			logger.debug("png_name:" + png_name);
			caseService = new ScenarioService();
			if(current_page.equals(scenario_job_page)){
				caseService.dealing_job_save_result(group_id,scenario_job_id,category,result,png_name);
				logger.debug("[Output]: success");
				response.getWriter().write("success");
			}else{
				logger.debug("[Output]: not_in_scenario");
				response.getWriter().write("not_in_scenario");
			}
			
			return;
		}else if("get_current_job_info".equals(action)){
			try {
				String group_id = request.getSession().getAttribute("group_id").toString();
				String job_id = request.getParameter("job_id");
				String jsonStrList="";
				logger.debug("job_id: " + job_id);
				
				caseService = new ScenarioService();
				List<ScenarioJobVO> list = caseService.get_all_job(group_id);
				for(int i = 0;i < list.size(); i ++){
					if(job_id.equals(list.get(i).getJob_id())){
						jsonStrList = new Gson().toJson(list.get(i));
					}
		        }
				logger.debug("[Output]: "+subs(jsonStrList,60));
				response.getWriter().write(jsonStrList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("full-range-log",e);
			}
		}else if("get_session_latlngzoom".equals(action)){
			String scenario_job_id=null2Str(request.getSession().getAttribute("scenario_job_id"));
			caseService = new ScenarioService();
			String lat_lng_zoom = caseService.get_memo(scenario_job_id);
			//caseService.set_memo(scenario_job_id,"");
			logger.debug("[Output]: "+lat_lng_zoom);
			response.getWriter().write(lat_lng_zoom);
			return;
		}else if("over_a_step".equals(action)){
			String scenario_job_id=null2Str(request.getSession().getAttribute("scenario_job_id"));
			String group_id = request.getSession().getAttribute("group_id").toString();
			String scenario_lat = request.getParameter("scenario_lat");
			String scenario_lng = request.getParameter("scenario_lng");
			String scenario_zoom = request.getParameter("scenario_zoom");
			logger.debug("scenario_lat: " + scenario_lat);
			logger.debug("scenario_lng: " + scenario_lng);
			logger.debug("scenario_zoom: " + scenario_zoom);
			if(scenario_lat.length()>2){
				logger.debug("records_cenario_lat_lng_zoom");
				caseService = new ScenarioService();
				caseService.set_memo(scenario_job_id, "{\"scenario_lat\":\""+scenario_lat+"\",\"scenario_lng\":\""+scenario_lng+"\",\"scenario_zoom\":\""+scenario_zoom+"\"}");
			}
			caseService = new ScenarioService();
			ScenarioJobVO current_job = null;
			List<ScenarioJobVO> list = caseService.get_all_job(group_id);
			for(int i = 0;i < list.size(); i ++){
				if(scenario_job_id.equals(list.get(i).getJob_id())){
					current_job = list.get(i);
				}
	        }
			if(current_job.getFlow_seq() == null){
				current_job.setFlow_seq("0");
			}
			if(Integer.parseInt(current_job.getFlow_seq())+1 ==Integer.parseInt(current_job.getMax_flow_seq())){
				caseService.over_step(scenario_job_id,current_job.getNext_flow_id(),((Integer.parseInt(current_job.getFlow_seq())+1)+""),(1+""),new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
				logger.debug("[Output]: "+"finished");
				response.getWriter().write("finished");
				
				logger.debug("Clear session: scenario_job_id");
				logger.debug("Clear session: scenario_job_page");
				
				request.getSession().setAttribute("scenario_job_id","");
				request.getSession().setAttribute("scenario_job_page","");
			}else{
				caseService.over_step(scenario_job_id, current_job.getNext_flow_id(),((Integer.parseInt(current_job.getFlow_seq())+1)+""),(0+""), null);
				current_job = null;
				List<ScenarioJobVO> list2 = caseService.get_all_job(group_id);
				for(int i = 0;i < list2.size(); i ++){
					if(scenario_job_id.equals(list2.get(i).getJob_id())){
						current_job = list2.get(i);
					}
		        }
				logger.debug("[Output]: "+current_job.getNext_flow_page());
				logger.debug("Set session: scenario_job_id : "+current_job.getNext_flow_page());
				response.getWriter().write(current_job.getNext_flow_page());
				request.getSession().setAttribute("scenario_job_page",current_job.getNext_flow_page());
			}
		}else if("last_step".equals(action)){
			String scenario_job_id=null2Str(request.getSession().getAttribute("scenario_job_id"));
			String group_id = request.getSession().getAttribute("group_id").toString();
			ScenarioJobVO current_job = null;
			caseService = new ScenarioService();
			caseService.dealing_job_reverse(scenario_job_id);
			
			List<ScenarioJobVO> list2 = caseService.get_all_job(group_id);
			for(int i = 0;i < list2.size(); i ++){
				if(scenario_job_id.equals(list2.get(i).getJob_id())){
					current_job = list2.get(i);
				}
	        }
			String reverse_page =current_job.getNext_flow_page().length()>2?current_job.getNext_flow_page():"scenarioJob.jsp";
			
			logger.debug("[Output]: "+reverse_page);
			logger.debug("Set session: scenario_job_page : "+reverse_page);
			response.getWriter().write(reverse_page);
			request.getSession().setAttribute("scenario_job_page",reverse_page);
			return;
		}else if("jump_step".equals(action)){
			String scenario_job_id=null2Str(request.getParameter("scenario_job_id"));
			String goto_flow=null2Str(request.getParameter("goto_flow"));
			String group_id = request.getSession().getAttribute("group_id").toString();
			logger.debug("scenario_job_id: " + scenario_job_id);
			logger.debug("goto_flow: " + goto_flow);
			
			ScenarioJobVO current_job = null;
			caseService = new ScenarioService();
			
			List<ScenarioJobVO> list = caseService.get_all_job(group_id);
			for(int i = 0;i < list.size(); i ++){
				if(scenario_job_id.equals(list.get(i).getJob_id())){
					current_job = list.get(i);
				}
	        }
			String set_flow_id="";
			String set_flow_page="";
			List<ScenarioJobVO> flow_list = caseService.get_scenario_child(current_job.getScenario_id());
			for(int i = 0;i < flow_list.size(); i ++){
				if(goto_flow.equals(flow_list.get(i).getFlow_seq())){
					set_flow_id=flow_list.get(i).getFlow_id();
				}
				if(goto_flow.equals((Integer.parseInt(flow_list.get(i).getFlow_seq())-1)+"")){
					set_flow_page=flow_list.get(i).getPage();
				}
	        }
			if(Integer.parseInt(goto_flow) ==Integer.parseInt(current_job.getMax_flow_seq())){
				caseService.over_step(scenario_job_id, set_flow_id, goto_flow, "1", new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
			}else{
				caseService.over_step(scenario_job_id, set_flow_id, goto_flow, "0", null);
			}
			
			List<ScenarioResultVO> old_json_result = new Gson().fromJson(current_job.getResult(), new TypeToken<List<ScenarioResultVO>>() {}.getType());
			for( java.util.Iterator<ScenarioResultVO> result_item = old_json_result.listIterator(); result_item.hasNext(); ){
				ScenarioResultVO currentElement = result_item.next();
//				logger.debug((Integer.parseInt(goto_flow)+1)+" #### "+(Integer.parseInt(currentElement.getStep()))+" ### "+set_flow_page);
				if(Integer.parseInt(goto_flow)+1==Integer.parseInt(currentElement.getStep())
						&&!"POI.jsp".equals(set_flow_page)){
					result_item.remove();
				}
			}
			String jsonStrList = new Gson().toJson(old_json_result);
			caseService.dealing_job_update_result(scenario_job_id,jsonStrList);
			logger.debug("[Output]: jump_to_success");
			response.getWriter().write("jump_to_success");
			return;
		}else if("clear_result_to_store".equals(action)){
			String scenario_job_id=null2Str(request.getSession().getAttribute("scenario_job_id"));
			String group_id = request.getSession().getAttribute("group_id").toString();
			ScenarioJobVO current_job = null;
			caseService = new ScenarioService();
			
			List<ScenarioJobVO> list = caseService.get_all_job(group_id);
			for(int i = 0;i < list.size(); i ++){
				if(scenario_job_id.equals(list.get(i).getJob_id())){
					current_job = list.get(i);
				}
	        }
			
			List<ScenarioResultVO> old_json_result = new Gson().fromJson(current_job.getResult(), new TypeToken<List<ScenarioResultVO>>() {}.getType());
			for( java.util.Iterator<ScenarioResultVO> result_item = old_json_result.listIterator(); result_item.hasNext(); ){
				ScenarioResultVO currentElement = result_item.next();
				if(Integer.parseInt(current_job.getFlow_seq())+1==Integer.parseInt(currentElement.getStep())){	
					if("查詢商圈".equals(currentElement.getCategory())
						||"查詢POI".equals(currentElement.getCategory())
						||"查詢熱力圖".equals(currentElement.getCategory())
						||"環域分析".equals(currentElement.getCategory())
						||"查詢地址".equals(currentElement.getCategory())
						||"完成此步驟".equals(currentElement.getCategory())){
						result_item.remove();
					}
				}
			}
			String jsonStrList = new Gson().toJson(old_json_result);
			caseService.dealing_job_update_result(scenario_job_id,jsonStrList);
			logger.debug("[Output]: clear_success");
			response.getWriter().write("clear_success");
		}
	
	}
	
	/*************************** 處理業務邏輯 ****************************************/
	public class ScenarioService {
		private case_interface dao;

		public ScenarioService() {
			dao = new ScenarioDAO();
		}
		public List<ScenarioJobVO> get_all_job(String group_id) {
			return dao.get_all_job(group_id);
		}
		public void insert_job(String group_id ,String scenario_id, String job_name) {
			dao.insert_job(group_id , scenario_id,job_name);
		}
		public void update_job(String job_id, String job_name) {
			dao.update_job(job_id,job_name);
		}
		public void delete_job(String job_id) {
			dao.delete_job(job_id);
		}
		public List<ScenarioJobVO> select_all_scenario(){
			return dao.select_all_scenario();
		}
		public void dealing_job_save_result(String group_id,String job_id, String category, String result, String png_name){
			dao.dealing_job_save_result(group_id,job_id,category,result, png_name);
		}
		public void over_step(String job_id,String flow_id,String flow_seq,String finished,String finish_time) {
			dao.over_step(job_id,flow_id,flow_seq,finished,finish_time);
		}
		public List<ScenarioJobVO> get_scenario_child(String scenario_id){
			return dao.get_scenario_child(scenario_id);
		}
		public void dealing_job_reverse(String job_id){
			dao.dealing_job_reverse(job_id);
		}
		public void set_memo(String job_id,String memo){
			dao.set_memo(job_id, memo);
		}
		public String get_memo(String job_id){
			return dao.get_memo(job_id);
		}
		public void dealing_job_update_result(String job_id,String json_result){
			dao.dealing_job_update_result( job_id, json_result);
		}
	}

	/*************************** 制定規章方法 ****************************************/
	interface case_interface {
		public List<ScenarioJobVO> get_all_job(String group_id);
		public void insert_job(String group_id, String scenario_id, String job_name);
		public void update_job(String job_id, String job_name);
		public void delete_job(String job_id);
		public List<ScenarioJobVO> select_all_scenario();
		public void dealing_job_save_result(String group_id,String job_id, String category, String result, String png_name);
		public void over_step(String job_id,String flow_id,String flow_seq,String finished,String finish_time);
		public List<ScenarioJobVO> get_scenario_child(String scenario_id);
		public void dealing_job_reverse(String job_id);
		public void set_memo(String job_id,String memo);
		public String get_memo(String job_id);
		public void dealing_job_update_result(String job_id,String json_result);
	}
	
	/*************************** 操作資料庫 ****************************************/
	public class ScenarioDAO implements case_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		
		private static final String sp_select_scenario_job_info = "call sp_select_scenario_job_info(?)";
		private static final String sp_get_scenario_flow_max_seq = "call sp_get_scenario_flow_max_seq(?)" ;
		private static final String sp_insert_scenario_job = "call sp_insert_scenario_job (?,?,?)";
		private static final String sp_update_scenario_job = "call sp_update_scenario_job (?,?)";
		private static final String sp_delete_scenario_job = "call sp_delete_scenario_job (?)";
		private static final String sp_update_scenario_job_result = "call sp_update_scenario_job_result (?,?)";
		private static final String sp_select_scenario = "call sp_select_scenario()";
		private static final String sp_update_scenario_job_next_step = "call sp_update_scenario_job_next_step (?,?,?,?,?)";
		private static final String sp_get_scenario_child = "call sp_get_scenario_child (?)";
		private static final String sp_select_scenario_job_last_step = "call sp_select_scenario_job_last_step (?)";
		private static final String sp_select_scenario_job_memo = "call sp_select_scenario_job_memo (?)";
		private static final String sp_update_scenario_job_memo = "call sp_update_scenario_job_memo (?,?)";
		@Override
		public List<ScenarioJobVO> get_all_job(String group_id){
			List<ScenarioJobVO> list = new ArrayList<ScenarioJobVO>();
			ScenarioJobVO scenarioJob = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_scenario_job_info);
				pstmt.setString(1, group_id);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					scenarioJob = new ScenarioJobVO();
					scenarioJob.setId(null2Str(rs.getString("id")));
					scenarioJob.setJob_id(null2Str(rs.getString("job_id")));
					scenarioJob.setJob_name(null2Str(rs.getString("job_name")));
					scenarioJob.setGroup_id(null2Str(rs.getString("group_id")));
					scenarioJob.setFlow_id(null2Str(rs.getString("flow_id")));
					scenarioJob.setFlow_name(null2Str(rs.getString("this.flow_name")));
					scenarioJob.setFlow_function(null2Str(rs.getString("this.flow_function")));
					
					scenarioJob.setScenario_id(null2Str(rs.getString("this.scenario_id")));
					scenarioJob.setScenario_name(null2Str(rs.getString("scenario_name")));
					scenarioJob.setPage(null2Str(rs.getString("this.page")));
					scenarioJob.setFlow_seq(null2Str(rs.getString("this.flow_seq")));
					scenarioJob.setJob_time(null2Str(rs.getString("job_time")));
					scenarioJob.setResult(null2Str(rs.getString("result")));
					scenarioJob.setFinished(null2Str(rs.getString("finished")));
					scenarioJob.setFinish_time(null2Str(rs.getString("finish_time")));
					scenarioJob.setNext_flow_id(null2Str(rs.getString("next.flow_id")));
					scenarioJob.setNext_flow_name(null2Str(rs.getString("next.flow_name")));
					scenarioJob.setNext_flow_page(null2Str(rs.getString("next.page")));
					scenarioJob.setNext_flow_explanation(null2Str(rs.getString("next.explanation")));
					scenarioJob.setNext_flow_guide(null2Str(rs.getString("next.guide")));
					scenarioJob.setMax_flow_seq(
						get_max_seq(scenarioJob.getScenario_id())
					);
					list.add(scenarioJob);
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
		public void insert_job(String group_id, String scenario_id, String job_name){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_insert_scenario_job);
				pstmt.setString(1, group_id);
				pstmt.setString(2, scenario_id);
				pstmt.setString(3, job_name);
				
				pstmt.executeUpdate();
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
		}
		public void update_job(String job_id, String job_name){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_update_scenario_job);
				pstmt.setString(1, job_id);
				pstmt.setString(2, job_name);
				
				pstmt.executeUpdate();
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
		}
		
		public void delete_job(String job_id){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_delete_scenario_job);
				pstmt.setString(1, job_id);
				pstmt.executeUpdate();
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
		}
		public String get_max_seq(String scenario_id){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String ret = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_scenario_flow_max_seq);
				pstmt.setString(1, scenario_id);
				rs = pstmt.executeQuery();
				while (rs.next()) {	
					ret = null2Str(rs.getString("max_seq"));
				}
				return ret;
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
		}
		public List<ScenarioJobVO> select_all_scenario(){
			List<ScenarioJobVO> list = new ArrayList<ScenarioJobVO>();
			ScenarioJobVO scenarioJobVO = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_scenario);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					scenarioJobVO = new ScenarioJobVO();
					scenarioJobVO.setScenario_id(null2Str(rs.getString("scenario_id")));
					scenarioJobVO.setScenario_name(null2Str(rs.getString("scenario_name")));
					scenarioJobVO.setResult(null2Str(rs.getString("explanation")));
					list.add(scenarioJobVO);
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
		
		public void dealing_job_reverse(String job_id){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String old_result = "[]";
			String last_flow_id ="";
			String last_flow_seq ="";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_scenario_job_last_step);
				
				pstmt.setString(1, job_id);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					last_flow_id = null2Str(rs.getString("last.flow_id"));
					last_flow_seq = null2Str(rs.getString("last.flow_seq"));
					old_result=null2Str(rs.getString("result"));
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
			
			List<ScenarioResultVO> old_json_result = new Gson().fromJson(old_result, new TypeToken<List<ScenarioResultVO>>() {}.getType());
			for( java.util.Iterator<ScenarioResultVO> result_item = old_json_result.listIterator(); result_item.hasNext(); ){
				ScenarioResultVO currentElement = result_item.next();
				if(Integer.parseInt(last_flow_seq)+2==Integer.parseInt(currentElement.getStep())){
					result_item.remove();
				}else if(Integer.parseInt(last_flow_seq)+1==Integer.parseInt(currentElement.getStep())){
					result_item.remove();
				}
			}
			this.over_step(job_id, last_flow_id, last_flow_seq, "0", null);
			String jsonStrList = new Gson().toJson(old_json_result);
			this.dealing_job_update_result(job_id,jsonStrList);
		}
		
		public void dealing_job_save_result(String group_id, String job_id, String category, String result, String png_name){
			ScenarioResultVO new_one= new ScenarioResultVO();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String old_result = "[]";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_scenario_job_info);
				
				pstmt.setString(1, group_id);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					if(job_id.equals(null2Str(rs.getString("job_id")))){
						new_one.setScenario_name(null2Str(rs.getString("scenario_name")));
						new_one.setStep(       (Integer.parseInt(null2Str(rs.getString("flow_seq")))+1)+""          );
						new_one.setFlow_name(null2Str(rs.getString("next.flow_name")));
						new_one.setPage(null2Str(rs.getString("next.page")));
						new_one.setCategory(category);
						new_one.setResult(result);
						new_one.setPng_name(png_name);
						old_result=null2Str(rs.getString("result"));
					}
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
			List<ScenarioResultVO> old_json_result = new Gson().fromJson(old_result, new TypeToken<List<ScenarioResultVO>>() {}.getType());
			old_json_result.add(new_one);
			String jsonStrList = new Gson().toJson(old_json_result);
			this.dealing_job_update_result(job_id,jsonStrList);
		}
		public void dealing_job_update_result(String job_id,String json_result){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_update_scenario_job_result);
				pstmt.setString(1, job_id);
				pstmt.setString(2, json_result);
				
				pstmt.executeUpdate();
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
		}
		public void over_step(String job_id,String flow_id,String flow_seq,String finished,String finish_time){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_update_scenario_job_next_step);
				pstmt.setString(1, job_id);
				pstmt.setString(2, flow_id);
				pstmt.setString(3, flow_seq);
				pstmt.setString(4, finished);
				pstmt.setString(5, finish_time);
				
				pstmt.executeUpdate();
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
		}
		public List<ScenarioJobVO> get_scenario_child(String scenario_id){
			List<ScenarioJobVO> list = new ArrayList<ScenarioJobVO>();
			ScenarioJobVO scenarioJob = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_get_scenario_child);
				pstmt.setString(1, scenario_id);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					scenarioJob = new ScenarioJobVO();
					scenarioJob.setFlow_id(null2Str(rs.getString("flow_id")));
					scenarioJob.setScenario_id(null2Str(rs.getString("scenario_id")));
					scenarioJob.setFlow_seq(null2Str(rs.getString("flow_seq")));
					scenarioJob.setFlow_name(null2Str(rs.getString("flow_name")));
					scenarioJob.setFlow_function(null2Str(rs.getString("flow_function")));
					scenarioJob.setPage(null2Str(rs.getString("page")));
					scenarioJob.setNext_flow_explanation(null2Str(rs.getString("explanation")));
					scenarioJob.setNext_flow_guide(null2Str(rs.getString("guide")));
					list.add(scenarioJob);
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
		public String get_memo(String job_id){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String memo="";

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_scenario_job_memo);
				pstmt.setString(1, job_id);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					memo=null2Str(rs.getString("memo"));
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
			return memo;
		}
		public void set_memo(String job_id,String memo){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_update_scenario_job_memo);
				pstmt.setString(1, job_id);
				pstmt.setString(2, memo);
				
				pstmt.executeUpdate();
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
		}		
	}
	private String subs(String str,int l) {
		if (str.length()>l){
			return str.substring(0, l)+"...";
		}else{
			return str;
		}
	}
	
	private String null2Str(Object object) {
		if (object instanceof Timestamp)
			return object == null ? "" : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(object);
		return object == null ? "" : object.toString().trim();
	}
}
