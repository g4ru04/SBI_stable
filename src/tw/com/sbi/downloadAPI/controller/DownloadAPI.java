package tw.com.sbi.downloadAPI.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.codec.binary.Base64;

public class DownloadAPI  extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(DownloadAPI.class);
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String action = null2Str(request.getParameter("action"));
		String pythonAPIWebservice_host = getServletConfig().getServletContext().getInitParameter("pythonwebservice") + "/Webapi/";
		String conString="";
		String user_id_base64 =new String(Base64.encodeBase64String( 
			(null2Str((String)request.getSession(true).getAttribute( "user_id" ))).getBytes()
		));
		String token_base64 = new String(Base64.encodeBase64String( 
			(null2Str((String)request.getSession(true).getAttribute( "token" ))).getBytes()
		));
		logger.debug("Action: " + action);
		try{
			if ("searchAPI_POI".equals(action)) {
				String cate = null2Str(request.getParameter("type"));
				logger.debug("type(base64): " + cate);
				if(cate!=""){
					String filename = ("POI-"+new String(Base64.decodeBase64(cate),"UTF-8"));
					logger.debug("download: " + filename+".json");
					conString = pythonAPIWebservice_host 
								+ "type=UE9J"
								+ "&cate="+ cate
								+ "&usid=" + user_id_base64
								+ "&tokn=" + token_base64;
					
					logger.debug(conString);
					String reply_json = webService(conString);
					response.setContentType( "application/octet-stream" );
					String fileName_tmp = URLEncoder.encode(filename+".json", "UTF8").replaceAll("\\+", "%20");
//					response.setHeader("Content-Disposition","attachment;filename=\"" + fileName_tmp + "\""); 
					response.setHeader("Content-Disposition", "attachment;filename*=utf-8'zh_TW'" + fileName_tmp);
//					response.setHeader( "Content-Disposition", "attachment; filename=\"" + new String(filename.getBytes(), "ISO8859-1")+".json" + "\"" );
					ServletOutputStream op = response.getOutputStream();
					op.write(reply_json.getBytes("UTF-8"));
					op.flush();
			        op.close();
				}
			}else if ("searchAPI_CompanyRegisterType".equals(action)) {
				String cate = null2Str(request.getParameter("type"));
				logger.debug("type(base64): " + cate);
				if(cate!=""){
					String filename = ("公司登記類別-"+new String(Base64.decodeBase64(cate),"UTF-8"));
					logger.debug("download: " + filename+".json");
					conString = pythonAPIWebservice_host 
							+ "type=Q29tcGFueVJlZ2lzdGVyVHlwZQ=="
							+ "&cate=" + cate
							+ "&usid=" + user_id_base64
							+ "&tokn=" + token_base64;
				
					
					String reply_json = webService(conString);
					response.setContentType( "application/octet-stream" );
					response.setHeader( "Content-Disposition", "attachment; filename=\"" + new String(filename.getBytes(), "ISO8859-1")+".json" + "\"" );
					ServletOutputStream op = response.getOutputStream();
					op.write(reply_json.getBytes("UTF-8"));
					op.flush();
			        op.close();
				}
			}else if ("searchAPI_CompanyRegisterList".equals(action)) {
				String cate = null2Str(request.getParameter("type"));
				logger.debug("type(base64): " + cate);
				if(cate!=""){
					String filename = ("公司登記清冊-"+new String(Base64.decodeBase64(cate),"UTF-8"));
					logger.debug("download: " + filename+".json");
					conString = pythonAPIWebservice_host 
							+ "type=Q29tcGFueVJlZ2lzdGVyTGlzdA=="
							+ "&cate=" + cate
							+ "&usid=" + user_id_base64
							+ "&tokn=" + token_base64;
				
					String reply_json = webService(conString);
					response.setContentType( "application/octet-stream" );
					response.setHeader( "Content-Disposition", "attachment; filename=\"" + new String(filename.getBytes(), "ISO8859-1")+".json" + "\"" );
					ServletOutputStream op = response.getOutputStream();
					op.write(reply_json.getBytes("UTF-8"));
					op.flush();
			        op.close();
				}
			}else if ("searchAPI_CompanyRegisterOther".equals(action)) {
				String cate = null2Str(request.getParameter("type"));
				logger.debug("type(base64): " + cate);
				if(cate!=""){
					String filename = ("公司登記其他-"+new String(Base64.decodeBase64(cate),"UTF-8"));
					logger.debug("download: " + filename+".json");
					conString = pythonAPIWebservice_host 
							+ "type=Q29tcGFueVJlZ2lzdGVyT3RoZXI="
							+ "&cate=" + cate
							+ "&usid=" + user_id_base64
							+ "&tokn=" + token_base64;
				
					String reply_json = webService(conString);
					response.setContentType( "application/octet-stream" );
					response.setHeader( "Content-Disposition", "attachment; filename=\"" + new String(filename.getBytes(), "ISO8859-1")+".json" + "\"" );
					ServletOutputStream op = response.getOutputStream();
					op.write(reply_json.getBytes("UTF-8"));
					op.flush();
			        op.close();
				}
			}else if ("searchAPI_CompanyRegisterStat".equals(action)) {
				String cate = null2Str(request.getParameter("type"));
				String dataTitle = null2Str(request.getParameter("dataTitle"));
				logger.debug("type(base64): " + cate);
				if(cate!=""){
					String filename = ("公司登記統計-"+new String(Base64.decodeBase64(dataTitle),"UTF-8"));
					logger.debug("download: " + filename+".json");
					conString = pythonAPIWebservice_host 
							+ "type=Q29tcGFueVJlZ2lzdGVyU3RhdA=="
							+ "&cate=" + cate
							+ "&usid=" + user_id_base64
							+ "&tokn=" + token_base64;
				
					String reply_json = webService(conString);
					response.setContentType( "application/octet-stream" );
					response.setHeader( "Content-Disposition", "attachment; filename=\"" + new String(filename.getBytes(), "ISO8859-1")+".json" + "\"" );
					ServletOutputStream op = response.getOutputStream();
					op.write(reply_json.getBytes("UTF-8"));
					op.flush();
			        op.close();
				}
			}else{
				logger.debug("[TransToJSP]");
				response.sendRedirect("./downloadAPI.jsp");
			}
		}catch(Exception e){
			logger.debug("full-range-log",e);
			e.printStackTrace();
			logger.debug("Error of call APIservlet:"+e.toString()+"]"); 
		}
	}
	protected String webService(String conString) throws ServletException, IOException {
		logger.info("webService get from: "+conString);
		String ret="";
		HttpClient client = new HttpClient();
		HttpMethod method= new GetMethod(conString); 
		try{
			client.executeMethod(method);
			InputStream webservice_reply = method.getResponseBodyAsStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(webservice_reply, writer, "UTF-8");
			ret = writer.toString();
			logger.debug("dataSize: "+ret.length()+"bytes -> "+
				ret.substring(0,(
					ret.length()> 200?200:(ret.length())
				))
			);
			method.releaseConnection();
		}catch(Exception e){
			logger.debug("full-range-log",e);
			e.printStackTrace();
			ret="Error of call webservice:["+conString+"] get:["+e.toString()+"]"; 
		}
		return ret;
	}
	private String null2Str(Object object) {
		if (object instanceof Timestamp)
			return object == null ? "" : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(object);
		return object == null ? "" : object.toString().trim();
	}
}
