package tw.com.sbi.realmap.controller;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.google.gson.Gson;

//import tw.com.sbi.scenariojob.controller.ScenarioJob;
import tw.com.sbi.common.controller.CommonMethod; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class CountryData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CountryData.class);
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
//		response.setContentType("text/text");
		response.setContentType("text/html;charset=UTF-8");
		String action = request.getParameter("action");
		//sp_select_countrystatistic_by_SecondTarget
//		if("draw_shpLegend".equals(action)){
//			String type=request.getParameter("type");//"GDP";
//			String year=request.getParameter("year");
//			GetData getData= new GetData();
//			String tmp = getData.buildup1(type,year)+"|"+getData.buildup2(type)+"|"+getData.buildup3(type);
//			response.getWriter().write(tmp);
//			logger.debug("retOfCountryData: "+type+" "+year+" "+tmp);
//		}
//		if("change_select".equals(action)){
//			String type = request.getParameter("type");
//			String year = request.getParameter("year");
//			GetData getData= new GetData();
//			String tmp = getData.change_select(year,type);
//			response.getWriter().write(tmp);
//			logger.debug(tmp.length()>1000?tmp.substring(0, 1000)+"... ":tmp);
//		}
		if("bigmac_select".equals(action)){
			GetData getData= new GetData();
			String tmp = getData.select_bigmac();
			response.getWriter().write(tmp);
		}
		
		if("get_country_data_disyear".equals(action)){
			String type = CommonMethod.null2str(request.getParameter("type"));
			logger.debug("type: " + type);
			String yearUnitStr = new GetDataNew().get_country_data_disyear_unit(type);
			logger.debug("ret: " + yearUnitStr);
			response.getWriter().write(yearUnitStr);
			
		}else if("get_country_data".equals(action)){
			JSONArray requestVO = new JSONArray();
			String type = CommonMethod.null2str(request.getParameter("type"));
			String year = CommonMethod.null2str(request.getParameter("year"));
			
			requestVO.put(type);
			requestVO.put(year);
			List<Map<String,String>> outcome = new CommonMethod(getServletConfig())
					.useMysqlDB("call sp_select_countrystatistic_by_SecondTarget(?,?)",requestVO);
			String outcomeStr = CommonMethod.JSONStringify(outcome);
			List<String> strlist= new ArrayList<String>();
			DecimalFormat df=new DecimalFormat("#.##");
			for(int i = 0; i < outcome.size(); i++)
			{
				strlist.add(df.format(Float.parseFloat(outcome.get(i).get("Data"))));
			}
			logger.debug(new GetDataNew().method_toGet_Foursep(strlist)+"|"+outcomeStr);
			response.getWriter().write(new GetDataNew().method_toGet_Foursep(strlist)+"|"+outcomeStr);
//			logger.debug(+"|"+unit+"|"+String.join(",",yearList));
//			response.getWriter().write(method_toGet_FoursepMaxMin(strlist)+"|"+unit+"|"+String.join(",",yearList));
			
		}else if("get_country_one_data_byTarget".equals(action)){
			//給 Age Gender Unban BigMac用
			JSONArray requestVO = new JSONArray();
			String type = CommonMethod.null2str(request.getParameter("type"));
			requestVO.put(type);
			String outcome =new CommonMethod(getServletConfig())
					.useMysqlDBString("call sp_select_countrystatistic_by_Target_newest(?)",requestVO);
			response.getWriter().write(outcome);
		}
		
		
	}
	
	
	public class GetDataNew{
		public String get_country_data_disyear_unit(String type){
			JSONArray requestVO = new JSONArray();
//			String type = CommonMethod.null2str(request.getParameter("type"));
			
			requestVO.put(type);
			List<Map<String,String>> years = new CommonMethod(getServletConfig())
					.useMysqlDB("call sp_select_countrystatistic_disyear_by_SecondTarget(?)",requestVO);
			String[] yearList = new String[years.size()];
			String unit = "";
			for(int i = 0; i < years.size(); i++)
			{
				yearList[i] = years.get(i).get("year")==null ? years.get(i).get("Type") : years.get(i).get("year") ;
				unit = years.get(i).get("unit");
			}
			
			return String.join(",",yearList)+"|"+unit;
		}
		public String method_toGet_Foursep(List<String> strlist){
			float min=1000000000,max=-1000000000;
			for(int i = 0; i < strlist.size(); i++)
			{
				float data = Float.parseFloat(strlist.get(i));
				if(data>max)max=data;
				if(data<min)min=data;
			}
			if(strlist.isEmpty()){ return "";}
			String tag="";
			for(int i=1;i<5;i++){
				String tmp;
				if(max>10000){
					tmp =  "" + Math.round(( (max-min) * 0.2 * i + min)/100) * 100 ;
				}else{
					tmp =  "" + Math.round(( (max-min) * 0.2 * i + min) *10) * 0.1 ;
				}
				int tmp_comma=tmp.indexOf(".");
				if(tmp_comma!=-1 && (tmp_comma+2)<=tmp.length()){
					tag+=tmp.substring(0, tmp_comma+2);
				}else{
					tag+=tmp;
				}
				if(i!=5)tag+=",";
			}
			GetData getdata = new GetData();
			if(strlist.size()>10){
//				return String.join(",", getdata.Quantile_GetBreaks(strlist,5))+"|"+min+"|"+max;
				return String.join(",", getdata.Quantile_GetBreaks(strlist,5));
			}else{
//				return tag+"|"+min+"|"+max;
				return tag;
			}
		}
	}
	public class GetData{
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		String buildquery1_1 = "SELECT data FROM tb_STAT_Trget_Country WHERE Second_Trget= ? AND Type=( SELECT  Type FROM tb_STAT_Trget_Country WHERE Second_Trget = ? ORDER BY Type DESC LIMIT 1)";
		String buildquery1_2 = "SELECT data FROM tb_STAT_Trget_Country WHERE Second_Trget= ? AND Type= ? ";
		String buildquery2 = "SELECT DISTINCT unit FROM tb_STAT_Country WHERE Second_Trget = ? ";
		String buildquery3 = "SELECT DISTINCT Type year FROM tb_STAT_Trget_Country WHERE Second_Trget= ? ORDER BY TYPE DESC";
		String changequery = "SELECT * FROM tb_STAT_Trget_Country CE, tb_SHP_Country WHERE CE.Country = tb_SHP_Country.CNTRY_NAME AND CE.Type = ? AND CE.Second_Trget = ? ";
		String bigmacquery = "SELECT * FROM tb_Statistics_BigMac INNER JOIN tb_SHP_Country ON tb_Statistics_BigMac.Country = tb_SHP_Country.CNTRY_NAME ";
		
		public String buildup1(String type, String year) {//分割的四個數字和 min max
			List<String> strlist= new ArrayList<String>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				if(year==null){
					pstmt = con.prepareStatement(buildquery1_1);
					pstmt.setString(1, type);
					pstmt.setString(2, type);
				}else{
					pstmt = con.prepareStatement(buildquery1_2);
					pstmt.setString(1, type);
					pstmt.setString(2, year);
				}
				
				rs = pstmt.executeQuery();
				int min=1000000000,max=-1000000000;
				while (rs.next()) {
					int tmp=Math.round(Float.parseFloat(rs.getString("data")));
					if(tmp>max)max=tmp;
					if(tmp<min)min=tmp;
					strlist.add(rs.getString("data"));
				}
				if(strlist.isEmpty()){ return "";}
				String tag="";
				for(int i=1;i<5;i++){
					String tmp;
					if(max>10000){
						tmp =  "" + Math.round(( (max-min) * 0.2 * i + min)/100) * 100 ;
					}else{
						tmp =  "" + Math.round(( (max-min) * 0.2 * i + min) *10) * 0.1 ;
					}
					int tmp_comma=tmp.indexOf(".");
					if(tmp_comma!=-1 && (tmp_comma+2)<=tmp.length()){
						tag+=tmp.substring(0, tmp_comma+2);
					}else{
						tag+=tmp;
					}
					if(i!=5)tag+=",";
				}
				if(strlist.size()>10){
					return String.join(",", Quantile_GetBreaks(strlist,5))+"|"+min+"|"+max;
				}else{
					return tag+"|"+min+"|"+max;
				}
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
		}
		public String buildup2(String type) {//unit
			List<String> strlist= new ArrayList<String>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(buildquery2);
				pstmt.setString(1, type);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					strlist.add(rs.getString("unit"));
				}
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
			return String.join(",", strlist);
		}
		public String buildup3(String type) {//年份列表
			List<String> strlist= new ArrayList<String>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(buildquery3);
				pstmt.setString(1, type);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					strlist.add(rs.getString("year"));
				}
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
			return String.join(",", strlist);
		}
		public String change_select(String year, String type) {
			List<Country> countrylist= new ArrayList<Country>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String jsonList = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(changequery);
				pstmt.setString(1, year);
				pstmt.setString(2, type);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Country country =new Country();
					country.country_name=rs.getString("country");
					country.economy_detail_statistic=rs.getString("data");
					country.geom=rs.getString("geom");
					countrylist.add(country);
				}
				Gson gson = new Gson();
				jsonList = gson.toJson(countrylist);
				
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
			return jsonList;
		}
		public String[] Quantile_GetBreaks(List<String> list, int count) {//小功能
			int i;
			if(list.size()==0){return new String[]{"0,0,0,0"};}
	        Collections.sort(list,
	        new Comparator<String>() {
	            public int compare(String o1, String o2) {
	            	float o1_f =Float.parseFloat(o1);
	            	float o2_f =Float.parseFloat(o2);
	            	if (o1_f < o2_f) return -1;
	                if (o1_f == o2_f) return 0; // Fails on NaN however, not sure what you want
	                if (o1_f > o2_f) return 1;
	            	return 1;
	            }
	        });
	        if(count==0 || list.size() == 0)return null;
	        String[] result = new String[count-1];
	        int jumprate = list.size()/count;
	        for ( i = 1; i < count; i++)
            {
	        	if(Float.parseFloat(list.get(jumprate*i))>10000){
	        		result[i-1]= "" + Math.round(Float.parseFloat(list.get(jumprate*i))/100) * 100 ;
	        	}else{
	        		result[i-1]=list.get(jumprate*i);
	        	}
            }
	        return result;
		}
		public String select_bigmac() {
			List<BigMac> list= new ArrayList<BigMac>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(bigmacquery);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					BigMac bigmac =new BigMac();
					bigmac.country_name=rs.getString("Country");
					bigmac.price=rs.getString("Price");
					bigmac.rawIndex=rs.getString("RawIndex");
					bigmac.actualExchangeRate=rs.getString("ActualExchangeRate");
					bigmac.impliedExchangeRate=rs.getString("ImpliedExchangeRate");
					bigmac.geom=rs.getString("geom");
					
					list.add(bigmac);
				}
				Gson gson = new Gson();
				String jsonList = gson.toJson(list);
				return jsonList;
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				if (rs != null) {try {rs.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (pstmt != null) {try {pstmt.close();} catch (SQLException se) {se.printStackTrace(System.err);logger.debug("full-range-log",se);}}
				if (con != null) {try {con.close();} catch (Exception e) {e.printStackTrace(System.err);logger.debug("full-range-log",e);}}
			}
		}
		
		class Country {
			String country_name;
			String economy_detail_statistic;
			String geom;
		}
		class BigMac{
			String country_name;
			String price;
			String rawIndex;
			String actualExchangeRate;
			String impliedExchangeRate;
			String geom;
		}
	}
}

