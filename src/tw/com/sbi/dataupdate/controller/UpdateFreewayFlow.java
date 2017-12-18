/**
 * 20170817 Ben
 * 此class為交通部臺灣區國道高速公路局所提供之交流道資料
 * 區間檢查目前為手動沒有寫程式去detect 頁面在此 http://tisvcloud.freeway.gov.tw/history/TDCS/M03A/
 * "今日" 無資料區段2017/06/16 ~ 2017/0713 以前資料為Targz 以後資料為Csv
 */
package tw.com.sbi.dataupdate.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import tw.com.sbi.common.controller.CommonMethod;

public class UpdateFreewayFlow {
//	public static void main(){
//		不確定專案裡面寫main會不會被亂執行到 先不寫
//	}
	public static final String targz_from_time = "2014/01/01";
	public static final String targz_till_time = "2017/06/15";
	public static String csv_from_time = "";
	public static String csv_till_time = "";
	private String oriFolder ;
	private String transFolder ;
	private String destFolder ;
	private ServletConfig servletConfig;
	
	private static final Logger logger = LogManager.getLogger(UpdateFreewayFlow.class);
	public UpdateFreewayFlow(ServletConfig server_servletConfig) {
		oriFolder = server_servletConfig.getServletContext().getInitParameter("tmpDIR")+"/oriFolder/";
		transFolder = server_servletConfig.getServletContext().getInitParameter("tmpDIR")+"/transFolder/";
		destFolder = server_servletConfig.getServletContext().getInitParameter("tmpDIR")+"/destFolder/";
		servletConfig = server_servletConfig;
		
		UnzipFiles.createDirectory(oriFolder, null);
		UnzipFiles.createDirectory(transFolder, null);
		UnzipFiles.createDirectory(destFolder, null);
	}
	
	
	public void toUpdate(){
		//資料處理太耗時間 避免不小心執行到 先return
		//目前頂多改 每隔一小時抓一次最近兩個小時內資料
		//int consider_carefully=1;
		//if(consider_carefully==1)return;
		
		//這邊取得targz
		boolean targz_update=false;
		boolean csv_update=true;
		//這邊取得CSV
		
		new CommonMethod(servletConfig).useMysqlDBString("call sp_delete_flow_interchange_data_over15days()",null);
		logger.debug("指令: 下載高公局資料[ "+csv_from_time+" ~ "+csv_till_time+" ] 進度: 刪除逾15天之資料(0/4)");
		if(csv_update){
			try{
				Calendar current_time = Calendar.getInstance();
				current_time.setTime(new Date());
				
				current_time.add(Calendar.DATE, -1);
				csv_till_time=new SimpleDateFormat("yyyy/MM/dd").format(current_time.getTime());
				csv_from_time=new SimpleDateFormat("yyyy/MM/dd").format(current_time.getTime());
				
				
				List<String> list = getFileCsvUrl();
				
				logger.debug("指令: 下載高公局資料[ "+csv_from_time+" ~ "+csv_till_time+" ] 進度: 開始下載(1/4)");
				for(int i = 0; i < list.size(); i++)
				{
					if(DownloadFiles.isValidURL(list.get(i))){ 
						String filename = list.get(i).split("/")[list.get(i).split("/").length-1];
						DownloadFiles.downlodFileSuccess(list.get(i),oriFolder+filename);
					}
				}
				logger.debug("指令: 下載高公局資料[ "+csv_from_time+" ~ "+csv_till_time+" ] 進度: 下載完成(2/4)");
				List<String> exist_time = new ArrayList<String>();
				List<Map<String, String>> outcome = new CommonMethod(servletConfig).useMysqlDB("call sp_select_flow_interchange_exist_time_list()",null);
				for(int i = 0; i < outcome.size(); i++){
					for (String value : outcome.get(i).values()) {
						if(!Pattern.compile("^[-+]?\\d+$").matcher(value).matches()){
							exist_time.add(value);
						}
					}  
				}
				
				File folder = new File(oriFolder);
				String[] children = folder.list();
				JSONArray batchElement= new JSONArray();
				if (children == null) {
					return;
				} else {
					for (int i = 0; i < children.length; i++) {
						String filename = children[i];
						try{
							BufferedReader reader = new BufferedReader(new FileReader(folder.getAbsolutePath() + "/" + filename));
							reader.readLine();
							String line = null; 
							while((line=reader.readLine())!=null){
								String item[] = line.split(",");
								if(!exist_time.contains(item[0])){
									Map<String,String> vo = new LinkedHashMap<String,String>();
									vo.put("1", item[0]);
									vo.put("2", item[1]);
									vo.put("3", item[2]);
									vo.put("4", item[3]);
									vo.put("5", item[4]);
									batchElement.put(vo);
								}
							}
							reader.close();
						}catch(Exception e){
							e.printStackTrace();
							logger.debug("遍歷CSV檔案出錯:"+e.toString());
						}
					}
				}
				
				new CommonMethod(servletConfig).batchCMD("call sp_insert_data_interchange_data(?,?,?,?,?)",batchElement);
				logger.debug("指令: 下載高公局資料[ "+csv_from_time+" ~ "+csv_till_time+" ] 進度: 資料庫更新完成(3/4)");
				
				DownloadFiles.deleteFolder(oriFolder);
				logger.debug("指令: 下載高公局資料[ "+csv_from_time+" ~ "+csv_till_time+" ] 進度: 成功刪除暫存資料(4/4)");
			}catch(Exception e){
				e.printStackTrace();
				logger.debug("更新高公局國道流量資料失敗(toUpdate-CSV)");
			}
		}
		
		if(targz_update){
			try{
				List<String> list = getFileTargzUrl();
				logger.debug("指令: 下載高公局資料[ "+targz_from_time+" ~ "+targz_till_time+" ] 進度: 開始下載(0/4)");
				for(int i = 0; i < list.size(); i++)
				{
					if(DownloadFiles.isValidURL(list.get(i))){ 
						String filename = list.get(i).split("/")[list.get(i).split("/").length-1];
						if(DownloadFiles.downlodFileSuccess(list.get(i),oriFolder+filename)){
							UnzipFiles.unTarGz( new File(oriFolder + filename) , transFolder);
						}
					}
				}
				logger.debug("指令: 下載高公局資料[ "+targz_from_time+" ~ "+targz_till_time+" ] 進度: 下載完成(1/4)");
				
				DownloadFiles.collectAllFiles(transFolder, destFolder);
				logger.debug("指令: 下載高公局資料[ "+targz_from_time+" ~ "+targz_till_time+" ] 進度: 整合完成(2/4)");
				
				DownloadFiles.deleteFolder(oriFolder);
				DownloadFiles.deleteFolder(transFolder);
				logger.debug("指令: 下載高公局資料[ "+targz_from_time+" ~ "+targz_till_time+" ] 進度: 刪除轉換遺留檔案(3/4)");
				
				logger.debug("指令: 下載高公局資料[ "+targz_from_time+" ~ "+targz_till_time+" ] 進度: (暫無)整理成單一csv檔案(4/4)");
				
			}catch(Exception e){
				e.printStackTrace();
				logger.debug("更新高公局國道流量資料失敗(toUpdate-Targz)");
			}
		}
	}
	
