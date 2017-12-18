package tw.com.sbi.common.controller;

import java.io.File;
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Array;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//import java.nio.channels.ReadableByteChannel;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//import java.util.Map;
import java.util.Random;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

import com.google.gson.Gson;

import tw.com.sbi.common.MySQLAccessModel;
import tw.com.sbi.developer.ClassSpy;
import tw.com.sbi.trackingEmbed.controller.HttpServletResponseEmbed;
import tw.com.sbi.vo.MenuVO;

//import com.google.gson.Gson;
//import com.sun.beans.decoder.ValueObject;

//import tw.com.sbi.common.ClassSpy;
//import tw.com.sbi.common.MapObject;
//import tw.com.sbi.common.MySQLAccess;
//import tw.com.sbi.vo.CityVO;
//import tw.com.sbi.vo.DAOBenVersion;
//import tw.com.sbi.vo.MenuVO;
//import tw.com.sbi.vo.RegisterEpaperVO;
//import tw.com.sbi.vo.ResponseVO;
//import tw.com.sbi.vo.ResponseVO_v1;
//import tw.com.sbi.vo.ScenarioResultVO;

//import com.sun.script.javascript.RhinoScriptEngine;

//import tw.com.sbi.dataupdate.controller.GetLatLngFromAddress;

//import tw.com.sbi.dataupdate.controller.DownloadFiles;
//import tw.com.sbi.dataupdate.controller.POIiconCopy;
//import tw.com.sbi.dataupdate.controller.UpdateFreewayFlow;
//import tw.com.sbi.dataupdate.controller.UpdateMetroFlow;

import org.apache.commons.io.FileUtils;

@WebServlet("/testForFuncs")

