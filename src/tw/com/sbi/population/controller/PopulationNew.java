package tw.com.sbi.population.controller;

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

import tw.com.sbi.vo.PopulationNewVO;
import tw.com.sbi.vo.PopulationDistrictsVO;
import java.util.Iterator;
import java.text.DecimalFormat;

public class PopulationNew extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PopulationNew.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+"?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String[] result=new String[1];
		String[] result_id=new String[1];
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
			String action= request.getParameter("action");
			logger.debug("Action: " + action);
			
			if("search_county".equals(action)){
				pstmt = con.prepareStatement("SELECT DISTINCT county_name, county_id FROM population_index_village");
				rs = pstmt.executeQuery();
				int count,k=0;if (rs.last()){count = rs.getRow();}else{count = 0;}rs.beforeFirst();
				result=new String[count];
				result_id=new String[count];
			    while (rs.next()) {
			    	result[k]=rs.getString("county_name");
			    	result_id[k]=rs.getString("county_id");
			    	k++;
				}
			    Gson gson = new Gson();
				String jsonStrList = gson.toJson(result);
				response.getWriter().write("{\"result\":"+jsonStrList+",\"result_id\":"+gson.toJson(result_id)+"}");
			}else if("search_town".equals(action)){
				String county= request.getParameter("county");
				pstmt = con.prepareStatement("SELECT DISTINCT town,town_id FROM population_index_village WHERE county_id = ? AND TOWN_ID IS NOT NULL ");
				pstmt.setString(1,county);
				rs = pstmt.executeQuery();
				int count,k=0;if (rs.last()){count = rs.getRow();}else{count = 0;}rs.beforeFirst();
				result=new String[count];
				result_id=new String[count];
			    while (rs.next()) {
			    	result[k]=rs.getString("TOWN");
			    	result_id[k]=rs.getString("TOWN_ID");
			    	k++;
				}
			    Gson gson = new Gson();
				String jsonStrList = gson.toJson(result);
				response.getWriter().write("{\"result\":"+jsonStrList+",\"result_id\":"+gson.toJson(result_id)+"}");
			}else if("search_village".equals(action)){
				String town= request.getParameter("town");
				pstmt = con.prepareStatement("SELECT DISTINCT VILLAGE,V_ID FROM population_index_village WHERE town_id = ? AND V_ID IS NOT NULL ");
				pstmt.setString(1,town);
				rs = pstmt.executeQuery();
				int count,k=0;if (rs.last()){count = rs.getRow();}else{count = 0;}rs.beforeFirst();
				result=new String[count];
				result_id=new String[count];
			    while (rs.next()) {
			    	result[k]=rs.getString("VILLAGE");
			    	result_id[k]=rs.getString("V_ID");
			    	k++;
				}
			    Gson gson = new Gson();
				String jsonStrList = gson.toJson(result);
				response.getWriter().write("{\"result\":"+jsonStrList+",\"result_id\":"+gson.toJson(result_id)+"}");
			}
			PopulationService service = new PopulationService();
			PopulationNewVO populationVO = new PopulationNewVO();
			
			if("search_t_age_county".equals(action)){
				//3th
				//call sp_select_population_dynamic('age_county','f','0_4','104');
				String gender= null2str(request.getParameter("gender"));
				String age= null2str(request.getParameter("age"));
				String year= null2str(request.getParameter("year"));
				logger.debug("table: " + "age_county");
				logger.debug("gender: " + gender);
				logger.debug("age: " + age);
				logger.debug("year: " + year);
				
				populationVO.setRequest_table("age_county");
				populationVO.setRequest_sex(gender);
				populationVO.setRequest_data_name(age);
				populationVO.setRequest_data_info_time(year);
				
				List<PopulationNewVO> populationlist = service.selectData(populationVO);
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if("search_t_age_edu_county".equals(action)){
				//4th
				//call sp_select_population_dynamic('age_edu_county','all','_15_19_senior','104');
				String gender= null2str(request.getParameter("gender"));
				String age= null2str(request.getParameter("age"));
				String edu= null2str(request.getParameter("edu"));
				String year= null2str(request.getParameter("year"));
				logger.debug("table: " + "age_edu_county");
				logger.debug("gender: " + gender);
				logger.debug("age: " + age);
				logger.debug("edu: " + edu);
				logger.debug("year: " + year);
				
				populationVO.setRequest_table("age_edu_county");
				populationVO.setRequest_sex(gender);
				populationVO.setRequest_data_name("_"+age+"_"+edu);
				populationVO.setRequest_data_info_time(year);
				
				List<PopulationNewVO> populationlist = service.selectData(populationVO);
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if("search_t_edu_county".equals(action)){
				//2nd
				//call sp_select_population_dynamic('edu_county','all','_phd','104');
				String gender= null2str(request.getParameter("gender"));
				String edu= null2str(request.getParameter("edu"));
				String year= null2str(request.getParameter("year"));
				logger.debug("table: " + "edu_county");
				logger.debug("gender: " + gender);
				logger.debug("edu: " + edu);
				logger.debug("year: " + year);
				
				populationVO.setRequest_table("edu_county");
				populationVO.setRequest_sex(gender);
				populationVO.setRequest_data_name("_"+edu);
				populationVO.setRequest_data_info_time(year);
				
				List<PopulationNewVO> populationlist = service.selectData(populationVO);
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if("search_t_marriage_county".equals(action)){
				//5th
				//call sp_select_population_dynamic('marriage_county','m','unmarried','104');
				String gender= null2str(request.getParameter("gender"));
				String marriage= null2str(request.getParameter("marriage"));
				String year= null2str(request.getParameter("year"));
				
				logger.debug("table: " + "edu_county");
				logger.debug("gender: " + gender);
				logger.debug("marriage: " + marriage);
				logger.debug("year: " + year);
				
				populationVO.setRequest_table("marriage_county");
				populationVO.setRequest_sex(gender);
				populationVO.setRequest_data_name(marriage);
				populationVO.setRequest_data_info_time(year);
				
				List<PopulationNewVO> populationlist = service.selectData(populationVO);
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if("search_district_three_levels".equals(action)){
				//1st
				//call sp_select_population_index_village('10002','10002010','10002010-001','101');
				String county_id= null2str(request.getParameter("county_id"));
				String town_id= null2str(request.getParameter("town_id"));
				String village_id= null2str(request.getParameter("village_id"));
				String year= null2str(request.getParameter("year"));
				
				logger.debug("table: " + "population_index_village");
				logger.debug("county_id: " + county_id);
				logger.debug("town_id: " + town_id);
				logger.debug("village_id: " + village_id);
				logger.debug("year: " + year);
				PopulationDistrictsVO requestVO = new PopulationDistrictsVO();
				requestVO.setRequest_county_id(county_id);
				requestVO.setRequest_town_id(town_id);
				requestVO.setRequest_village_id(village_id);
				requestVO.setRequest_data_info_time(year);
				PopulationDistrictsVO tofill = new PopulationDistrictsVO();
				service.selectDistrictData(requestVO,tofill);
				service.selectDistrictDataAge(requestVO,tofill);
				
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(tofill);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
				
			}else if("select_age_years".equals(action)){
				List<PopulationNewVO> populationlist = service.getYears("age_county");
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}else if("select_age_edu_years".equals(action)){
				List<PopulationNewVO> populationlist = service.getYears("age_edu_county");
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}else if("select_edu_years".equals(action)){
				List<PopulationNewVO> populationlist = service.getYears("edu_county");
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}else if("select_marriage_years".equals(action)){
				List<PopulationNewVO> populationlist = service.getYears("marriage_county");
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}else if("select_village_years".equals(action)){
				List<PopulationNewVO> populationlist = service.getYears("population_index_village");
				Gson gson = new Gson();
				String jsonStrList = gson.toJson(populationlist);
				logger.debug("[Output]: "+jsonStrList);
				response.getWriter().write(jsonStrList);
			}
			
		} catch (SQLException se) {
			System.out.println("ERROR WITH: "+se);
			logger.debug("full-range-log",se);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException("A database error occured. " + cnfe.getMessage());
		}
		return;
	}
	
	public class PopulationService {
		private population_interface dao;

		public PopulationService() {
			dao = new PopulationDAO();
		}
		public List<PopulationNewVO> selectData(PopulationNewVO populationVO) {
			return dao.selectData(populationVO);
		}
		public List<PopulationNewVO> getYears(String table) {
			return dao.getYears(table);
		}
		public PopulationDistrictsVO selectDistrictData(PopulationDistrictsVO requestVO,PopulationDistrictsVO tofill) {
			return dao.selectDistrictData(requestVO,tofill);
		}
		public PopulationDistrictsVO selectDistrictDataAge(PopulationDistrictsVO requestVO,PopulationDistrictsVO tofill) {
			return dao.selectDistrictDataAge(requestVO,tofill);
		}
	}
	
	interface population_interface {
		public List<PopulationNewVO> selectData(PopulationNewVO populationVO);
		public List<PopulationNewVO> getYears(String table);
		public PopulationDistrictsVO selectDistrictData(PopulationDistrictsVO requestVO,PopulationDistrictsVO tofill);
		public PopulationDistrictsVO selectDistrictDataAge(PopulationDistrictsVO requestVO,PopulationDistrictsVO tofill);
	}
	
	/*******************************************************************/
	class PopulationDAO implements population_interface {
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		//private final String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");

		//stored procedure
		private static final String sp_select_population_dynamic = "call sp_select_population_dynamic(?,?,?,?)";
		private static final String sp_select_population_index_village_years = "call sp_select_population_index_village_years()";
		private static final String sp_select_age_county_years = "call sp_select_age_county_years()";
		private static final String sp_select_age_edu_county_years = "call sp_select_age_edu_county_years()";
		private static final String sp_select_edu_county_years = "call sp_select_edu_county_years()";
		private static final String sp_select_marriage_county_years = "call sp_select_marriage_county_years()";
		private static final String sp_select_population_index_village = "call sp_select_population_index_village(?,?,?,?)"; 
		private static final String sp_select_population_village = "call sp_select_population_village(?,?,?,?)";
		
		@Override
		public List<PopulationNewVO> selectData(PopulationNewVO requestVO) {
			List<PopulationNewVO> list = new ArrayList<PopulationNewVO>();
			PopulationNewVO populationNewVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			int total_amount=0;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_population_dynamic);
				pstmt.setString(1, requestVO.getRequest_table());
				pstmt.setString(2, requestVO.getRequest_sex());
				pstmt.setString(3, requestVO.getRequest_data_name());
				pstmt.setString(4, requestVO.getRequest_data_info_time());
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					populationNewVO = new PopulationNewVO();
					populationNewVO.setCounty_data(null2str(rs.getString("data")));
					populationNewVO.setCounty_name(null2str(rs.getString("county_name")));
					total_amount += Integer.parseInt(populationNewVO.getCounty_data());
					list.add(populationNewVO); // Store the row in the list
				}
				
				if(total_amount==0){
					total_amount=1;
				}
				for( Iterator<PopulationNewVO> result_item = list.listIterator(); result_item.hasNext(); ){
					PopulationNewVO currentElement = result_item.next();
					DecimalFormat df=new DecimalFormat("0.00%");
					currentElement.setCounty_data_percent(df.format((Float.parseFloat(currentElement.getCounty_data())/total_amount)));
				}
				
			} catch (SQLException se) {
				// Handle any driver errors
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

		@Override
		public List<PopulationNewVO> getYears(String table) {
			// TODO Auto-generated method stub
			List<PopulationNewVO> list = new ArrayList<PopulationNewVO>();
			PopulationNewVO populationNewVO = null;
			
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				
				String query_str="";
				if("age_county".equals(table)){
					query_str = sp_select_age_county_years ;
				}else if("age_edu_county".equals(table)){
					query_str = sp_select_age_edu_county_years ;
				}else if("edu_county".equals(table)){
					query_str = sp_select_edu_county_years ;
				}else if("marriage_county".equals(table)){
					query_str = sp_select_marriage_county_years ;
				}else if("population_index_village".equals(table)){
					query_str = sp_select_population_index_village_years ;
				}
				pstmt = con.prepareStatement(query_str);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					populationNewVO = new PopulationNewVO();
					populationNewVO.setRequest_data_info_time( null2str(rs.getString("years")));
					list.add(populationNewVO);
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
		public PopulationDistrictsVO selectDistrictData(PopulationDistrictsVO requestVO,PopulationDistrictsVO populationDistrictsVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_population_index_village);
				pstmt.setString(1, requestVO.getRequest_county_id());
				pstmt.setString(2, requestVO.getRequest_town_id());
				pstmt.setString(3, requestVO.getRequest_village_id());
				pstmt.setString(4, requestVO.getRequest_data_info_time());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					populationDistrictsVO.setCounty_id(null2str(rs.getString("county_id")));
					populationDistrictsVO.setCounty_name(null2str(rs.getString("county_name")));
					populationDistrictsVO.setTown_id(null2str((requestVO.getRequest_town_id()==""?"":rs.getString("town_id"))));
					populationDistrictsVO.setTown_name(null2str((requestVO.getRequest_town_id()==""?"":rs.getString("town"))));
					populationDistrictsVO.setVillage_id(null2str((requestVO.getRequest_village_id()==""?"":rs.getString("V_ID"))));
					populationDistrictsVO.setVillage_name(null2str((requestVO.getRequest_village_id()==""?"":rs.getString("VILLAGE"))));
					
					if(!"".equals(requestVO.getRequest_village_id())){
						if(rs.getString("A15UP_M1_M_CNT")!=null){
							populationDistrictsVO.setM_F_RAT(null2str(rs.getString("M_F_RAT")));
							populationDistrictsVO.setP_H_CNT(null2str(rs.getString("P_H_CNT")));
							populationDistrictsVO.setP_DEN(null2str(rs.getString("P_DEN")));
							populationDistrictsVO.setDEPENDENCY_RAT(null2str(rs.getString("DEPENDENCY_RAT")));
							populationDistrictsVO.setA0A14_A15A65_RAT(null2str(rs.getString("A0a14_A15A65_RAT")));
							populationDistrictsVO.setA65UP_A15A64_RAT(null2str(rs.getString("A65up_A15A64_RAT")));
							populationDistrictsVO.setA65_A0A14_RAT(null2str(rs.getString("A65_A0A14_RAT")));
							populationDistrictsVO.setSpace02("");
						}
					}
					
					populationDistrictsVO.setA15UP_M1_M_CNT(null2str(rs.getString("A15UP_M1_M_CNT")));
					populationDistrictsVO.setA15UP_M1_F_CNT(null2str(rs.getString("A15UP_M1_F_CNT")));
					populationDistrictsVO.setA15UP_M2_M_CNT(null2str(rs.getString("A15UP_M2_M_CNT")));
					populationDistrictsVO.setA15UP_M2_F_CNT(null2str(rs.getString("A15UP_M2_F_CNT")));
					populationDistrictsVO.setA15UP_M3_M_CNT(null2str(rs.getString("A15UP_M3_M_CNT")));
					populationDistrictsVO.setA15UP_M3_F_CNT(null2str(rs.getString("A15UP_M3_F_CNT")));
					populationDistrictsVO.setA15UP_M4_M_CNT(null2str(rs.getString("A15UP_M4_M_CNT")));
					populationDistrictsVO.setA15UP_M4_F_CNT(null2str(rs.getString("A15UP_M4_F_CNT")));
					
					if(rs.getString("no_edu")!=null){
						populationDistrictsVO.setNo_edu(null2str(rs.getString("no_edu")));
						populationDistrictsVO.setSelf_edu(null2str(rs.getString("self_edu")));
						populationDistrictsVO.setEle_edu(null2str(rs.getString("ele_edu")));
						populationDistrictsVO.setJun_edu(null2str(rs.getString("jun_edu")));
						populationDistrictsVO.setSen_edu(null2str(rs.getString("sen_edu")));
						populationDistrictsVO.setCol_edu(null2str(rs.getString("col_edu")));
						populationDistrictsVO.setUni_edu(null2str(rs.getString("uni_edu")));
						populationDistrictsVO.setMas_edu(null2str(rs.getString("mas_edu")));
						populationDistrictsVO.setPhd_edu(null2str(rs.getString("phd_edu")));
						populationDistrictsVO.setSpace05("");
						populationDistrictsVO.setSpace06("");
						populationDistrictsVO.setSpace07("");
						populationDistrictsVO.setSpace08("");
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
			return populationDistrictsVO;
		}
		
		public PopulationDistrictsVO selectDistrictDataAge(PopulationDistrictsVO requestVO,PopulationDistrictsVO populationDistrictsAgeVO) {
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_population_village);
				pstmt.setString(1, requestVO.getRequest_county_id());
				pstmt.setString(2, requestVO.getRequest_town_id());
				pstmt.setString(3, requestVO.getRequest_village_id());
				pstmt.setString(4, requestVO.getRequest_data_info_time());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					populationDistrictsAgeVO.setCounty_id(null2str(rs.getString("county_id")));
					populationDistrictsAgeVO.setCounty_name(null2str(rs.getString("county_name")));
					populationDistrictsAgeVO.setTown_id(null2str((requestVO.getRequest_town_id()==""?"":rs.getString("town_id"))));
					populationDistrictsAgeVO.setTown_name(null2str((requestVO.getRequest_town_id()==""?"":rs.getString("town"))));
					populationDistrictsAgeVO.setVillage_id(null2str((requestVO.getRequest_village_id()==""?"":rs.getString("V_ID"))));
					populationDistrictsAgeVO.setVillage_name(null2str((requestVO.getRequest_village_id()==""?"":rs.getString("VILLAGE"))));
					if(rs.getString("TOTAL_CNT")!=null){
						populationDistrictsAgeVO.setTOTAL_CNT(null2str(rs.getString("TOTAL_CNT")));
						populationDistrictsAgeVO.setTOTAL_M_CNT(null2str(rs.getString("TOTAL_M_CNT")));
						populationDistrictsAgeVO.setTOTAL_F_CNT(null2str(rs.getString("TOTAL_F_CNT")));
						populationDistrictsAgeVO.setSpace01("");
						populationDistrictsAgeVO.setA0A4_CNT(null2str(rs.getString("A0A4_CNT")));
						populationDistrictsAgeVO.setA0A4_M_CNT(null2str(rs.getString("A0A4_M_CNT")));
						populationDistrictsAgeVO.setA0A4_F_CNT(null2str(rs.getString("A0A4_F_CNT")));
						populationDistrictsAgeVO.setA5A9_CNT(null2str(rs.getString("A5A9_CNT")));
						populationDistrictsAgeVO.setA5A9_M_CNT(null2str(rs.getString("A5A9_M_CNT")));
						populationDistrictsAgeVO.setA5A9_F_CNT(null2str(rs.getString("A5A9_F_CNT")));
						populationDistrictsAgeVO.setA10A14_CNT(null2str(rs.getString("A10A14_CNT")));
						populationDistrictsAgeVO.setA10A14_M_CNT(null2str(rs.getString("A10A14_M_CNT")));
						populationDistrictsAgeVO.setA10A14_F_CNT(null2str(rs.getString("A10A14_F_CNT")));
						populationDistrictsAgeVO.setA15A19_CNT(null2str(rs.getString("A15A19_CNT")));
						populationDistrictsAgeVO.setA15A19_M_CNT(null2str(rs.getString("A15A19_M_CNT")));
						populationDistrictsAgeVO.setA15A19_F_CNT(null2str(rs.getString("A15A19_F_CNT")));
						populationDistrictsAgeVO.setA20A24_CNT(null2str(rs.getString("A20A24_CNT")));
						populationDistrictsAgeVO.setA20A24_M_CNT(null2str(rs.getString("A20A24_M_CNT")));
						populationDistrictsAgeVO.setA20A24_F_CNT(null2str(rs.getString("A20A24_F_CNT")));
						populationDistrictsAgeVO.setA25A29_CNT(null2str(rs.getString("A25A29_CNT")));
						populationDistrictsAgeVO.setA25A29_M_CNT(null2str(rs.getString("A25A29_M_CNT")));
						populationDistrictsAgeVO.setA25A29_F_CNT(null2str(rs.getString("A25A29_F_CNT")));
						populationDistrictsAgeVO.setA30A34_CNT(null2str(rs.getString("A30A34_CNT")));
						populationDistrictsAgeVO.setA30A34_M_CNT(null2str(rs.getString("A30A34_M_CNT")));
						populationDistrictsAgeVO.setA30A34_F_CNT(null2str(rs.getString("A30A34_F_CNT")));
						populationDistrictsAgeVO.setA35A39_CNT(null2str(rs.getString("A35A39_CNT")));
						populationDistrictsAgeVO.setA35A39_M_CNT(null2str(rs.getString("A35A39_M_CNT")));
						populationDistrictsAgeVO.setA35A39_F_CNT(null2str(rs.getString("A35A39_F_CNT")));
						populationDistrictsAgeVO.setA40A44_CNT(null2str(rs.getString("A40A44_CNT")));
						populationDistrictsAgeVO.setA40A44_M_CNT(null2str(rs.getString("A40A44_M_CNT")));
						populationDistrictsAgeVO.setA40A44_F_CNT(null2str(rs.getString("A40A44_F_CNT")));
						populationDistrictsAgeVO.setA45A49_CNT(null2str(rs.getString("A45A49_CNT")));
						populationDistrictsAgeVO.setA45A49_M_CNT(null2str(rs.getString("A45A49_M_CNT")));
						populationDistrictsAgeVO.setA45A49_F_CNT(null2str(rs.getString("A45A49_F_CNT")));
						populationDistrictsAgeVO.setA50A54_CNT(null2str(rs.getString("A50A54_CNT")));
						populationDistrictsAgeVO.setA50A54_M_CNT(null2str(rs.getString("A50A54_M_CNT")));
						populationDistrictsAgeVO.setA50A54_F_CNT(null2str(rs.getString("A50A54_F_CNT")));
						populationDistrictsAgeVO.setA55A59_CNT(null2str(rs.getString("A55A59_CNT")));
						populationDistrictsAgeVO.setA55A59_M_CNT(null2str(rs.getString("A55A59_M_CNT")));
						populationDistrictsAgeVO.setA55A59_F_CNT(null2str(rs.getString("A55A59_F_CNT")));
						populationDistrictsAgeVO.setA60A64_CNT(null2str(rs.getString("A60A64_CNT")));
						populationDistrictsAgeVO.setA60A64_M_CNT(null2str(rs.getString("A60A64_M_CNT")));
						populationDistrictsAgeVO.setA60A64_F_CNT(null2str(rs.getString("A60A64_F_CNT")));
						populationDistrictsAgeVO.setA65A69_CNT(null2str(rs.getString("A65A69_CNT")));
						populationDistrictsAgeVO.setA65A69_M_CNT(null2str(rs.getString("A65A69_M_CNT")));
						populationDistrictsAgeVO.setA65A69_F_CNT(null2str(rs.getString("A65A69_F_CNT")));
						populationDistrictsAgeVO.setA70A74_CNT(null2str(rs.getString("A70A74_CNT")));
						populationDistrictsAgeVO.setA70A74_M_CNT(null2str(rs.getString("A70A74_M_CNT")));
						populationDistrictsAgeVO.setA70A74_F_CNT(null2str(rs.getString("A70A74_F_CNT")));
						populationDistrictsAgeVO.setA75A79_CNT(null2str(rs.getString("A75A79_CNT")));
						populationDistrictsAgeVO.setA75A79_M_CNT(null2str(rs.getString("A75A79_M_CNT")));
						populationDistrictsAgeVO.setA75A79_F_CNT(null2str(rs.getString("A75A79_F_CNT")));
						populationDistrictsAgeVO.setA80A84_CNT(null2str(rs.getString("A80A84_CNT")));
						populationDistrictsAgeVO.setA80A84_M_CNT(null2str(rs.getString("A80A84_M_CNT")));
						populationDistrictsAgeVO.setA80A84_F_CNT(null2str(rs.getString("A80A84_F_CNT")));
						populationDistrictsAgeVO.setA85A89_CNT(null2str(rs.getString("A85A89_CNT")));
						populationDistrictsAgeVO.setA85A89_M_CNT(null2str(rs.getString("A85A89_M_CNT")));
						populationDistrictsAgeVO.setA85A89_F_CNT(null2str(rs.getString("A85A89_F_CNT")));
						populationDistrictsAgeVO.setA90A94_CNT(null2str(rs.getString("A90A94_CNT")));
						populationDistrictsAgeVO.setA90A94_M_CNT(null2str(rs.getString("A90A94_M_CNT")));
						populationDistrictsAgeVO.setA90A94_F_CNT(null2str(rs.getString("A90A94_F_CNT")));
						populationDistrictsAgeVO.setA95A99_CNT(null2str(rs.getString("A95A99_CNT")));
						populationDistrictsAgeVO.setA95A99_M_CNT(null2str(rs.getString("A95A99_M_CNT")));
						populationDistrictsAgeVO.setA95A99_F_CNT(null2str(rs.getString("A95A99_F_CNT")));
						populationDistrictsAgeVO.setA100UP_5_CNT(null2str(rs.getString("A100UP_5_CNT")));
						populationDistrictsAgeVO.setA100UP_5_M_CNT(null2str(rs.getString("A100UP_5_M_CNT")));
						populationDistrictsAgeVO.setA100UP_5_F_CNT(null2str(rs.getString("A100UP_5_F_CNT")));
						populationDistrictsAgeVO.setSpace03("");
						populationDistrictsAgeVO.setSpace04("");
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
			return populationDistrictsAgeVO;
		}
	}
	
	public String null2str(Object object) {
		return object == null ? "" : object.toString().trim();
	}
}