	public static void combineInOneCsv(String ori_folder, String dest_csv) {
		File dir = new File(ori_folder);
		String[] csvFiles = dir.list();
		
		Arrays.sort(csvFiles);
		int i;
		
		try{
			File csv = new File(dest_csv);
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv,true));
			
			for(i=0;i<csvFiles.length;i++){
				BufferedReader reader = new BufferedReader(new FileReader(ori_folder+"/"+csvFiles[i]));
				reader.readLine();
				String line = null; 
				while((line=reader.readLine())!=null){ 
					bw.write("NULL,"+line); 
			        bw.newLine(); 
				} 
			}
			
			bw.close(); 
		}catch(Exception e){
			e.printStackTrace();
			logger.debug("更新高公局國道流量資料失敗(toUpdate-CSV)");
		}
	}
	
	
	public static List<String> targzUrlFormat(Date d){
		List<String> urlList = new ArrayList<String>();
		urlList.add("http://tisvcloud.freeway.gov.tw/history/TDCS/M03A/M03A_"+new SimpleDateFormat("yyyyMMdd").format(d)+".tar.gz");
		return urlList;
	}
	public static List<String> csvUrlFormat(Date d){
		List<String> urlList = new ArrayList<String>();
		String dayStr = new SimpleDateFormat("yyyyMMdd").format(d);
		int i,j;
		for(i=0;i<24;i++){
			for(j=0;j<60;j+=5){
				urlList.add(
					"http://tisvcloud.freeway.gov.tw/history/TDCS/M03A/"+dayStr+"/"+String.format("%02d", i)+"/TDCS_M03A_"+dayStr+"_"+String.format("%02d", i)+String.format("%02d", j)+"00.csv"
				);
			}
		}
		return urlList;
	}
	
	public static List<String> getFileCsvUrl(){
		String p_from_time = csv_from_time;
		String p_till_time = csv_till_time;
		
		List<String> urlList = new ArrayList<String>();
		SimpleDateFormat my_format = new SimpleDateFormat("yyyy/MM/dd");
		try{
			Calendar from_time = Calendar.getInstance();
			from_time.setTime(my_format.parse(p_from_time));
			Calendar till_time = Calendar.getInstance();
			till_time.setTime(my_format.parse(p_till_time));
			
			urlList.addAll(csvUrlFormat(from_time.getTime()));
			while(from_time.compareTo(till_time)<0){
				from_time.add(Calendar.DATE, 1);
				urlList.addAll(csvUrlFormat(from_time.getTime()));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.debug("更新高公局國道流量資料失敗(csvuUrl)");
		}
		return urlList ; 
	}
	
	public static List<String> getFileTargzUrl(){
		String p_from_time = targz_from_time;
		String p_till_time = targz_till_time;
		
		List<String> urlList = new ArrayList<String>();
		SimpleDateFormat my_format = new SimpleDateFormat("yyyy/MM/dd");
		try{
			Calendar from_time = Calendar.getInstance();
			from_time.setTime(my_format.parse(p_from_time));
			Calendar till_time = Calendar.getInstance();
			till_time.setTime(my_format.parse(p_till_time));
			
			urlList.addAll(targzUrlFormat(from_time.getTime()));
			
			while(from_time.compareTo(till_time)<0){
				from_time.add(Calendar.DATE, 1);
				urlList.addAll(targzUrlFormat(from_time.getTime()));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.debug("更新高公局國道流量資料失敗(取targzUrl)");
		}
		return urlList ; 
	}
	public static void dealAllFiles(String ori_folder, String dest_folder) {
		File dir = new File(ori_folder);
		if (dir.isFile()) {
			if(ori_folder.indexOf(".csv")!=-1){
				//(ori_folder, dest_folder);
			}
		}
		String[] children = dir.list();
		if (children == null) {
			return;
		} else {
			for (int i = 0; i < children.length; i++) {
				String filename = children[i];
				dealAllFiles(dir.getAbsolutePath() + "/" + filename, dest_folder);
			}
		}
	}
	
	public static String getOpenDataStatus(ServletConfig servletConfig) {
		String timeStr = "";
		
		String oriFolder = servletConfig.getServletContext().getInitParameter("tmpDIR") + "/oriFolder/";
		DownloadFiles.downlodFileSuccess(
				"http://tisvcloud.freeway.gov.tw/history/TDCS/M03A/",
				oriFolder + "/FreeWayFlowPage.html");
		BufferedReader reader = null;
		String buffer = ""; // 不到100K的檔案 無妨 要改以後再改
		try {

			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(oriFolder + "/FreeWayFlowPage.html"), "UTF-8")); // 指定讀取文件的編碼格式，以免出現中文亂碼

			String str = null;
			while ((str = reader.readLine()) != null) {
				buffer += str;
			}
			timeStr = buffer.split("Parent Directory")[1].split("<tr><td valign=\"top\"><a href=\"")[1].split("/\"><img src=\"/history/theme/icons/folder.png")[0].trim();
			reader.close();
			
			new File(oriFolder + "/FreeWayFlowPage.html").delete();
			
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			logger.debug("getOpenDataStatusErr: "+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("getOpenDataStatusErr: "+e.toString());
		}
		return timeStr;
	}
}