public class TestPlace extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TestPlace.class);
	
	public static void displayDirectoryContents(File dir) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.println("directory:" + file.getCanonicalPath());
					displayDirectoryContents(file);
				} else {
					System.out.println("     file:" + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("full-range-log",e);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		MyResponseWrapper 
		response = new HttpServletResponseEmbed((HttpServletResponse) response); 
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		try{
			
			CommonMethod.requestParameter(request);
			CommonMethod.requestHeader(request);
			
			response.getWriter().write("Have Visit Test Func---<br>");
			logger.debug("Have Visit Test Func---");
//#########################################################################################
//#########################################################################################
			CommonMethod mysqlconnection = new CommonMethod(getServletConfig());
			mysqlconnection.restrictSQL = 0 ;
			List<Map<String, String>> outcome = mysqlconnection
					.useMysqlDB("SELECT name, param_list, body_utf8 FROM mysql.proc WHERE db = 'cdri' AND name = 'sp_select_company_by_type'",null);
			
			logger.debug(new Gson().toJson(outcome));
			
			
			
			String sysString = StartupSetting.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String projectPath = sysString.split(sysString.split("/")[sysString.split("/").length - 1])[0];
			System.out.println(projectPath);
			Connection con = null;
			try{
				Context initContext = new InitialContext();
				Context envContext = (Context) initContext.lookup("java:/comp/env");
				DataSource ds = (DataSource) envContext.lookup("jdbc/MySQL");
				con = ds.getConnection();
				CallableStatement stmt = con.prepareCall("call sp_STAT_City()");
				ResultSet rssMysqlDataset = stmt.executeQuery();
				ResultSetMetaData rsmMetaData = rssMysqlDataset.getMetaData();
		        int intColumnCount = rsmMetaData.getColumnCount();
		        
		        while (rssMysqlDataset.next()) {
		        	
		            Map<String,Object> mapRowData = new HashMap<String,Object>();
		            
		            for (int i = 1; i <= intColumnCount; i++) {  
		            	mapRowData.put(rsmMetaData.getColumnLabel(i), rssMysqlDataset.getObject(i));
		            }
		            System.out.print(new Gson().toJson(mapRowData)+", ");
//		            Object objRowData = this.convertRowResult(mapRowData);
//		            lbjResponseVO.add(objRowData);  
		            
		        }  
				
				if (!con.isClosed()) {
					System.out.println("與db連線ing");
				}
			} catch (SQLException sqle) {
				logger.debug("full-range-log",sqle);
				System.out.println("sqle=" + sqle);
			} finally {
				con.close();
				con = null;
			}
			
//#########################################################################################
//#########################################################################################
			//測試段"temporary"
//			
//			try{
//				throw new Exception("hello");
//			}catch(Exception e){
//				logger.debug(e);
//				logger.debug("==========");
//				logger.debug("Some",e);
//				logger.debug("",e);
//			}
//			String sysString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath(); 
//			String projectPath = sysString.split(sysString.split("/")[sysString.split("/").length - 1])[0];
//			response.getWriter().write(sysString+"<br>");
//			response.getWriter().write(sysString.split(sysString.split("/")[sysString.split("/").length - 2])[0]+"<br>");
//			getClass().getDeclaredFields()
//			Field[] fields= getClass().getDeclaredFields();
//			logger.debug("新北市樹林區三興里一八鄰三興七巷七一之一號".replaceAll("一", "1").replaceAll("二", "2").replaceAll("三", "3").replaceAll("四", "4").replaceAll("五", "5")
//					.replaceAll("六", "6").replaceAll("七", "7").replaceAll("八", "8").replaceAll("九", "9")
//					.replaceAll("十","1").replaceAll("廿","2")
//					.replaceAll("\\?", "").replaceAll("○","零").replaceAll("零","0").replaceAll("..里","").replaceAll("\\d+鄰",""));
//			displayDirectoryContents(new File(sysString.split(sysString.split("/")[sysString.split("/").length - 2])[0]));
//			TestDao dao = new TestDao(getServletConfig());
//			
//			if(dao.doAction("test")){
//				logger.debug(
//					new Gson().toJson(dao.getResponseList())
//				);
//			}
			
//			logger.debug(new DataAccessObject(getServletConfig()).parseInt(null));
//			logger.debug(new DataAccessObject(getServletConfig()).parseInt("123"));
//			logger.debug(new DataAccessObject(getServletConfig()).parseInt("ABC"));
//			
//			logger.debug(new DataAccessObject(getServletConfig()).parseString(null));
//			logger.debug(new DataAccessObject(getServletConfig()).parseString("123"));
//			logger.debug(new DataAccessObject(getServletConfig()).parseString("ABC"));
//			logger.debug("\n");
//			ClassSpy.main("tw.com.sbi.realmap.controller.POI","ALL");
			
			
//			response.getWriter().write( new String(Base64.encodeBase64String("今天天氣不錯".getBytes()))+"<br>");
//			response.getWriter().write( new String(Base64.encodeBase64String("今天天氣不錯".getBytes("US-ASCII")))+"<br>");
//			response.getWriter().write( new String(Base64.encodeBase64String("今天天氣不錯".getBytes("Big5")))+"<br>");
//			response.getWriter().write( new String(Base64.encodeBase64String("今天天氣不錯".getBytes("UTF-8")))+"<br>");

//			logger.debug( new String(Base64.encodeBase64String("今天天氣不錯".getBytes())));
//			logger.debug( new String(Base64.encodeBase64String("今天天氣不錯".getBytes("US-ASCII"))));
//			logger.debug( new String(Base64.encodeBase64String("今天天氣不錯".getBytes("Big5"))));
//			logger.debug( new String(Base64.encodeBase64String("今天天氣不錯".getBytes("UTF-8"))));
			
//			String a = "TPS(第三人称射击）";
//			String b = "TPS(第三人称射击)";
//			logger.debug(a.equals(b));
			
			
//			Object inst = ResponseVO_v1.class.newInstance();
//			inst = DAOBenVersion.class.newInstance();
//			inst = ScenarioResultVO.class.newInstance();
//			inst = ClassSpy.class.newInstance();
////			inst = MySQLAccess.class.newInstance();
////			inst = MapObject.class.newInstance();
//			inst = ResponseVO.class.newInstance();
//			
//			Field[] fields= inst.getClass().getDeclaredFields();
//			for(int i=0;i<fields.length;i++){
//				logger.debug(fields[i].getName());
////				Field field = clz.getDeclaredField(fields[i].getName());
//			}
//			Class clz = RegisterEpaperVO.class;
//			clz.getDeclaredField("ccc");
			
//			Class[] cArg = new Class[3]; //Our constructor has 3 arguments
//			cArg[0] = Long.class; //First argument is of *object* type Long
//			cArg[1] = String.class; //Second argument is of *object* type String
//			cArg[2] = int.class; //Third argument is of *primitive* type int
//			Constructor[] ctrs = clz.getDeclaredConstructors();
//			logger.debug(ctrs.length);
//			for(int i=0;i<ctrs.length;i++){
//				logger.debug(ctrs[i].get);
//			}
//			Constructor intArgsConstructor  = clz.getConstructor(new Class[] {int.class});
//			Object[]    intArgs             = new Object[] { new Integer(12) };
//			Object      object              = intArgsConstructor.newInstance(intArgs);
			
			
//			try {
//			MenuVO student1 = MenuVO.class.newInstance();
//			} catch (InstantiationException ex) {

//			} catch (IllegalAccessException ex) {

//			}
//			Field[] fields= clz.getDeclaredFields();
//			for(int i=0;i<fields.length;i++){
////				Field field = clz.getDeclaredField(fields[i].getName());
//				Field field = clz.getDeclaredField("nick_name2");
//				field.setAccessible(true);
//				field.set(inst,"123123");
////				field.set(obj, value);
////				logger.debug(fields[i].getName());
//			}
//			logger.debug(CommonMethod.JSONStringify(inst));
//			logger.debug(clz.getFields());
//			ReadableByteChannel a ;
//			for(Field field : clz.getDeclaredFields()){
//				  Class type = field.getType();
//				  String name = field.getName();
//				  Annotation[] annotations = field.getDeclaredAnnotations();
//				  annotations.toString()
//				}
//			logger.debug(ValueObject.class.getAnnotatedInterfaces());
//			logger.debug(String.format("%160s", "sp_insert_Epaper_Order(?,?,?,?)"));
//			logger.debug(String.format("%80s", "sp_insert_Epaper_Order(?,?,?,?)"));
//			logger.debug(String.format("%.80s", "sp_insert_Epaper_Order(?,?,?,?)"));
//			logger.debug(String.format("%d", "sp_insert_Epaper_Order(?,?,?,?)"));
//			Gson gson = new Gson();
//			temp[] voEarr =  gson.fromJson("[{\"aaa\":\"aaa\",\"bbb\":\"\"}]",null);
//			logger.debug("call sp_insert_Epaper_Order(?,?,?,?)".indexOf("call sp_"));
//			logger.debug(voEarr==null);
//			int i=0,j=0;
//			int k=i/j;
			
//			logger.debug(CommonMethod.null2str(request.getParameter("action")));
//			List<String> list = new ArrayList<String>();
//			list.add("123");
//			list.add("456");
//			list.add("789");
//			init2(list);
//			Class<?> arrayClass = String[].class;
//			System.out.println(arrayClass);
//			Class<?> namedClass = Class.forName("[L" + String.class.getName() + ";");
//			System.out.println(namedClass);
//			System.out.println(arrayClass == namedClass);
//			Class<?> clazz = String.class;
//			Class<?> arrayClass2 = Array.newInstance(clazz,0).getClass();
//			System.out.println(arrayClass2);
			
//			"<html><head>"
//			+ "<script src=\"http://www.a-ber.com.tw/sbi/js/jquery-1.12.4.min.js\"></script>"
//			+ "<style>body {background-image: url(images/SBI-WaterMarker-loading.png);"
//			+ "background-repeat: no-repeat;"
//		+ "background-attachment: fixed;"
//			+ "background-position: center;"
//			+ "background-size: contain;"
//			+ "}</style></head><body>"
//			+ "<script>"	
//			+ "$(function() {$.ajax({type : 'POST',url : 'https://api.twilio.com/2010-04-01/Accounts/AC4df5ca98148c3665be7914bc79583775/Messages.json',dataType : 'json',async : false,headers : {'Authorization' : 'Basic '+ btoa('AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc')},data : {'To' : '+886919863010','From' : '+13346001946','Body' : 'SBI Error Please Check EMail'},success : function(result) {window.location.href = 'login.jsp';;}});});"
//			+ "</script>"
//			+ "</body></html>");
			
			
//			Map<String,Object> params = new LinkedHashMap<>();
//	        params.put("To", "+886919863010");
//	        params.put("From", "+13346001946");
//	        params.put("Body", "SomethingWrong");
//
//	        StringBuilder postData = new StringBuilder();
//	        for (Map.Entry<String,Object> param : params.entrySet()) {
//	            if (postData.length() != 0) postData.append('&');
//	            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//	            postData.append('=');
//	            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//	        }
//	        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
//			logger.debug(postData.toString());
//			URL url = new URL("https://api.twilio.com/2010-04-01/Accounts/AC4df5ca98148c3665be7914bc79583775/Messages.json");
//		    URLConnection conn = url.openConnection();
//		    conn.setDoOutput(true);
//		    conn.setRequestProperty ("Authorization", "Basic QUM0ZGY1Y2E5ODE0OGMzNjY1YmU3OTE0YmM3OTU4Mzc3NToxYjcxNTRiNDFiMTZiYTA1NjEyNGIwNzJhYTAyOTFiYw==");
//
//		    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
//		    String data = "To=+886919863010&From=+13346001946&Body=javaSomethingWrongPleaseCheckMail";
//		    writer.write(data);
//		    writer.flush();
//		    String line;
//		    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		    while ((line = reader.readLine()) != null) {
//		      System.out.println(line);
//		    }
//		    writer.close();
//		    reader.close();
//			
//			try{
//			String urlParameters  = "To=+886919863010&From=+13346001946&Body=5234";
////			String urlParameters  = "data={'TO':'+886919863010','From':'+13346001946','Body':'javaSomethingWrongPleaseCheckMail'}";
//			byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
//			int    postDataLength = postData.length;
//			String requestA        = "https://api.twilio.com/2010-04-01/Accounts/AC4df5ca98148c3665be7914bc79583775/Messages.json";
//			URL    url            = new URL( requestA );
//			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
//			conn.setDoOutput( true );
//			conn.setInstanceFollowRedirects( false );
//			conn.setRequestMethod( "POST" );
//			conn.setRequestProperty("Host","api.twilio.com");
//			conn.setRequestProperty("User-Agent","curl/7.50.1");
//			conn.setRequestProperty("Accept","BBB");
//			conn.setRequestProperty("Authorization","Basic QUM0ZGY1Y2E5ODE0OGMzNjY1YmU3OTE0YmM3OTU4Mzc3NToxYjcxNTRiNDFiMTZiYTA1NjEyNGIwNzJhYTAyOTFiYw==");
//			conn.setRequestProperty("AAA","BBB");
//			conn.setRequestProperty("AAA","BBB");
//			conn.setRequestProperty( "Authorization", "Basic QUM0ZGY1Y2E5ODE0OGMzNjY1YmU3OTE0YmM3OTU4Mzc3NToxYjcxNTRiNDFiMTZiYTA1NjEyNGIwNzJhYTAyOTFiYw==");
//			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
//			conn.setRequestProperty( "charset", "utf-8");
//			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
//			conn.setUseCaches( false );
//			DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
//			wr.write( postData );
//			logger.debug(conn.getResponseCode());
//			}catch(Exception e){
//				e.printStackTrace();
//			}
			
//			RecordMsg.sendPhoneMsg("", "", "0919863010");
//			Map<String, String> contextParamMap = new HashMap<String, String>();
//			ServletContext sc = getServletConfig().getServletContext();
//			Enumeration<String> parameters = sc.getInitParameterNames();
//			while (parameters.hasMoreElements()) {
//				String parameter = (String) parameters.nextElement();
//				contextParamMap.put(parameter, sc.getInitParameter(parameter));
//			}
//			new StartupSetting().submitReport("DEPLOY",contextParamMap);
//			List<Map<String, String>> outcome =  new ArrayList<Map<String,String>>();//new CommonMethod(getServletConfig()).useMysqlDB("call sp_get_update_situation()",null);
//			try{
//				outcome = new CommonMethod(getServletConfig()).useMysqlDB("call sp_get_data_resume('');",null);
//				
//				report += "<tr><th>資料名稱</th><th>SBI中最新日期</th><th>資料用途說明</th><th>資料來源</th></tr>";
//				for(int i=0 ; i<outcome.size() ; i++){
////					report += "<tr><td colspan='4'><hr style='color: #ddd;'></td></tr>";
//					report += "<tr><td style='vertical-align: baseline;'>"+outcome.get(i).get("data_zh_name")+"</td><th style='vertical-align: baseline;'>"+outcome.get(i).get("data_time") + "</th><td style='vertical-align: baseline;max-width:240px;'>"+outcome.get(i).get("data_purpose")+"</td><td style='vertical-align: baseline;'>"+outcome.get(i).get("data_source")+"</td></tr>";
//				}
//				
//			}catch(Exception e){
//				e.printStackTrace();
//				report += "<tr><th colspan='2'>=====　讀取資料狀態失敗　=====</td></tr>";
//				logger.debug(e.toString());
//			}
//			report += "</table>";
//			response.getWriter().write(report);
//			response.getWriter().write(new StartupSetting().deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host"));
//			
//			
//			
//			String strtest = "<a class='href' href='http://www.a-ber.com.tw' target='_blank'>系統頁面</a> <a class='href' href='http://www.a-ber.com.tw' target='_blank'>系統頁面</a> <a class='href' href='http://www.a-ber.com.tw' target='_blank'>系統頁面</a> ";
//			response.getWriter().write(strtest.replaceAll("class='href'", "style='background:#03a9f4;border-radius:3px;color:#ffffff;font-family: Microsoft JhengHei;padding:6px 8px;'"));
//			FileWriter fw = new FileWriter("d:/test.txt");
//	        fw.write("test");
//	        fw.flush();
//	        fw.close();
	        
//	        logger.debug( new StartupSetting().deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host") );
//	        ProcessHandle.current().getPid();
//	        Map<String, String> contextParamMap = new HashMap<String, String>();
//			ServletContext sc = getServletConfig().getServletContext();
//			Enumeration<String> parameters = sc.getInitParameterNames();
//			while (parameters.hasMoreElements()) {
//				String parameter = (String) parameters.nextElement();
//				contextParamMap.put(parameter, sc.getInitParameter(parameter));
//			}
//			new StartupSetting().submitReport("DailyCheck", contextParamMap);
//			ScriptEngineManager engineManager = new ScriptEngineManager();  
////		    RhinoScriptEngine engine = (RhinoScriptEngine)
//		    ScriptEngine engine = engineManager.getEngineByName("javascript");  
//		    String script = "function hello(message){document.writeln(message);}";  
//		    engine.eval(script);
//		    engine.eval("hello(123);");
//			ScriptEngineManager manager = new ScriptEngineManager();
//			ScriptEngine scriptEngine = manager.getEngineByName("JavaScript");
//		    ScriptEngine bashScriptEngine = scriptEngine;
//
//		    bashScriptEngine.put("string", "aString");
//		    bashScriptEngine.put("integer", 42);
//		    bashScriptEngine.put("float", 42.0);
//
//		    Integer returnCode = (Integer) bashScriptEngine.eval("echo %string% %integer% %float%");
//
//		    assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
//		    assertEquals("aString 42 42.0\n", scriptOutput.toString());
		    
//		    engine.invokeFunction("hello", "javascript");  
//			logger.debug(request.getParameter("toLong"));
//			CommonMethod.requestHeader(request);
//			java.util.Enumeration e = request.get();
//			  for(;e.hasMoreElements();){
//			    String key = e.nextElement().toString();
//			    System.out.println(key + " : " + request.getHeader(key));
//			  }
//			logger.debug(request.getRemoteUser());
//			logger.debug(request.getRemotePort());
//			logger.debug(request.getRemoteHost());
//			logger.debug(System.getProperty("user.name"));
//			GetLatLngFromAddress.main(null);
//			String url = "https://maps.googleapis.com/maps/api/geocode/json?key="+"AIzaSyCkf_RLlFG-WXTurGgeEa-N5SH1mo0Ki3I"+"&address="+java.net.URLEncoder.encode("台北市內湖區康寧路三段189巷21弄26號","UTF-8")+"&sensor=true";
//			InputStream in = new URL(url).openStream();
//			int i;
//			String buf = "";
//			char c;
//			while((i=in.read())!=-1)
//	         {
//	            // converts integer to character
//	            c=(char)i;
//	            buf+=c;
//	            // prints character
//	            System.out.print(c);
//	         }
//			in.close();
//			JSONObject element = new JSONObject(buf);
//			JSONObject location = element.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
//			location.getString("lat");
//			location.getString("lng");
//			logger.debug(location.getString("lat")+"  "+location.getString("lng"));
//			logger.debug(buf);
//			response.getWriter().write("<br>##"+AssistMethod.getValidCode(request)+"##<br>");
//			logger.debug(AssistMethod.getValidCode(request));
			//###############
			//測試倉庫
//			
//			logger.debug("para: "+CommonMethod.null2str(request.getParameter("para")));
//			response.getWriter().write("para: "+CommonMethod.null2str(request.getParameter("para")));
//			logger.debug(Calendar.getInstance().get(Calendar.MONTH)+1);
			//###############
			logger.debug("Have Finish Test Func Success!!!");
			response.getWriter().write("<br>Have Finish Test Func Success!!!<br>");
		}catch(Exception e){
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();
//			logger.debug("##"+ sw.toString());
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
//	class TestDao extends DataAccessObject{
//		 
//		public TestDao(ServletConfig cfgServletConfig) {
//			super(cfgServletConfig);
//		}
//		public void setRequestStatement(String action) throws SQLException{
//			
//			if("test".equals(action)){
//				this.stmQueryStatement = this.conMysql.prepareCall("call sp_selectall_menu()");
//			}else{
//				this.stmQueryStatement = this.conMysql.prepareCall("call sp_selectall_user('')");
//			}
////			
////			SharedFunction.myLogDebug("exeMYSQL: " + strStoreprocedureQuery + " withPara: "
////					+ (lstRqstList == null ? "[]" : new Gson().toJson(lstRqstList)));
////			
////			for (int i = 0; lstRqstList != null && i < lstRqstList.size(); i++) {
////				MySQLAccess.setParaSwitch(stmPrepareQuery, i+1, lstRqstList.get(i));
////			}
////			return true;
//		}
//		
//		public Object convertRowResult(Map<String,Object> mapRowData){
//			MenuVO menuVO = new MenuVO();
//			menuVO.setId(""+mapRowData.get("Second_id"));
//			menuVO.setParentId(""+mapRowData.get("First_id"));
//			menuVO.setMenuName((String)mapRowData.get("First_Level"));
//			menuVO.setUrl((String)mapRowData.get("url"));
//			return menuVO;
//		}
//	}
	//	public <T> List<T> init2(Object objs) {
	//	@SuppressWarnings("unchecked")
	//	List<String> list = (List<String>) objs;
	//	for(int i=0;i<list.size();i++){
	//		logger.debug(list.get(i));
	//	}
	//	return null;
	//	
	//}
	//public class temp{
	//	private String aaa = "123";
	//	String bbb = "456";
	//	public String getAaa() {
	//		return aaa;
	//	}
	//	public void setAaa(String aaa) {
	//		this.aaa = aaa;
	//	}
	//}
	//public <E> void test(List<E> list){
	//	E a = list.get(0);
	//	logger.debug(a);
	//	for(int i=0;i<list.size();i++){
	//		logger.debug(list.get(i));
	//	}
	//}
	
	public static class AssistMethod{
		
		public static String getValidCode(HttpServletRequest request){
			return CommonMethod.null2str( request.getSession().getAttribute("checkcode"));
		}
		
		public static String generateEmail(){
			String email = "";
			int i;
			int[] A = new int[8];
			
			for (i = 0; i < 8; i++) {
				if (i < 4) {
					A[i] = (int) ((Math.random() * 26) + 97);
				} else {
					double road = Math.random();
					if(road<0.4){
						A[i] = (int) ((Math.random() * 26) + 97);
					}else if(road<0.5){
						A[i] = (int) (((Math.random() * 26) + 65));
					}else{
						A[i] = ((int) ((Math.random() * 10) + 48));
					}
				}
			}
			
			for (i = 0; i < 8; i++) {
				email += (char) A[i];
			}
			email +="@gmail.com";
			return email;
		}
		
		public static String getChineseName(){
			List<String>chineseNameList = Arrays.asList(
					"劉家妤","鍾明愛","王金鳳","狄柏毅","林典順","謝則正","吳育霖","沈仲文","陳靜盈","吳燦中","葉詩涵","杜義琬","簡智裕","林雅姍","方蕙純",
					"鄭駿茜","鄭欣潔","李靜芳","查怡婷","林子綸","賴盈甄","蔡為修","林清菱","王俊賢","竇佳燕","盧詩琦","吳必愛","劉泰淑","林雅婷","蘇敏伶",
					"林偉成","許原信","陳永隆","屈俊男","陳家豪","車俊霖","毛旭延","王婉瑜","陳偉誠","張曼蓮","蔡明昇","王彥翔","鄧妍裕","曾怡婷","楊威廷",
					"方傑睿","戴玫清","潘靖雯","陳宗凱","富孟君","蔡宜虹","鄭慧茹","蘇佐雨","何怡君","廖喬隆","李佳松","楊怡璇","李宛惠","陳慧凡","黃建銘",
					"張慧玲","許孟倫","鄭恆能","李苡良","陳奕雅","蔡雅萍","蔡宜治","林承志","洪修蕙","蔡育汝","王智妍","謝佳穎","宋政霖","陳雅雯","汪琬婷",
					"劉欣怡","錢政儒","黃淑惠","劉宜芳","汪宛儒","韓宗毅","李孟財","姜郁翔","郭禎紋","蔡冠志","賴承學","鄭伊宣","劉美育","鄧明慈","張月琦",
					"林秀美","施俊杰","馮逸南","林怡意","黃國坤","陳翊治","王玉玲","李冠雲","陳建苓","顏馨儀","余志豪","林姿妤","陳冠宏","林伯財","周佳茂",
					"劉佳穎","高志成","周以仁","戴奎韻","陳淑娟");
			int rand = new Random().nextInt(chineseNameList.size());
			
			return chineseNameList.get(rand);
		}
		
	}
}
