package tw.com.sbi.common.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

@WebServlet("/viewStoredProcedures")

public class UnderTable extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TestPlace.class);
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		try{
//			String sp ="";
			
			if(request.getParameter("sql")==null){
				return;
			}
			
			Class.forName("com.mysql.jdbc.Driver");
			final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
					+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
			final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
			final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
			Connection con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
			PreparedStatement pstmt = con.prepareStatement("SELECT name, param_list, body_utf8 FROM mysql.proc WHERE db = 'cdri' AND name = '"+request.getParameter("sql")+"' ORDER BY name");
			ResultSet rs = pstmt.executeQuery();
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			
			while (rs.next()) {
				Map<String,String> obj = new LinkedHashMap<String,String>();
				obj.put("name",rs.getString("name"));
				obj.put("param_list",rs.getString("param_list"));
				obj.put("body_utf8",new String(rs.getBytes("body_utf8"),"utf-8").replaceAll("\r\n","\n"));
				list.add(obj);
			}
			
//			List<Map<String, String>> outcome = mysqlconnection //name, param_list, body_utf8
//					.useMysqlDB("SELECT * FROM mysql.proc WHERE db = 'cdri' AND name = 'sp_select_company_by_type'",null);
//			logger.debug(new Gson().toJson(outcome));
//			response.getWriter().write(outcome.get(0).get("body")+"<br><br>");
//			response.getWriter().write(new Gson().toJson(outcome));
			String ip = ViewStatus.getGlobalIP();
			
			
			for(int i=0;i<list.size();i++){
				response.getWriter().write(
						//"\""+list.get(i).get("name")+"\",\""+list.get(i).get("param_list")+"\",\"<br><br><br>"
						//+list.get(i).get("body_utf8").replaceAll("\n","<br>").replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;")
						//+"<br><br><br>\"\n"
						
						"DROP PROCEDURE IF EXISTS "+list.get(i).get("name")+";<br><br>"
						+"DELIMITER $$ <br>"
						+"CREATE DEFINER=`root`@`localhost` PROCEDURE `"+list.get(i).get("name")+"`("+list.get(i).get("param_list")+")<br>"
						+list.get(i).get("body_utf8").replaceAll("\n","<br>").replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;")+" $$ <br>"
						+"DELIMITER ; <br>"
						+"<br>"
					);
			}
			logger.debug(new Gson().toJson(list));
//			response.getWriter().write(new Gson().toJson(list));
			
		}catch(Exception e){
			logger.debug("full-range-log",e);
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();
			logger.debug("Have Encountered Error: "+e.toString());
			response.getWriter().write("Have Encountered Error: "+e.toString()+"<br>");
			response.getWriter().write("<font style='color:red;'>"
						+sw.toString().replace("\r\n", "<br>")
									.replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
									.replace("(", "(<a href='#' style='color:blue;'>")
									.replace(")", "</a>)")
						+"</font>");
		}
	}
	}