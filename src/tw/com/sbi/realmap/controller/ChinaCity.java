package tw.com.sbi.realmap.controller;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class ChinaCity extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(ChinaCity.class);
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		if("draw_shpLegend".equals(action)){
			String type=request.getParameter("type");//"GDP";
			GetData getData= new GetData();
			String tmp = getData.buildup1(type)+"|"+getData.buildup2(type);//+"|"+getData.buildup3(type);
			response.getWriter().write(tmp);
		}
		if("change_select".equals(action)){
			String type = request.getParameter("type");
			GetData getData= new GetData();
			String tmp = getData.change_select(type);
			response.getWriter().write(tmp);
		}
	}
	public class GetData{
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		String buildquery1 = "SELECT ? data FROM tb_SHP_ChinaCity";
		String buildquery2 = "SELECT DISTINCT unit FROM tb_STAT_City WHERE English = ? ";
		String changequery = "SELECT City, ? data, geom FROM tb_SHP_ChinaCity ";
		public String buildup1(String type) {//分割的四個數字和 min max
			List<String> strlist= new ArrayList<String>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				String tmpquery = "SELECT "+ type +" data FROM tb_SHP_ChinaCity";
				pstmt = con.prepareStatement(tmpquery);
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
		public String change_select(String type) {
			List<Country> countrylist= new ArrayList<Country>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String jsonList = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				String tmpquery="SELECT City, "+type+" data, geom FROM tb_SHP_ChinaCity ";
				pstmt = con.prepareStatement(tmpquery);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Country country =new Country();
					country.country_name=rs.getString("City");
					country.data=rs.getString("data");
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
		class Country {
			String country_name;
			String data;
			String geom;
		}
	}
}