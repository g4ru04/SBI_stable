package tw.com.sbi.upload.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.config.Lookup;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.BasicResponseHandler;
//import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;


public class Upload extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(Upload.class);
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		logger.info("upload start");
		logger.info(request.getSession().getAttribute("group_id").toString());
		if(request.getSession().getAttribute("group_id")==null){
			logger.info("session group_id == null");
			return;
		}
		
		String conString="";
		String ret="";
		
		conString=putFile(request, response);
		try{
			TimeUnit.SECONDS.sleep(3);
		}catch(Exception e){
			ret="Sleep error";
			logger.debug("full-range-log",e);
		}
		if(conString.charAt(0)!='E'){
			ret=webService(request, response,conString);
		}else{
			ret=conString;
		}

		logger.info("ret: " + ret);
		request.setAttribute("action",ret);
		RequestDispatcher successView = request.getRequestDispatcher("./upload.jsp");
		successView.forward(request, response);
		logger.info("upload end");
		return ;
	}
	
	protected String putFile(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String conString="";
		String ret="";
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    String group_id = request.getSession().getAttribute("group_id").toString();
	    String user_id = request.getSession().getAttribute("user_id").toString();
		String dbName = request.getParameter("db_name");
	    logger.info("dbName:" + dbName); 
	    user_id =(user_id==null||user_id.length()<3)?"UNKNOWN":user_id;
	    group_id=(group_id==null)?"UNKNOWN":group_id;
		String no_way = getServletConfig().getServletContext().getInitParameter("uploadpath")+"/"+group_id+"/"+dbName+"_";
		new File(getServletConfig().getServletContext().getInitParameter("uploadpath")+"/").mkdir();
		new File(getServletConfig().getServletContext().getInitParameter("uploadpath")+"/"+group_id).mkdir();
		new File(getServletConfig().getServletContext().getInitParameter("uploadpath")+"/fail").mkdir();
		int maxFileSize = 5000 * 1024;
		int maxMemSize = 5000 * 1024;
		String contentType = request.getContentType();
		if (contentType!=null && (contentType.indexOf("multipart/form-data") >= 0)) {
		      DiskFileItemFactory factory = new DiskFileItemFactory();
		      factory.setSizeThreshold(maxMemSize);
		      String file_over=getServletConfig().getServletContext().getInitParameter("uploadpath")+"/fail";
		      factory.setRepository(new File(file_over));
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      upload.setSizeMax( maxFileSize );
		      try{ 
		         List<FileItem> fileItems = upload.parseRequest(request);
		         Iterator<FileItem> i = fileItems.iterator();
		         while ( i.hasNext () ) 
		         {
		            FileItem fi = (FileItem)i.next();
		            if ( !fi.isFormField () ) {
		                String fileName = fi.getName();
		                logger.info("fileName: " + fileName);
		                logger.info("no_way: " + no_way);
		                String fullname = no_way + fileName;
		                logger.info("fullname: "+fullname);
						conString=getServletConfig().getServletContext().getInitParameter("pythonwebservice")
								+"/sbiupload/urls="
								+new String(Base64.encodeBase64String((fullname).getBytes()));
						logger.info("conString: " + conString);
		                File file ;
		                file = new File(fullname) ;
		                fi.write( file ) ;
		                logger.info("success");
		            }
		         }
		      }catch(Exception ex) {
		    	  logger.debug("full-range-log",ex);
		          ret="E_write_File:"+ex.toString();
		          return ret;
		      }
		   }else{
			   ret="E_No one found.";
			   return ret;
		   }
		if(ret.length()>3){
			return ret;
		}
		return conString;
	}
	protected String webService(HttpServletRequest request,HttpServletResponse response,String conString) throws ServletException, IOException {
		logger.info("into webService");
		String ret="";
		HttpClient client = new HttpClient();
		HttpMethod method= new GetMethod(conString); 
		try{
			client.executeMethod(method);
		}catch(Exception e){
			logger.debug("full-range-log",e);
			ret=e.toString();
			ret="Error of call webservice:"+ret; 
		}
		ret = method.getResponseBodyAsString();
//		if("success".compareTo(method.getResponseBodyAsString())!=0){
//			ret="Error_Connection: "+conString;
//		}else{
//			ret="success";
//		}
		method.releaseConnection();
		return ret;
	}
}
