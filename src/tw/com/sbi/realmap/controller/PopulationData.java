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
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class PopulationData extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(PopulationData.class);
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		if("selectall_Statistics_Gender".equals(action)){
			GetData getData= new GetData();
			String tmp = getData.selectall_Statistics_Gender();
			response.getWriter().write(tmp);
		}
		if("selectall_Statistics_Countryage".equals(action)){
			GetData getData= new GetData();
			String tmp = getData.selectall_Statistics_Countryage();
			response.getWriter().write(tmp);
		}
		if("selectall_Statistics_CountryLaborForce".equals(action)){
			GetData getData= new GetData();
			String tmp = getData.selectall_Statistics_CountryLaborForce();
			response.getWriter().write(tmp);
		}
	}
	public class GetData{
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		String query = "SELECT sn, NAME, geom FROM tb_SHP_ChinaProvincial ";
		String tb_SHP_city_query = "SELECT * FROM tb_SHP_City where city = ? ";
		public String selectall_Statistics_Gender() {
			List<Data> datalist= new ArrayList<Data>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String jsonList = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("SELECT * FROM tb_Statistics_Gender INNER JOIN tb_Statistics_Country ON tb_Statistics_Gender.Country = tb_Statistics_Country.Name");
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Data data =new Data();
					data.country_name=rs.getString("Name");
					data.lat=rs.getString("latitude");
					data.lng=rs.getString("longitude");
					data.data1=rs.getString("Male");
					data.data2=rs.getString("Female");
					datalist.add(data);
				}
				Gson gson = new Gson();
				jsonList = gson.toJson(datalist);
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
		public String selectall_Statistics_Countryage() {
			List<Data> datalist= new ArrayList<Data>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String jsonList = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("SELECT * FROM tb_Statistics_Countryage INNER JOIN tb_Statistics_Country ON tb_Statistics_Countryage.Country = tb_Statistics_Country.Name");
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Data data =new Data();
					data.country_name=rs.getString("Name");
					data.lat=rs.getString("latitude");
					data.lng=rs.getString("longitude");
					data.data1=rs.getString("UnderFourteen");
					data.data2=rs.getString("FifteenSixtyfour");
					data.data3=rs.getString("OverSixtyfive");
					datalist.add(data);
				}
				Gson gson = new Gson();
				jsonList = gson.toJson(datalist);
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
		public String selectall_Statistics_CountryLaborForce() {
			List<Data> datalist= new ArrayList<Data>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String jsonList = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement("SELECT * FROM tb_Statistics_CountryLaborForce INNER JOIN tb_Statistics_Country ON tb_Statistics_CountryLaborForce.Country = tb_Statistics_Country.Name");
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Data data =new Data();
					data.country_name=rs.getString("Name");
					data.lat=rs.getString("latitude");
					data.lng=rs.getString("longitude");
					data.data1=rs.getString("Male");
					data.data2=rs.getString("Female");
					datalist.add(data);
				}
				Gson gson = new Gson();
				jsonList = gson.toJson(datalist);
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
		class Data {
			String country_name;
			String lat;
			String lng;
			String data1;
			String data2;
			String data3;
		}
	}
}