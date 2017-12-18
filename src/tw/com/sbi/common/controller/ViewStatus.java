package tw.com.sbi.common.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tw.com.sbi.common.controller.TestPlace.AssistMethod;
import tw.com.sbi.dataupdate.controller.POIiconCopy;
import tw.com.sbi.dataupdate.controller.UpdateFreewayFlow;
import tw.com.sbi.dataupdate.controller.UpdateMetroFlow;

@WebServlet("/viewStatus")
public class ViewStatus extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(ViewStatus.class);
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		
		logger.debug("action: "+CommonMethod.null2str(request.getParameter("action")));
		if(CommonMethod.null2str(request.getParameter("action")).equals("")){
			String ret = "";
			
			ret += ("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
					+ "<title>檢視內容</title>"
					+ "<style>body {background-image: url(images/SBI-WaterMarker-loading.png);"
					+ "background-repeat: no-repeat;"
					+ "background-attachment: fixed;"
					+ "background-position: center;"
					+ "background-size: contain;"
					+ "}</style>"
					+ "</head><body>"
					+ "<script>window.location.href = './viewStatus?action=main';</script>"
					+ "</body></html>");
			
			response.getWriter().write(ret);
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("main")){
			
			try {
				logger.debug("ViewStatus by: [ "+getLocalIP()+" , "+getGlobalIP()+" ]");
//				if(CommonMethod.null2str(request.getParameter("action")).equals("main")){
//					throw new Exception("123");
//				}
				//設定各式各樣的參數對照
				Map<String, String> getName_zhTW = new HashMap<>();
				getName_zhTW.put("population_index_village", "人口社經資料1");
				getName_zhTW.put("population_village", "人口社經資料2");
				getName_zhTW.put("tb_countrystatistic", "國家-社經資料");
				getName_zhTW.put("tb_data_BD", "商圈資訊");
				getName_zhTW.put("tb_data_POI", "POI資訊");
				getName_zhTW.put("tb_data_interchange_data", "交流道車流量");
				getName_zhTW.put("tb_data_metro_flow_data", "捷運人流");
				getName_zhTW.put("tb_upload_doc", "商機觀測站");
				
				Map<String, String> getOpenDataTime = new HashMap<>();
				getOpenDataTime.put("population_index_village", "DetectNotYet");//https://segis.moi.gov.tw/STAT/Web/Portal/STAT_PortalHome.aspx
				getOpenDataTime.put("population_village", "DetectNotYet");//https://segis.moi.gov.tw/STAT/Web/Portal/STAT_PortalHome.aspx
				getOpenDataTime.put("tb_countrystatistic", "DetectNotYet");
				getOpenDataTime.put("tb_data_BD", "DetectNotYet");
				getOpenDataTime.put("tb_data_POI", "");//尚無來源?或者來源過於廣範
				getOpenDataTime.put("tb_data_interchange_data", "<a href='http://tisvcloud.freeway.gov.tw/history/TDCS/M03A/'>"+UpdateFreewayFlow.getOpenDataStatus(getServletConfig())+"</a>");
				getOpenDataTime.put("tb_data_metro_flow_data", "<a href='http://data.taipei/opendata/datalist/datasetMeta?oid=63f31c7e-7fc3-418b-bd82-b95158755b4d'>"+UpdateMetroFlow.getOpenDataStatus(getServletConfig())+"</a>");
				getOpenDataTime.put("tb_upload_doc", "請洽商發院");
				
				Map<String, String> pythonWebserviceName = new HashMap<>();
				pythonWebserviceName.put("sbiupload", "產業分析基礎資料更新程序");
				pythonWebserviceName.put("selectregion", "區位選擇取答案");
				pythonWebserviceName.put("OpenData", "population.jsp頁面(不明之更新)");
				pythonWebserviceName.put("persona", "目標客群定位");
				pythonWebserviceName.put("entrysrategy", "市場進入策略");
				pythonWebserviceName.put("finance", "新創公司損益平衡");
				pythonWebserviceName.put("Webapi", "API資料下載");
				
				Map<String, String> ipToWhom = new HashMap<>();
				
				ipToWhom.put("192.168.112.164", "自家164");
				ipToWhom.put("10.0.0.4", "Aber-１");
				ipToWhom.put("10.0.0.5", "Aber-２");
				ipToWhom.put("127.0.0.1", "商研院主機");
//				ipToWhom.put("192.168.25.80", "我的電腦上的Eclipse");浮動ip 無法使用
				
				ipToWhom.put("125.227.140.193", "自家164(外)");
				ipToWhom.put("23.99.114.140", "Aber-１(外)");
				ipToWhom.put("52.175.19.14", "Aber-２(外)");
				ipToWhom.put("61.218.8.55", "商研院主機(外)");
				ipToWhom.put("61.218.5.190", "文湖街18號電腦上的Eclipse(外)");
				
				String ret = "";
				ret += ("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
						+ "<title>系統狀態</title>"
						+ "<link rel=\"stylesheet\" href=\"http://www.a-ber.com.tw/sbi/css/styles_populationNew.css\">"
						+ "<script src=\"http://www.a-ber.com.tw/sbi/js/jquery-1.12.4.min.js\"></script>"
						+ "<style>"
						+ ".excel-table td:nth-child(2),.excel-table td:nth-child(3){text-align:right;}"
						+ ".blink_me {animation: blinker 1s linear infinite;} @keyframes blinker { 50% { opacity: 0; }}"
						+ "table.padding td, table.padding th{ padding:2px 8px;border-bottom:1px solid #888; }"
						+ ".padding td:nth-child(2){text-align:center;}"
						+ "</style>"
						+ "</head><body style='background-color:#efe;'>"
//						+ "</head><body style='background-image: url(images/SBI-WaterMarker.png);background-repeat: no-repeat;background-attachment: fixed;background-position: center;background-size: contain;'>"
						+ "<script>"
						+ "var startT= new Date();"
						+ "if(btoa(prompt('輸入管理者密碼：'))!='YWRtaW4xMjM='|| (new Date()-startT)> 10*1000 ){"
						+ "		window.location.href = 'http://www.a-ber.com.tw/sbi/';"
						+ "}"
						+ "function getMenuname(d){return d.reduce(function(a,d){return a.concat(d[\"menuName\"]).concat(getMenuname(d[\"subMenu\"]));},[]);}"
						+ "var dealKeyPress=true;"
						+ "$( \"body\" ).keypress(function(event) {"
						+ "		if(!dealKeyPress){return;}"
						+ "		event.preventDefault();"
						+ "		console.log(event.which);"
						+ "		$('#keyboard').append(String.fromCharCode(event.which));"
						+ "		if( event.which == 0 ) {"
						+ "			dealKeyPress=false;"
						+ "		}"
						+ "		if( event.which == 8 ) {"
						+ "			$('#keyboard').html($('#keyboard').html().slice(0,-2));"
						+ "		}"
						+ "		if( event.which == 13 ) {"
						+ "			var my_cmd = $('#keyboard').html();"
						+ "			$('#keyboard').html('');"
						+ "			if(!window.systemcall){"
						+ "				alert('查無指令:\\n'+my_cmd);"
						+ "			}"
						+ "		}"
						+ "});"
						+ "</script>"
						+ "智能雲端市場定位系統SBI系統狀態如下：<br>SBI-USER>：<span id='keyboard'　style='font-size:1.5em;'></span><a class='blink_me' style='font-size:1.5em;'>▍</a><br><br>");
				
				List<Map<String, String>> outcome =  new ArrayList<Map<String,String>>();//new CommonMethod(getServletConfig()).useMysqlDB("call sp_get_update_situation()",null);
				try{
					outcome = new CommonMethod(getServletConfig()).useMysqlDB("call sp_get_update_situation()",null);
				}catch(Exception e){
					ret += "<table class='excel-table'><tr><th>中文名稱</th><th>所存資料表明稱</th><th>資料總數</th><th>建立時間</th><th>最後修改時間(註:會因重整而遺失)</th><th>來源的最新資料</th></tr><tr><td colspan='6'>sql表格讀取失敗</td></tr></table>";
					logger.debug(e.toString());
				}
				if(outcome.size()>0){
					ret += "<table class='excel-table'><tr>";
					ret += "<th>中文名稱</th>";
					for (String key : outcome.get(0).keySet()) {  
					    ret += "<th>"
					    		+key.replace("TABLE_NAME","所存資料表明稱").replace("TABLE_ROWS","資料總數").replace("CREATE_TIME","建立時間").replace("UPDATE_TIME","最後修改時間(註:會因重整而遺失)")
					    		+"</th>";
					}
					
					ret += "<th>來源的最新資料</th>";
					ret += "</tr>";
				}
				for(int i=0;i<outcome.size();i++){
					String table_name = outcome.get(i).get("TABLE_NAME");
					ret += "<tr>";
					ret += "<td>"+getName_zhTW.get(table_name)+"</td>";
					for (Entry<String, String> entry : outcome.get(i).entrySet()) {  
						ret += "<td>"+entry.getValue()+"</td>";
					}  
					ret += "<td>"+getOpenDataTime.get(table_name)+"</td>";
					ret += "</tr>";
				}
				ret += "</table>";
				
				//以上資料庫狀態
				//以下專案狀態
				ret += "<a></a>";
				ret += "<table class='padding' style='float:left;'>";
				
				ret += "<tr><th colspan='2'>"+"↑上表出來意即順利連接資料庫成功"+"</th></tr>";
				//EX: ret += "<tr><td>"+"AAA"+"</td><td>"+"BBB"+"</td></tr>";
				//可能要稍微check一下正確性 因為eclipse和直接部屬不太一樣?
				ret += "<tr><td>專案部屬日期：</td><td>"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
						new File(
							request.getSession().getServletContext().getRealPath("")
						).lastModified()
					) + "</td></tr>";
				ret += "<tr><td>OS：</td><td>"
						+ (System.getProperty("os.name").toLowerCase().indexOf("windows")>-1?"Windows":"Linux")
						+ "〈"+System.getProperty("os.name")+"〉"
						+ "</td></tr>";
				ret += "<tr><td>主機：</td><td>"
						+ ipToWhom.get(getGlobalIP())
						+ "</td></tr>";
				
				ret += "<tr><td>主機IP&nbsp;(內,外,client)：</td><td>"
						+ "[ "+getLocalIP()+" , "+getGlobalIP()+" , "+new String(Base64.decodeBase64(CommonMethod.null2str(request.getSession().getAttribute("pSession"))),"UTF-8")+" ]"
						+ "</td></tr>";
				
				ret += "<tr><td>WS整體連接狀態(點擊看詳細)：</td><td><a href='#nothing' id='ws_status' onclick='$(\"#ws_table\").fadeToggle();'></a></td></tr>";
				ret += "<tr><td>Session狀態:</td><td>"+"<a href='#nothing' onclick='$(\"#session_table\").fadeToggle();'>Session</a>"+"</td></tr>";
				ret += "<tr><td>更新筆記:</td><td>"+"<a href='#nothing' onclick='$(\"#update_note\").fadeToggle();'>筆記</a>"+"</td></tr>";
				ret += "<tr><td>各頁面記錄:</td><td>"+"<a href='./viewStatus?action=viewPageStatus'>查閱記錄</a>&nbsp;(跳轉)"+"</td></tr>";
				ret += "<tr><td>讀取log:&nbsp;(至多1000行)</td><td>"+"<a href='./viewStatus?action=viewLog'>Log</a>&nbsp;(跳轉)"+"</td></tr>";
				ret += "<tr><td>使用軌跡:&nbsp;(至多1000行)</td><td>"+"<a href='./viewStatus?action=viewLog&type=trace'>使用軌跡</a>&nbsp;(跳轉)"+"</td></tr>";
				ret += "<tr><td>手動更新POIicon:</td><td>"+"<a href='#' onclick='if(confirm(\"確定更新POI嗎?\\n(將從 專案目錄 下複製至 POI存放目錄 下)\")){window.location.href = \"./viewStatus?action=updatePOIicon\"}'>更新</a>"+"</td></tr>";
				ret += "<tr><td>刪除Deployed_Over:&nbsp;</td><td>"+"<a href='./viewStatus?action=redeployee'>刪除後跳轉login</a>&nbsp;"+"</td></tr>";
				ret += "<tr><td>看目前config:&nbsp;</td><td>"+"<a href='./viewStatus?action=contextXML'>contextXML</a>&nbsp;"+"</td></tr>";
				//新功能加在這邊比較好
				ret += "</table>";
				
				ret += "<div id='update_note' style='display:none;float: right;max-width:50vw;'>" + updateNote().replaceAll("    ", "　").replaceAll("\\t", "\\\\t").replaceAll("\\n", "\\\\n") + "</div>" ;
				
				//已下讀ws狀態
				String wsPath = getServletConfig().getServletContext().getInitParameter("pythonwebservice");
				Boolean no_href = wsPath.indexOf("localhost:8099") != -1 ;
				
				String wsTable = "<table id='ws_table' class='padding' style='float:left;word-break:break-all;margin:20px;display:none;'><tr><th colspan='2'>Webservice狀態</th></tr>";
				String wsBuff = "";
				wsTable += "<tr><th colspan='2'>"+"200表正常, 500表正常但沒給參數, 404表連接失敗, -1表其它Error"+"</th></tr>";
				String[] webserviceName = {"sbiupload","selectregion","OpenData","persona","entrysrategy","finance","Webapi"};
				for(int i=0;i<webserviceName.length;i++){
					HttpResponse httpResponse;
					int responseCode = -1 ;
					try{
						httpResponse = HttpClientBuilder.create().build().execute(
							new HttpGet(wsPath+"/"+webserviceName[i]+"/")
						);
						responseCode = httpResponse.getStatusLine().getStatusCode();
					}catch(Exception e){
						logger.debug("wsTestConnectError: "+e.toString());
					}
					wsBuff += ("".equals(wsBuff)?"":", ")+responseCode;
					wsTable += "<tr><td>WS連接狀態("+pythonWebserviceName.get(webserviceName[i])+")：</td><td><a "+(no_href?"":"href='"+wsPath+"/"+webserviceName[i]+"/'")+">"+responseCode + "</a></td></tr>";
				}
				ret += wsTable+"</table>";
				ret += "<script>$('#ws_status').html('"+wsBuff+"')</script>";
				
				
				//已下讀Session狀態
				ret += "<table id='session_table' class='padding' style='float:left;word-break:break-all;margin:20px;display:none;'><tr><th colspan='2'>"+"Session狀態"+"</th></tr>"
						+ "<tr><td style='width: 90px;'>sessionID</td><td style='width: 500px;'>"+request.getSession().getAttribute("sessionID")+"</td></tr>"
						+ "<tr><td>group_id</td><td>"+request.getSession().getAttribute("group_id")+"</td></tr>"
						+ "<tr><td>group_id</td><td>"+request.getSession().getAttribute("group_name")+"</td></tr>"
						+ "<tr><td>user_id</td><td>"+request.getSession().getAttribute("user_id")+"</td></tr>"
						+ "<tr><td>user_name</td><td>"+request.getSession().getAttribute("user_name")+"</td></tr>"
						+ "<tr><td>role</td><td>"+request.getSession().getAttribute("role")+"</td></tr>"
						+ "<tr><td>privilege</td><td>"+request.getSession().getAttribute("privilege")+"</td></tr>"
						+ "<tr><td>token</td><td>"+request.getSession().getAttribute("token")+"</td></tr>"
						+ "<tr><td>checkcode</td><td>"+request.getSession().getAttribute("checkcode")+"</td></tr>"
						+ "<tr><td>menu</td><td><script>document.write(getMenuname(eval("+request.getSession().getAttribute("menu")+")))</script></td></tr>"
						+ "</table>";
				ret += "<a href='./login.jsp' style='position:fixed;bottom:20px;right:20px;padding:5px 8px;background-color:#ccc;border-radius:4px;border:1px solid #111;'>回到系統</a>";
				ret += ("</body></html>");
				
				response.getWriter().write(ret);
				
	    	}catch(Exception e){
	    		
	    		StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
	    		logger.debug("error:"+sw.toString());
				response.getWriter().write(
					"<div>Encountered Error: "+e.toString()+"<br>"
						+"<font style='color:red;'>"
						+sw.toString().replace("\r\n", "<br>")
									.replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
									.replace("(", "(<a href='#' style='color:blue;'>")
									.replace(")", "</a>)")
						+"</font></div><div><br>您或許可以嘗試　<a href='./viewStatus?action=redeployee'>點此刪除Deployed_Over</a>　並重新執行</div>"
				);
	    	}
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("viewLog")){
			String file_name = "";
			if(CommonMethod.null2str(request.getParameter("type")).equals("trace")){
				file_name = "/data/log/sbi/sbi-trace.log";
			}else{
				file_name = "/data/log/sbi/sbi.log";
			}
			String ret = "";
			ret += ("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
					+ "<title>檢視內容</title>"
					+ "<link rel=\"stylesheet\" href=\"http://www.a-ber.com.tw/sbi/css/styles_populationNew.css\">"
					+ "</head><body style='background-color:#efe;word-break: break-all;'>");
			ret += "<a href='./viewStatus' style='position:fixed;top:80px;right:80px;padding:5px 8px;background-color:#ccc;border-radius:4px;border:1px solid #111;'>返回</a>";
			List<String> list = new ArrayList<String>();
			BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(file_name), "UTF-8"));
			reader.readLine();
			String line = null; 
			while((line=reader.readLine())!=null){ 
				list.add(line);
			} 
		    for(int i = list.size()-1000>0?list.size()-1000:0 , len = list.size();i<len;++i){  
		    	ret += list.get(i)+"<br>"; 
		    }
			ret += ("<div id='fakeDB_end' style='height:0px; overflow:hidden'></div>"
					+ "<script>fakeDB_end.scrollIntoView();"
					+ "	document.addEventListener('DOMContentLoaded', function() {"
					+ "		document.getElementById('fakeDB_end').scrollIntoView();"
					+ "	}, false);"
					+ "//setTimeout(function(){  }, 1000);</script>"
					+ "</body></html>");
			response.getWriter().write(ret);
			reader.close();
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("viewPageStatus")){
			Map<String, String> getPageName_zhTW = new HashMap<>();
			getPageName_zhTW.put("POI.jsp", "商圈資訊");
			Map<String, String> getPageUsageDescription = new HashMap<>();
			getPageUsageDescription.put("POI.jsp", "此頁面暫時為 SBI 主要且唯一賣點<br>1. 呈現商圈及各店家位置<br>2. 店家分佈熱力圖<br>3. 依地址查詢位置<br>4. 環域分析");
			Map<String, String> getPageInsufficient = new HashMap<>();
			getPageInsufficient.put("POI.jsp", "1. POI,熱力圖 取當前頁面 移動後沒重整<br>2. 熱力圖功能不同類別覆蓋上去的狀況不正確<br>3. 因為POI一直心增功能，先做好的情境流程用到的存檔好像不完全<br>4. 地址須輸入很完整才有資料");
			Map<String, String> getPageAssignTime = new HashMap<>();
			getPageAssignTime.put("POI.jsp", "2016/07/01");
			Map<String, String> getPageKickoffTime = new HashMap<>();
			getPageKickoffTime.put("POI.jsp", "2016/09/21");
			
			String ret = "";
			ret += ("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
					+ "<title>檢視內容</title>"
					+ "<style>"
					+ "table.padding td, table.padding th{ padding:2px 8px;border-bottom:1px solid #888; }"
					+ "table.padding th[colspan='7']{font-weight:bolder;font-size:1.3em;color:#888;}"
					+ ".padding td:nth-child(2){text-align:center;}"
					+ "</style>"
					+ "</head><body style='background-color:#efe;word-break: break-all;'>");
			ret += "<a href='./viewStatus' style='position:fixed;top:80px;right:80px;padding:5px 8px;background-color:#ccc;border-radius:4px;border:1px solid #111;'>返回</a>";
			ret += "<table class='padding' style='float:left;'>";
			ret += "<tr><th colspan='7'>各頁面狀態</th></tr>";
			ret += "<tr><th>檔案名稱</th><th>中文名稱</th><th>用途敘述</th><th>尚待改進</th><th>指派時間</th><th>完成時間</th><th>最後更新日期</th></tr>";
			File projectDir = new File(request.getSession().getServletContext().getRealPath(""));
			String[] fileNames = projectDir.list();
			File temp = null;
			List<String> fileNameList = Arrays.asList(fileNames);
			Collections.sort(fileNameList,
		        new Comparator<String>() {
		            public int compare(String o1, String o2) {
		            	File file1 = new File(projectDir + File.separator + o1);
		            	File file2 = new File(projectDir + File.separator + o2);
		            	return file1.lastModified() == file2.lastModified() ? 0 : (file1.lastModified() < file2.lastModified() ? 1 : -1 ) ;
		            }
		        });
			int how_long=0;
			long one_day_millisecond = 86400*1000;
			for (int i = 0; i < fileNameList.size(); i++) {
				String filename = fileNameList.get(i);
				if(filename.indexOf("jsp")!=-1||filename.indexOf("html")!=-1){
					temp = new File(projectDir + File.separator + filename);
					if((new Date().getTime()-temp.lastModified())>1*one_day_millisecond && how_long==0){
						how_long++;
						ret += "<tr><th colspan='7'>↓↓↓超過&nbsp;"+"一天以上"+"&nbsp;未編輯↓↓↓</th></tr>";
					}
					if((new Date().getTime()-temp.lastModified())>3*one_day_millisecond && how_long==1){
						how_long++;
						ret += "<tr><th colspan='7'>↓↓↓超過&nbsp;"+"三天"+"&nbsp;未編輯↓↓↓</th></tr>";
					}
					if((new Date().getTime()-temp.lastModified())>7*one_day_millisecond && how_long==2){
						how_long++;
						ret += "<tr><th colspan='7'>↓↓↓超過&nbsp;"+"一週"+"&nbsp;未編輯↓↓↓</th></tr>";
					}
					if((new Date().getTime()-temp.lastModified())>30*one_day_millisecond && how_long==3){
						how_long++;
						ret += "<tr><th colspan='7'>↓↓↓超過&nbsp;"+"30天"+"&nbsp;未編輯↓↓↓</th></tr>";
					}
					if((new Date().getTime()-temp.lastModified())>90*one_day_millisecond && how_long==4){
						how_long++;
						ret += "<tr><th colspan='7'>↓↓↓超過&nbsp;"+"一季"+"&nbsp;未編輯↓↓↓</th></tr>";
					}
					if((new Date().getTime()-temp.lastModified())>365*one_day_millisecond && how_long==5){
						how_long++;
						ret += "<tr><th colspan='7'>↓↓↓超過&nbsp;"+"一年"+"&nbsp;未編輯↓↓↓</th></tr>";
					}
					
					ret += "<tr><td>"+temp.getName()+"&nbsp;<a href='"+temp.getName()+"'style='float:right'>前往</a></td>"
							+ "<td>"+CommonMethod.null2str(getPageName_zhTW.get(temp.getName()))+"</td>"
							+ "<td>"+CommonMethod.null2str(getPageUsageDescription.get(temp.getName()))+"</td>"
							+ "<td>"+CommonMethod.null2str(getPageInsufficient.get(temp.getName()))+"</td>"
							+ "<td>"+CommonMethod.null2str(getPageAssignTime.get(temp.getName()))+"</td>"
							+ "<td>"+CommonMethod.null2str(getPageKickoffTime.get(temp.getName()))+"</td>"
							+ "<td>"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(temp.lastModified())+"</td>"
							+ "</tr>";
				}
			}
			ret+="</table>";
			ret += ("<div id='fakeDB_end' style='height:0px; overflow:hidden'></div>"
					+ "<script>fakeDB_end.scrollIntoView();//setTimeout(function(){  }, 1000);</script>"
					+ "</body></html>");
			response.getWriter().write(ret);
			
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("updatePOIicon")){
			String ret = "";
			ret += ("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
					+ "<title>檢視內容</title>"
					+ "<link rel=\"stylesheet\" href=\"http://www.a-ber.com.tw/sbi/css/styles_populationNew.css\">"
					+ "</head><body style='background-color:#efe;'>");
			ret += "<a href='./viewStatus' style='position:fixed;top:80px;right:80px;padding:5px 8px;background-color:#ccc;border-radius:4px;border:1px solid #111;'>返回</a>"
					+"更新POI筆數: "+new POIiconCopy(getServletConfig()).copyFolder();
			ret += ("</body></html>");
			response.getWriter().write(ret);
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("getGlobalIP")){
			//只有這個是給前台呼的
			response.getWriter().write(getGlobalIP());
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("getCheckCode")){
			//給壓力測試用的Jmeter呼叫的
			response.getWriter().write("checkcode:'"+AssistMethod.getValidCode(request)+"'");
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("getChineseName")){
			//給壓力測試用的Jmeter呼叫的
			response.getWriter().write("chineseName:'"+AssistMethod.getChineseName()+"'");
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("generateEmail")){
			//給壓力測試用的Jmeter呼叫的
			response.getWriter().write("email:'"+AssistMethod.generateEmail()+"'");
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("setPrivateSession")){
			//記錄Debug用
			request.getSession().setAttribute("pSession", CommonMethod.null2str(request.getParameter("pSession")));
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("redeployee")){
			
			String sysString = StartupSetting.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String projectPath = sysString.split(sysString.split("/")[sysString.split("/").length - 1])[0];
			new File(projectPath + "Deployed_Over").delete();
			logger.debug("rm Deployed_Over than [TransToJSP]");
			response.sendRedirect("./login.jsp");
			
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("loggerDebug")){
			logger.debug("front-end-logger-req: "+CommonMethod.null2str(request.getParameter("action")).equals("msg"));
			
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("contextXML")){
			String msg = "\n";
			ServletContext context = getServletConfig().getServletContext();
			Enumeration<String> attributeNames = context.getInitParameterNames();
	        int i = 0;
	        while (attributeNames.hasMoreElements()) {
	            String nextElement = attributeNames.nextElement();
	            msg += (++i + ". " + nextElement+": ");
	            Object attribute = context.getInitParameter(nextElement);
	            msg += (attribute+"\n");
	        }
//	        ServletConfig config2 = getServletConfig();
//	        String name = config2.getServletName();
//	        msg += (name);
//	        Enumeration<String> names = config2.getInitParameterNames();
//	        i = 0;
//	        while (names.hasMoreElements()) {
//	            String nextElement = names.nextElement();
//	            msg += (++i + ":" + nextElement + ":");
//	            String initParameter = config2.getInitParameter(nextElement);
//	            msg += (initParameter);
//	        }
	        response.getWriter().write(msg.replace("\n","<br>").replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;"));
			logger.debug(msg);
		}else if(CommonMethod.null2str(request.getParameter("action")).equals("ToStartReport")){
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.MINUTE, 30);
			calendar.set(Calendar.SECOND, 0);

			Date date = calendar.getTime();
			if (date.before(new Date())) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			date = calendar.getTime();
			
			Map<String, String> contextParamMap = new HashMap<String, String>();
			ServletContext sc = getServletConfig().getServletContext();
			Enumeration<String> parameters = sc.getInitParameterNames();
			while (parameters.hasMoreElements()) {
				String parameter = (String) parameters.nextElement();
				contextParamMap.put(parameter, sc.getInitParameter(parameter));
			}
			
			new StartupSetting().submitReport("DEPLOY",contextParamMap);
			new Timer().schedule(new DailyCheckTimer(contextParamMap), date, 24* 60 * 60 * 1000);
			
		}
	}
	public class DailyCheckTimer extends TimerTask {
		Map<String, String> contextParamMap = null;
		public String deployedPath = ""; 
		public SimpleDateFormat dayFormat_m = null;
		public String uuid = "";
		public DailyCheckTimer(Map<String, String> contextParamMap) {
			this.contextParamMap = contextParamMap;
			String sysString = StartupSetting.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String projectPath = sysString.split(sysString.split("/")[sysString.split("/").length - 1])[0];
			this.deployedPath = projectPath + "Deployed_Over";
			this.dayFormat_m =  new SimpleDateFormat("yyyy/MM/dd HH:mm");

			this.uuid = this.dayFormat_m.format(new File(this.deployedPath).lastModified());
		}
		@Override
		public void run() {
			String current_ver = this.dayFormat_m.format(new File(this.deployedPath).lastModified());
			if(this.uuid.equals(current_ver)){
				new StartupSetting().submitReport("DailyCheck", this.contextParamMap);
			}else{
				logger.debug("Timer of "+this.uuid+" canceled.");
				this.cancel();
			}
		}
	}
	
	public static String getGlobalIP() {
		try {
			URL url = new URL("http://vl7.net/ip");
//			URL url = new URL("https://helloacm.com/api/what-is-my-ip-address/");
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection connection = null;
			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
				connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.142 Safari/535.19");
			} else {
				return "null";
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String urlString = "";
			String current;
			while ((current = in.readLine()) != null) {
				urlString += current;
			}
			return urlString.replaceAll("\"", "");
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("full-range-log",e);
			return "X.X.X.X";
		}
	}
	public static String getLocalIP() {
		String sIP = "";
		InetAddress ip = null;
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("windows")>-1) {
				ip = InetAddress.getLocalHost();
			}else {
				boolean bFindIP = false;
				Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
						.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					if (bFindIP) {
						break;
					}
					NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						ip = (InetAddress) ips.nextElement();
						if (ip.isSiteLocalAddress()
								&& !ip.isLoopbackAddress()
								&& ip.getHostAddress().indexOf(":") == -1) {
							bFindIP = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("full-range-log",e);
		}
		if (null != ip) {
			sIP = ip.getHostAddress();
		}
		return sIP;
	}
	public static String updateNote() {
		//Ben的純文字更新筆記
		return  "因為手動更新的資料太多 並沒有時間整進系統規劃中 所以必須留下紀錄以免消失<br>"
			+ "<br>"
			+ "目前所處理過的資料opendata<br>"
			+ "1.捷運人流<br>"
			+ "    手動從網頁下載csv檔案更新<br>"
			+ "    http://data.taipei/opendata/datalist/datasetMeta?oid=63f31c7e-7fc3-418b-bd82-b95158755b4d<br>"
			+ "2.未來三天天氣預報 <br>"
			+ "    無資料呈現 寫crontab自動去抓臺北市和新北市 離台北捷運有存在的21個區的資料<br>"
			+ "    http://opendata.cwb.gov.tw/index<br>"
			+ "3.歷史天氣資料<br>"
			+ "    手動去抓17個離捷運最近距離觀測站的html類型資料<br>"
			+ "    http://e-service.cwb.gov.tw/HistoryDataQuery/<br>"
			+ "4.交流道車流資訊<br>"
			+ "    寫crontab自動去抓前一天的交流道資料<br>"
			+ "    http://tisvcloud.freeway.gov.tw/history/TDCS/M03A<br>"
			+ "5.POI資料<br>"
			+ "    以後可能會以自動去爬這個取代 (後者有更新時間 前者介面較讓人信任)<br>"
			+ "    1.http://www.i-write.idv.tw/life/info/info.html <br>"
			+ "    2.http://www.319papago.idv.tw/lifeinfo/life-index.html<br>"
			+ "    <br>"
			+ "    目前只有四大超商的手動更新<br>"
			+ "    7-11: 假定所有門市都有ibon的話<br>"
			+ "        於頁面手動選23個縣市別 然後複製貼上執行這個的結果*23<br>"
			+ "            resultBen=[];<br>"
			+ "            $('#InquiryResule').find('tr').each(function(){<br>"
			+ "                resultBen.push(<br>"
			+ "                    'SEVEN\t'<br>"
			+ "                    +$(this).find('td:nth-child(1)').text().trim()+'\t'<br>"
			+ "                    +$(this).find('td:nth-child(2)').text().trim()+'\t'<br>"
			+ "                    +$(this).find('td:nth-child(3)').text().trim()+'\n');<br>"
			+ "            });<br>"
			+ "            resultBen.join('');<br>"
			+ "        http://www.ibon.com.tw/retail_inquiry.aspx#gsc.tab=0<br>"
			+ "        <br>"
			+ "    全家:  筠臻爬的 我還不知道確實的方法<br>"
			+ "        '全家'<br>"
			+ "        http://www.family.com.tw/marketing/inquiry.aspx<br>"
			+ "        <br>"
			+ "    OK-mart:<br>"
			+ "        進頁面直接點開始查詢 然後複製貼上執行這個的結果<br>"
			+ "        resultBen=[];<br>"
			+ "        $('#shopList ul li').each(function(){<br>"
			+ "            resultBen.push(<br>"
			+ "                'OK超商\t'<br>"
			+ "                +$(this).find('div').html().split(':showshop('')[1].split('','')[0]+'\t'<br>"
			+ "                +$(this).find('h2').text()+'\t'<br>"
			+ "                +$(this).find('span').text()+'\n');<br>"
			+ "        });<br>"
			+ "        resultBen.join('');<br>"
			+ "        http://www.okmart.com.tw/convenient_shopSearch<br>"
			+ "        <br>"
			+ "    Hi-life:<br>"
			+ "        進頁面輸入查詢('店') 然後複製貼上執行這個的結果<br>"
			+ "        resultBen=[];<br>"
			+ "        $('.searchResults table tbody tr').each(function(){<br>"
			+ "            resultBen.push(<br>"
			+ "                '萊爾富\t'<br>"
			+ "                +$(this).find('th:nth-child(1)').text().trim()+'\t'<br>"
			+ "                +$(this).find('th:nth-child(2)').text().trim()+'\t'<br>"
			+ "                +$(this).find('td:nth-child(3)').text().trim()+'\n');<br>"
			+ "        });<br>"
			+ "        resultBen.join('');<br>"
			+ "        http://www.hilife.com.tw/storeInquiry_name.aspx<br>"
			+ "    概略刪除DB中台灣的便利商店:<br>"
			+ "        DELETE FROM `tb_data_POI` <br>"
			+ "        WHERE subtype = '便利商店' <br>"
			+ "        AND lng BETWEEN 117.890375 AND 122.102875<br>"
			+ "        AND lat BETWEEN 21.824907 AND 26.877802<br>"
			+ "    用上面四種答案的地址丟tw.com.sbi.dataupdate.controller.GetLatLngFromAddress查完經緯度 丟進DB<br>"
			+ " 目前還有原本HTML介接SBI test.do 改log4j的方法<br>";     
		
	}
}
