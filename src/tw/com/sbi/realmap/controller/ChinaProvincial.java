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
public class ChinaProvincial extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(ChinaProvincial.class);
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		if("selectall_SHP_City".equals(action)){
			String city = request.getParameter("city");
			GetData getData= new GetData();
			String tmp = getData.selectall_SHP_City(city);
			response.getWriter().write(tmp);
		}
		if("selectall_SHP_ChinaProvincial".equals(action)){
			GetData getData= new GetData();
			String tmp = getData.selectall_SHP_ChinaProvincial();
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
		public String selectall_SHP_City(String city) {
			List<Country> countrylist= new ArrayList<Country>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String jsonList = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(tb_SHP_city_query);
				pstmt.setString(1, city);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Country country =new Country();
					country.country_name=rs.getString("city");
					country.cX=rs.getString("cX");
					country.cY=rs.getString("cY");
					country.geom=rs.getString("geom");
					country.living=rs.getString("living");
					country.household=rs.getString("household");
					country.male=rs.getString("male");
					country.female=rs.getString("female");
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
		public String selectall_SHP_ChinaProvincial() {
			List<Country> countrylist= new ArrayList<Country>();
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String jsonList = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Country country =new Country();
					country.country_name=rs.getString("NAME");
					country.data=rs.getString("sn");
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
		class Country {
			String country_name;
			String data;
			String cX;
			String cY;
			String geom;
			String living;
			String household;
			String male;
			String female;
		}
	}
}