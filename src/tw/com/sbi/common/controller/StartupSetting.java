package tw.com.sbi.common.controller;
/**
 * // 1.寄信給我 系統狀態 + 測試報告
		// 2.O 依照網址修改web.xml 不同tomcat不同位置
		// 3.看有沒有要記錄的東西 這裡宣告或記下來之類的 ex:總登入人數=0
		// 留下自動成長版本號

		// 但不是做在這頁的
		// 4.三個DB檢察diff
		// 列出table不同資料數量的
		// 列出sp不同資料的
		// 誰多什麼 誰少什麼 以164為基礎
		// textsearch 系統總共用到哪些sp 列出來
		//
		// 5. 沒用到的頁面統和
		// ex 匯入POI 可是沒有地方選出來 偷偷放公車站 台鐵高鐵站
		// ex flowTrendsAll.jsp
 * 
 */
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

//import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@WebListener
public class StartupSetting implements ServletContextListener {
	private static final Logger logger = LogManager.getLogger(StartupSetting.class);
	public JSONObject deployConfig = new JSONObject( //revise 和 report都會用到
			"{ \"61.218.5.190\": {  \"host\": \"my_pc\",  \"dbURL\": \"jdbc:mysql://192.168.112.164:3306/cdri\",  \"dbUserName\": \"root\",   \"dbPassword\": \"admin123\",  \"pythonwebservice\": \"http://192.168.112.164:8070\",  \"page\": \"http://localhost:8080/sbi/\" },"
			+" \"125.227.140.193\": {  \"host\": \"164\",  \"dbURL\": \"jdbc:mysql://192.168.112.164:3306/cdri\",  \"dbUserName\": \"root\",  \"dbPassword\": \"admin123\",  \"pythonwebservice\": \"http://192.168.112.164:8070\",  \"page\": \"http://192.168.112.164/sbi/\" },"
			+" \"23.99.114.140\": {  \"host\": \"aber1\",  \"dbURL\": \"jdbc:mysql://10.0.0.4/sbi\",  \"dbUserName\": \"root\",  \"dbPassword\": \"Admin@csi1008!\",  \"pythonwebservice\": \"http://localhost:8099\",  \"page\": \"http://abers1.eastasia.cloudapp.azure.com/sbi/\" },"
			+" \"52.175.19.14\": {  \"host\": \"aber2\",  \"dbURL\": \"jdbc:mysql://10.0.0.4/sbi\",  \"dbUserName\": \"root\",  \"dbPassword\": \"Admin@csi1008!\",  \"pythonwebservice\": \"http://localhost:8099\",  \"page\": \"http://abers2.eastasia.cloudapp.azure.com/sbi/\" },"
			+" \"61.218.8.55\": {  \"host\": \"55\",  \"dbURL\": \"jdbc:mysql://localhost:3306/sbi\",  \"dbUserName\": \"cdri\",  \"dbPassword\": \"Admin1008!\",  \"pythonwebservice\": \"http://61.218.8.55:8099\",  \"page\": \"http://sbi1.cdri.org.tw/sbi/\" } } ");
	
