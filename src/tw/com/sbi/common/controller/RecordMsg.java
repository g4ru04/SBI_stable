package tw.com.sbi.common.controller;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
//import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
//import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class RecordMsg extends HttpServlet {
	public static boolean noRecord = false ; 
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(RecordMsg.class);
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
//		logger.debug("message: "+CommonMethod.null2str(request.getParameter("message")));
		
		String infoMessage="<table style='font-family: Microsoft JhengHei;'>";
		infoMessage += "<tr><th colspan='2'>======================</td></tr>";
		infoMessage += "<tr><td>主機:</td><td>"+new StartupSetting().deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host")+"</td></tr>";
		infoMessage += "<tr><td>主機IP:</td><td>"+ViewStatus.getGlobalIP()+"</td></tr>";
		infoMessage += "<tr><td>主機部屬時間:</td><td>"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new File(request.getSession().getServletContext().getRealPath("")).lastModified())+"</td></tr>";
		infoMessage += "<tr><td>groupName:</td><td>"+CommonMethod.null2str(request.getSession().getAttribute("group_name"))+"</td></tr>";
		infoMessage += "<tr><td>userName:</td><td>"+CommonMethod.null2str(request.getSession().getAttribute("user_name"))+"</td></tr>";
		infoMessage += "<tr><td>clientIP:</td><td>"+new String(Base64.decodeBase64(CommonMethod.null2str(request.getSession().getAttribute("pSession"))),"UTF-8")+"</td></tr>";
		infoMessage += "<tr><td>time:</td><td>"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+"</td></tr>";
		infoMessage += "</table>";
		String newMessage = "", newMessageToSend = "";
		
		if(!"".equals(CommonMethod.null2str(request.getParameter("message")))){
			logger.debug("SomethingIn");
			newMessage = (new String(Base64.decodeBase64(CommonMethod.null2str(request.getParameter("message"))),"UTF-8")+infoMessage);
			newMessageToSend = new String(Base64.encodeBase64String(newMessage.getBytes("UTF-8")));
		}
		
		if(CommonMethod.null2str(request.getParameter("level")).equals("fatal")){
			sendMail(
				"SBI_MSG(FATAL)",
				newMessageToSend
			);
		}else if(CommonMethod.null2str(request.getParameter("level")).equals("debug")){
			sendMail(
				"SBI_MSG(DEBUG)",
				newMessageToSend
			);
		}else if(!CommonMethod.null2str(request.getParameter("toWhom")).equals("")){
			
			sendMailTo(CommonMethod.null2str(request.getParameter("title")),
						CommonMethod.null2str(request.getParameter("message")),
						CommonMethod.null2str(request.getParameter("toWhom"))
			);
			
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("recordANAL")){
			//recordMsg.do?action=recordANAL&title=ajaxSend&content=title=123
			String act = new String(Base64.decodeBase64(CommonMethod.null2str(request.getParameter("act"))),"UTF-8");
			String content = new String(Base64.decodeBase64(CommonMethod.null2str(request.getParameter("content"))),"UTF-8");
			recordANAL(request,act,content);
			
		}
		
		response.sendRedirect("./login.jsp");
		
	}
	public static void recordANAL(HttpServletRequest rqst, String act,Object content){
		recordANAL(rqst,act,new Gson().toJson(content));
	}
	public static void recordANAL(HttpServletRequest rqst, String act,String content){
		LogManager.getLogger("anal").debug("[" +CommonMethod.null2str(rqst.getSession().getAttribute("group_name"))+" / "+CommonMethod.null2str(rqst.getSession().getAttribute("user_name")) +" ] =="+act+"==> ["+content+"]");
	}
	
	public static void sendMailTo(String title, String content,String toWhom){
		
		new Thread(new Runnable() {
		    public void run() {
		    	try {
		    		if(ViewStatus.getLocalIP().indexOf("192.168")==-1 || true){
		    			my_logger("Have Send Mission: ("+title+","+content+")"+" to "+toWhom);
		    			if(sendMailImplement((ViewStatus.getLocalIP().indexOf("192.168")==-1?title:"(canNeglect)"+title), content, toWhom, 0)){
		    				my_logger("SBI訊息 ，已於 "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+"發送成功");
						}else{
							my_logger("SBI 訊息發信失敗!!");
						}
		    		}
		    		
		    	} catch (Exception e) {
		    		StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					logger.debug(sw.toString());
				}
		    }
		}).start();
		
	}
	public static void sendPhoneMsg(String title,String content,String toWhom){
		try {
			URL url = new URL("https://api.twilio.com/2010-04-01/Accounts/AC4df5ca98148c3665be7914bc79583775/Messages.json");
			URLConnection urlConnection = url.openConnection();

			HttpURLConnection connection = (HttpURLConnection) urlConnection;;
			connection.setRequestMethod("POST");
//			con.setRequestProperty("User-Agent", USER_AGENT);
//			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//			Authenticator.setDefault(new Authenticator() {
//			      protected PasswordAuthentication getPasswordAuthentication() {
//			        return new PasswordAuthentication("AC4df5ca98148c3665be7914bc79583775", "1b7154b41b16ba056124b072aa0291bc".toCharArray());
//			      }
//			    });
			Authenticator.setDefault (new Authenticator() {
			    protected java.net.PasswordAuthentication getPasswordAuthentication() {
			        return new java.net.PasswordAuthentication ("AC4df5ca98148c3665be7914bc79583775", "1b7154b41b16ba056124b072aa0291bc".toCharArray());
			    }
			});
//			byte[] message = ("AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc").getBytes("UTF-8");
//			String encoding = javax.xml.bind.DatatypeConverter.printBase64Binary(message);
//			String encoding = new String(Base64.encodeBase64String("AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc".getBytes()));
//			connection.setRequestProperty("Authorization", "Basic " + encoding);
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.142 Safari/535.19");
			String urlParameters = "To=+886919863010&From=+13346001946&Body=SomethingWrongPleaseCheckMail";

			// Send post request
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
//            connection = (HttpURLConnection) urlConnection;
			logger.debug(connection.getResponseCode());
            new InputStreamReader(connection.getInputStream());
		} catch (Exception e) {
//			CommonMethod.logErr(e);
			logger.debug("full-range-log",e);
		}
		
//		 try {
//            URL url = new URL ("https://api.twilio.com/2010-04-01/Accounts/AC4df5ca98148c3665be7914bc79583775/Messages.json");
////            String encoding = new String(Base64.encodeBase64String("AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc".getBytes()));
////            String encoding = java.util.Base64.getEncoder().encodeToString(
////            		"AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc"
////            		.getBytes(‌"UTF-8")
////            		);
//            String encoding = javax.xml.bind.DatatypeConverter.printBase64Binary("AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc".getBytes());
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.142 Safari/535.19");
//            connection.setRequestProperty("Authorization", "Basic " + encoding);
//
//			connection.setDoOutput(true);
//			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//			String urlParameters = "To=+886"+toWhom+"&From=+13346001946&Body=SomethingWrongPleaseCheckMail";
//			wr.writeBytes(urlParameters);
//			wr.flush();
//			wr.close();
//			logger.debug(connection.getResponseCode());
////			new InputStreamReader(connection.getInputStream());
////            InputStream retcontent = (InputStream)connection.getInputStream();
////            BufferedReader in   = 
////                new BufferedReader (new InputStreamReader (retcontent));
////            String line;
////            while ((line = in.readLine()) != null) {
////                System.out.println(line);
////            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
	}
	public void sendMail(String title, String content) {
		sendMail(title,content,"benchen@pershing.com.tw");
	}
	public void sendMail(String title, String content,String toWhom) {
		try {
			URL url = new URL("http://23.99.114.140/sbi/recordMsg.do");
			URLConnection urlConnection = url.openConnection();

			HttpURLConnection connection = (HttpURLConnection) urlConnection;;
			connection.setRequestMethod("POST");
//			con.setRequestProperty("User-Agent", USER_AGENT);
//			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.142 Safari/535.19");
			String urlParameters = "title="+title+"&message="+content.replaceAll("\\+","%2B")+"&toWhom="+toWhom;

			// Send post request
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
//            connection = (HttpURLConnection) urlConnection;
            
            new InputStreamReader(connection.getInputStream());
		} catch (Exception e) {
			logger.debug("full-range-log",e);
//			CommonMethod.logErr(e);
		}
	}
			
	public static boolean sendMailImplement(String title, String content, String toWhom, int pwdErr){
		
		try{
			if(!"23.99.114.140".equals(ViewStatus.getGlobalIP())){
				try{
					URL url = new URL("http://23.99.114.140/sbi/recordMsg.do?title="+title+"&message="+content.replaceAll("\\+","%2B")+"&toWhom="+toWhom);
					URLConnection urlConnection = url.openConnection();
					
					HttpURLConnection connection = null;
		            connection = (HttpURLConnection) urlConnection;
		            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.142 Safari/535.19");
		            new InputStreamReader(connection.getInputStream());
		            my_logger("交接成功!! at:"+pwdErr);
			        return true ;
			        
			    }catch(IOException e){
			    	logger.debug("full-range-log",e);
			    	my_logger("交接發信訊息失敗!! at:"+pwdErr);
			    	return false ;
			    }
				
			}else{
				Calendar todayCalendar= Calendar.getInstance();
				String smtphost = "cloud-pershing-com-tw.mail.protection.outlook.com";
				String smtpport = "25";
				String username = "pscaber@cloud.pershing.com.tw";
				String pwd = "Pershing.2017!!";
				
				if(pwdErr==0){
					//nothing~
				}else if(pwdErr==1){
					pwd = "Pershing."+todayCalendar.get(Calendar.YEAR)+""+(todayCalendar.get(Calendar.MONTH)+1>6?"!!":"!");
				}else if(pwdErr==2){
					todayCalendar.add(Calendar.MONTH, -6);
					pwd = "Pershing."+todayCalendar.get(Calendar.YEAR)+""+(todayCalendar.get(Calendar.MONTH)+1>6?"!!":"!");
				}else{
					my_logger("訊息未發送失敗!! at:"+pwdErr);
					return false;
				}
				
				String password = pwd;
				String towhom = toWhom;
				
				title = "".equals(title)?"SBI 錯誤訊息":title;
				
		        Properties props = new Properties();
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.starttls.enable", "false");
		        props.put("mail.smtp.host",smtphost);
		        props.put("mail.smtp.port", smtpport);
		        
		        
		        Session session = Session.getInstance(props,
			          new javax.mail.Authenticator() {
			            protected PasswordAuthentication getPasswordAuthentication() {
			            	return new PasswordAuthentication(username,password);
			            }
			          });
		        
		        try {
		            Message message = new MimeMessage(session);
		            message.setFrom(new InternetAddress(username));
		            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(towhom));
		            
		            message.setSubject(title);
		            
		            MimeBodyPart textPart = new MimeBodyPart();
		            StringBuffer html = new StringBuffer();
		            html.append("<table style='margin:0 auto;padding:0;width:100%;font-size:1.5em;font-family: \"微軟正黑體\"' align='center' border='0' ><tr><td>"
		            			+new String(Base64.decodeBase64(content),"UTF-8")
		            			+"</td></tr></table>");
		            textPart.setContent(html.toString(), "text/html; charset=UTF-8");
		            Multipart mmp = new MimeMultipart();
		            mmp.addBodyPart(textPart);
		            message.setContent(mmp);
		            Transport.send(message);
		            my_logger("發送成功 at:"+pwdErr);
		            return true;
		            
		        }catch (Exception e) {
		        	logger.debug("full-range-log",e);
		        	my_logger(e.toString());
		        	if(pwdErr>1){
		        		StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						my_logger("訊息發信三次失敗!! at:"+pwdErr);
			        	my_logger(sw.toString());
			        	return false;
		        	}else{
		        		return sendMailImplement(title, content, toWhom, pwdErr+1); 
		        	}
		        	
		        }
			}
		}catch(Exception e){
			logger.debug("full-range-log",e);
			my_logger(e.toString());
	    	return false ;
	    }
	}
	public static void my_logger(String msg){
		if(!noRecord){
			logger.debug(msg);
		}
	}
}