	public void contextInitialized(ServletContextEvent event) {

		try {
			String sysString = StartupSetting.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String projectPath = sysString.split(sysString.split("/")[sysString.split("/").length - 1])[0];
			File tag = new File(projectPath + "Deployed_Over");
			
			//logger.debug("Ben's projectPath: D:/workspace/eclipse/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/sbi/WEB-INF/");
			boolean deployed_over_effective = ( new File(projectPath + "Deployed_Over")
					.lastModified()+ 5 * 60 * 1000
				>  new File(projectPath + "web.xml")
					.lastModified()
				);
			if (tag.exists() && deployed_over_effective) {
				return;
			} else {
				if(!deployed_over_effective){
					logger.debug("deployed over lose efficacy");
				}
				tag.delete();
				tag.createNewFile();
			}
			
			Map<String, String> contextParamMap = new HashMap<String, String>();

			contextParamMap = new HashMap<String, String>();
			ServletContext sc = event.getServletContext();
			Enumeration<String> parameters = sc.getInitParameterNames();
			while (parameters.hasMoreElements()) {
				String parameter = (String) parameters.nextElement();
				contextParamMap.put(parameter, sc.getInitParameter(parameter));
			}
			
			logger.debug("start-up");
			reviseWebxml(contextParamMap);
			new TimerManager(contextParamMap);
		} catch (Exception e) {
			CommonMethod.logErr(e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.debug("shut-down-exec-ShutdownHook");
			}
		});
	}

	public void contextDestroyed(ServletContextEvent event) {
		 logger.debug("shut-down-exec-contextDestroyed");
	}


	public void submitReport(String title, Map<String, String> contextParamMap) {
		logger.debug("submitReport - > sendMail");
		try {
			RecordMsg recorder = new RecordMsg();
			RecordMsg.noRecord = true;
			if("DailyCheck".equals(title) && Arrays.asList("aber2").contains(deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host"))){
				recorder.sendMail("SBI_" + title + "_MSG"+ "(" + deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host")+")" ,
					new String(Base64.encodeBase64String(
						(title + "-" + deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host") 
							+ " At: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "<br>"
							+ briefStatusReport(contextParamMap)
						).getBytes("UTF-8"))),
					new String(Base64.decodeBase64("bWV0YWxpdUBwZXJzaGluZy5jb20udHc="),"UTF-8"));
			}
			recorder.sendMail("SBI_" + title + "_MSG"+ "(" + deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host")+")" ,
				new String(Base64.encodeBase64String(
					(title + "-" + deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("host") 
						+ " At: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "<br>"
						+ briefStatusReport(contextParamMap)
					).getBytes("UTF-8")))
				);
		} catch (Exception e) {
			CommonMethod.logErr(e);
		}
	}

	public String reviseWebxml(Map<String, String> contextParamMap) {
		
		JSONObject thisHost = deployConfig.getJSONObject(ViewStatus.getGlobalIP());

		String sysString = StartupSetting.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String projectPath = sysString.split(sysString.split("/")[sysString.split("/").length - 1])[0];
		try {
			File inputFile = new File(projectPath + "web.xml");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(inputFile);
			NodeList contextParams = doc.getElementsByTagName("context-param");
			for (int i = 0; i < contextParams.getLength(); i++) {
				Node paraNode = contextParams.item(i);
				NodeList list = paraNode.getChildNodes();
				String editCategory = "", editString = "";
				for (int j = 0; j < list.getLength(); j++) {
					Node node = list.item(j);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) node;
						if ("param-name".equals(eElement.getNodeName()) && "dbURL".equals(eElement.getTextContent())) {
							editString = thisHost.getString("dbURL");
							editCategory = "dbURL";
						}
						if ("param-name".equals(eElement.getNodeName())
								&& "dbUserName".equals(eElement.getTextContent())) {
							editString = thisHost.getString("dbUserName");
							editCategory = "dbUserName";
						}
						if ("param-name".equals(eElement.getNodeName())
								&& "dbPassword".equals(eElement.getTextContent())) {
							editString = thisHost.getString("dbPassword");
							editCategory = "dbPassword";
						}
						if ("param-name".equals(eElement.getNodeName())
								&& "pythonwebservice".equals(eElement.getTextContent())) {
							editString = thisHost.getString("pythonwebservice");
							editCategory = "pythonwebservice";
						}
						if ("param-value".equals(eElement.getNodeName()) && "".equals(eElement.getTextContent())
								&& !"".equals(editString)) {
							logger.debug(editCategory+" ## "+editString);
							
							
							eElement.setTextContent(editString);
							contextParamMap.put(editCategory, editString);
						}
					}
				}

			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			FileOutputStream os = new FileOutputStream(projectPath + "web.xml");
			StreamResult consoleResult = new StreamResult(os);

			transformer.transform(source, consoleResult);
		} catch (Exception e) {
			CommonMethod.logErr(e);
		}
		return thisHost.getString("host");
	}

	public String briefStatusReport(Map<String, String> contextParamMap) {
		String sysString = StartupSetting.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		JSONObject thisHostInfo = deployConfig.getJSONObject(ViewStatus.getGlobalIP());
		
		String cell_1="";
		String cell_2="";
		String cell_3="";
		String cell_4="";
		String cell_5="";
		
		try {
			
			cell_1 += "<table style='font-family: Microsoft JhengHei;'>";
			cell_1 += "<tr><td><b>系統名稱：</b></td><td>SBI-" + thisHostInfo.get("host") + "</td></tr>";
			cell_1 += "<tr><td><b>系統host：</b></td><td>" + ViewStatus.getGlobalIP() + "</td></tr>";
			cell_1 += "<tr><td><b>部屬時間(rootDIR)：</b></td><td>" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
					new File(sysString.split(sysString.split("/")[sysString.split("/").length - 2])[0]).lastModified())
					+ " </td></tr>";
			String wsPath = contextParamMap.get("pythonwebservice");
			String wsBuff = "";
			try{
				String[] webserviceName = { "sbiupload", "selectregion", "OpenData", "persona", "entrysrategy", "finance",
					"Webapi" };
				for (int i = 0; i < webserviceName.length; i++) {
					URL url = new URL(wsPath + "/" + webserviceName[i] + "/");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.connect();
					int responseCode = connection.getResponseCode();
	
					wsBuff += ("".equals(wsBuff) ? "" : ", ") + responseCode;
				}
			}catch(Exception e){
				wsBuff = "連線時發生未知錯誤";
				CommonMethod.logErr("webserviceconnectError：",e);
			}
			cell_1 += "<tr><td><b>PythonWebservice：</b></td><td>" + wsBuff + "</td></tr>";
			cell_1 += "</table>";
			
			cell_2 += "<table style='font-family: Microsoft JhengHei;'><tr>";
			
			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"' target='_blank'><table style='font-family: Microsoft JhengHei;background:#03a9f4;'><tr><td style='padding:3px 5px'><a href='"+thisHostInfo.get("page")+"' target='_blank'><font color='white' style='font-weight:800px;text-decoration:none;'>系統頁面</font></a></td></tr></table></a></td>";
			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"/viewStatus' target='_blank'><table style='font-family: Microsoft JhengHei;background:#03a9f4;'><tr><td style='padding:3px 5px'><a href='"+thisHostInfo.get("page")+"/viewStatus' target='_blank'><font color='white' style='font-weight:800px;text-decoration:none;'>管理頁面</font></a></td></tr></table></a></td>";
			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewLog' target='_blank'><table style='font-family: Microsoft JhengHei;background:#03a9f4;'><tr><td style='padding:3px 5px'><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewLog' target='_blank'><font color='white' style='font-weight:800px;text-decoration:none;'>系統Log</font></a></td></tr></table></a></td>";
			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewPageStatus' target='_blank'><table style='font-family: Microsoft JhengHei;background:#03a9f4;'><tr><td style='padding:3px 5px'><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewPageStatus' target='_blank'><font color='white' style='font-weight:800px;text-decoration:none;'>頁面修改資訊</font></a></td></tr></table></a></td>";
			
//			cell_2 += "<td style='text-align:center;height:24px;'><a style='margin:4px;' href='"+thisHostInfo.get("page")+"' target='_blank'><div style='height:24px;width:100px;background:#03a9f4;text-align:center;'><font color='#03a9f4'>　　</font><font color='white'>系統頁面</font><font color='#03a9f4'>　　</font></div></a></td> ";
//			cell_2 += "<td style='text-align:center;height:24px;'><div style='height:24px;width:100px;background:#03a9f4;text-align:center;'><a style='margin:4px;' href='"+thisHostInfo.get("page")+"' target='_blank'><font color='#03a9f4'>　　</font><font color='white'>系統頁面</font><font color='#03a9f4'>　　</font></a></div></td> ";
//			cell_2 += "<td style='text-align:center;height:24px;'><div style='height:24px;width:100px;background:#03a9f4;text-align:center;'><a style='margin:4px;' href='"+thisHostInfo.get("page")+"' target='_blank'><font color='#03a9f4'>　　</font><font color='white'>系統頁面</font><font color='#03a9f4'>　　</font></a></div></td> ";
//			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"' target='_blank'>系統頁面</a></td>";
//			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"/viewStatus' target='_blank'>管理頁面</a></td>";
//			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewLog' target='_blank'>系統Log</a></td>";
//			cell_2 += "<td style='padding:5px;'><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewPageStatus' target='_blank'>頁面修改資訊</a></td>";
//			cell_2 += "<td style='height:18px;padding-left:12px;padding-right:12px;'><div><a class='href' href='"+thisHostInfo.get("page")+"/viewStatus?action=viewPageStatus' target='_blank'><font color='white' style='text-decoration:none;'>頁面修改資訊</font></a></td>";
//			cell_2 += "<tr><td>系統頁面:　</td><td colspan='2'><a href='"+thisHostInfo.get("page")+"'>跳轉</a> ;</td></tr>";
//			cell_2 += "<tr><td>管理頁面:　</td><td><a href='"+thisHostInfo.get("page")+"/viewStatus' target='_blank'>點我</a> ;</td></tr>";
//			cell_2 += "<tr><td>系統Log:　</td><td><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewLog' target='_blank'>點我</a> ;</td></tr>";
//			cell_2 += "<tr><td>頁面修改資訊:　</td><td><a href='"+thisHostInfo.get("page")+"/viewStatus?action=viewPageStatus' target='_blank'>點我</a> ;</td></tr>";
//			cell_2 += "</table>";
			cell_2 += "</tr></table>";
			cell_3 += "<table style='font-family: Microsoft JhengHei;border-collapse: collapse;border-spacing: 0;'>";
			List<Map<String, String>> outcome =  new ArrayList<Map<String,String>>();//new CommonMethod(getServletConfig()).useMysqlDB("call sp_get_update_situation()",null);
			try{
				outcome = new CommonMethod(
							contextParamMap.get("dbURL"),
							contextParamMap.get("dbUserName"),
							contextParamMap.get("dbPassword")
						).useMysqlDB("call sp_get_data_resume('')",null);
				
				cell_3 += "<tr>"
						+ "<th style='border:1px solid #000;border-right:1px solid #555;background-color:#ccc;'>資料名稱</th>"
						+ "<th style='border:1px solid #000;border-right:1px solid #555;background-color:#ccc;'>SBI中日期/數量</th>"
						+ "<th style='border:1px solid #000;border-right:1px solid #555;background-color:#ccc;'>資料用途說明</th>"
						+ "<th style='border:1px solid #000;border-right:1px solid #555;background-color:#ccc;'>資料來源</th>"
						+ "</tr>";
				for(int i=0 ; i<outcome.size() ; i++){
//					cell_3 += "<tr><td colspan='4'><hr style='color: #aaa;'></td></tr>";
					cell_3 += "<tr>"
							+ "<td style='padding:12px;border:1px solid #555;background-color:#efefef;'>"+outcome.get(i).get("data_zh_name")+"</td>"
							+ "<td style='padding:12px;border:1px solid #555;background-color:#efefef;'>"+outcome.get(i).get("data_time") +"</td>"//+"<br>"+outcome.get(i).get("data_amount")+"</td>"
							+ "<td style='padding:12px;border:1px solid #555;background-color:#efefef;'>"+outcome.get(i).get("data_purpose")+"</td>"
							+ "<td style='padding:12px;border:1px solid #555;background-color:#efefef;'>"+outcome.get(i).get("data_source")+"</td>"
							+ "</tr>";
				}
				
			}catch(Exception e){
				cell_3 += "<tr><td>=====　讀取資料狀態失敗　=====</td></tr>";
				logger.debug(e.toString());
				logger.debug("full-range-log",e);
			}
			cell_3 += "</table>";
			
			cell_4 += "<table style='font-family: Microsoft JhengHei;'>";
			cell_4 += "<tr><td><b>dbURL：</b></td><td>"+contextParamMap.get("dbURL") + "</td></tr>";
			cell_4 += "<tr><td><b>dbUserName：</b></td><td>"+contextParamMap.get("dbUserName") + "</td></tr>";
			cell_4 += "<tr><td><b>dbPassword：</b></td><td>"+contextParamMap.get("dbPassword") + "</td></tr>";
			cell_4 += "<tr><td><b>pythonService：<b></td><td><font color='black'  style='text-decoration:none;'>"+contextParamMap.get("pythonwebservice") + "</font></td></tr>";
			cell_4 += "</table>";
			
			
//			String crossImgDataURL ="<img src=\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 18 18'%3E%3Cpath fill='none' stroke='%23F57373' stroke-width='2' d='M1.13 1.13L16.9 16.9m-15.77 0L16.9 1.13' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E\"/>";
//			String tickImgDataURL ="<img src=\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='15' viewBox='0 0 16 15'%3E%3Cpath fill='%2310CC72' fill-rule='evenodd' d='M5.34 14.57c-.3 0-.6-.13-.83-.35L.35 9.94C-.1 9.48-.1 8.74.36 8.3c.46-.45 1.2-.44 1.64 0l3.23 3.33L13.63.46c.37-.5 1.1-.6 1.6-.23.52.4.62 1.12.24 1.63L6.27 14.1c-.2.28-.52.44-.85.47h-.08'/%3E%3C/svg%3E/>";
			String crossTableCell = "<td style='text-align:center;border:1px solid #555;background-color:#fefefe;'><b><font color='red' style='font-weight:900;'>X</font></b></td>";
			String tickTableCell = "<td style='text-align:center;border:1px solid #555;background-color:#fefefe;'><b><font color='green' style='font-weight:900;'>√</font></b></td>";
			cell_5 += "<table style='font-family: Microsoft JhengHei;border-collapse: collapse;border-spacing: 0;'>";
			cell_5 += "<tr>"
					+ "<th style='text-align:center;width:140px;border:1px solid #000;padding:2px 4px;background-color:#efefef;'><b>資料庫數據連接</b></th>"
					+ "<th style='text-align:center;width:140px;border:1px solid #000;padding:2px 4px;background-color:#efefef;'><b>Python網路服務</b></th>"
					+ "<th style='text-align:center;width:140px;border:1px solid #000;padding:2px 4px;background-color:#efefef;'><b>後台系統運作</b></th>"
					+ "<th style='text-align:center;width:140px;border:1px solid #000;padding:2px 4px;background-color:#efefef;'><b>錯誤回報機制</b></th>"
					+ "</tr>";
			cell_5 += "<tr>";
			try{
				new CommonMethod(
					contextParamMap.get("dbURL"),
					contextParamMap.get("dbUserName"),
					contextParamMap.get("dbPassword")
				).useMysqlDB("call sp_selectall_user('')",null);
				cell_5 += tickTableCell;
			}catch(Exception e){
				logger.debug("full-range-log",e);
				cell_5 += crossTableCell;
			}
			try{
				HttpURLConnection connection = (HttpURLConnection) new URL(contextParamMap.get("pythonwebservice") + "/Webapi/").openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				if(connection.getResponseCode()==200){
					cell_5 += tickTableCell;
				}else{
					cell_5 += crossTableCell;
				}
			}catch(Exception e){
				logger.debug("full-range-log",e);
				cell_5 += crossTableCell;
			}
			cell_5 += tickTableCell;
			cell_5 += tickTableCell;
			cell_5 += "</tr>";
			cell_5 += "</table>";
		} catch (Exception e) {
			logger.debug("full-range-log",e);
			CommonMethod.logErr("briefStatusReportError",e);
		}
		//background:#03a9f4;border-radius:3px;color:#ffffff;display:block;font-family:Roboto,Helvetica Neue,Helvetica,Arial,sans-serif;font-weight:normal;letter-spacing:1px;padding:14px 8px;text-decoration:none
		String report = "<table style='font-family: Microsoft JhengHei;width:100%;'>";
		report += "<tr><td style='padding-top:18px;padding-left:10px;'><div class='title'>系統狀態</div></td></tr>";
		report += "<tr><td class='cells'>"+cell_5+ "</td></tr>";
		report += "<tr><tr style='height:18px'></tr></tr>";
		report += "<tr><td style='padding-top:18px;padding-left:10px;'><div class='title'>系統資訊</div></td></tr>";
		report += "<tr><td class='cells'>"+cell_1+ "</td></tr>";
		report += "<tr><tr style='height:18px'></tr></tr>";
		report += "<tr><td style='padding-top:18px;padding-left:10px;'><div class='title'>外部連結</div></td></tr>";
		report += "<tr><td class='cells'>"+cell_2+ "</td></tr>";
		report += "<tr><tr style='height:18px'></tr></tr>";
		report += "<tr><td style='padding-top:18px;padding-left:10px;'><div class='title'>資料狀態</div></td></tr>";
		report += "<tr><td class='cells'>"+cell_3+ "</td></tr>";
		report += "<tr><tr style='height:18px'></tr></tr>";
		report += "<tr><td style='padding-top:18px;padding-left:10px;'><div class='title'>CONFIG</div></td></tr>";
		report += "<tr><td class='cells'>"+cell_4+ "</td></tr>";
		report += "<tr><tr style='height:18px'></tr></tr>";
		report += "</table>";
		report = report.replaceAll("class='href'", "style='height:24px;background:#03a9f4;border-radius:3px;padding-right:8px;padding-left:8px;padding-top:6px;'");
		report = report.replaceAll("class='title'", "style='border-bottom:1px solid #666; background:#eee; border-radius:8px;font-weight:900;font-size:1.2em;'");
		report = report.replaceAll("class='cells'", "style='padding-left:20px;'");

		return report;
	}
	
	//以下timer相關
	public class TimerManager {
		Map<String, String> contextParamMap = null;
		public TimerManager(Map<String, String> contextParamMap) {
			this.contextParamMap = contextParamMap;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, 3);
			new Timer().schedule(new DelayTouch(), calendar.getTime());
			calendar.add(Calendar.SECOND, 20);
			new Timer().schedule(new DelayForMails(contextParamMap), calendar.getTime());
		}
	}

	// Timer1 三秒後touch
	public class DelayTouch extends TimerTask {
		@Override
		public void run() {
			String sysString = StartupSetting.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String projectPath = sysString.split(sysString.split("/")[sysString.split("/").length - 1])[0];
			new File(projectPath + "web.xml").setLastModified(System.currentTimeMillis());
			logger.debug("Have Touched");
		}
	}
	
	// Timer2 20秒後deployee
	public class DelayForMails extends TimerTask {
		Map<String, String> contextParamMap = null;
		public DelayForMails(Map<String, String> contextParamMap) {
			this.contextParamMap = contextParamMap;
		}
		@Override
		public void run() {
			try {
//				submitReport("DEPLOY",contextParamMap);
				URL url = new URL(deployConfig.getJSONObject(ViewStatus.getGlobalIP()).getString("page") +"/viewStatus?action=ToStartReport");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.142 Safari/535.19");
		        connection.getInputStream(); //new InputStreamReader();
			}catch(Exception e){
				CommonMethod.logErr(e);
			}
		}
	}
}