package tw.com.sbi.dataupdate.controller;

/** 
 * 有空再分出去
 * 跟其他update一樣 
 * 是個工具 但不屬於SBI
 */

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class GetLatLngFromAddress {
	private static final Logger logger = LogManager.getLogger(GetLatLngFromAddress.class);
//	public static String inputFile = "C:/Users/10506002/Desktop/convienien.txt";
	public static String inputFile = "C:/Users/10506002/Desktop/exception.txt";
//	public static String outputFile = "C:/Users/10506002/Desktop/dest.txt";
	public static String outputFile = "C:/Users/10506002/Desktop/dest2.txt";
	
	public static String errorFile = "C:/Users/10506002/Desktop/error.txt";

	public static String model = "INSERT INTO tb_data_POI(poi_id, type, subtype, name, address, BD, lng, lat, icon, memo, reserved) VALUES (NULL,'##type##','便利商店','##name##','##address##','','##lng##','##lat##','##icon##','','');\r\n";
	
	public static void main(String[] args) {
		int i=0;
		try { 
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
			BufferedWriter errorOut =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errorFile), "UTF-8"));
			
			InputStreamReader isr = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(isr);
			reader.readLine();
			String line = null; 
			while((line=reader.readLine())!=null){ 
				List<String> dataItem = new ArrayList<String>();
				String address = "";
				String item[] = line.split("\t");
				for(int j=0;j<item.length;j++){
					dataItem.add(item[j]);
					address = item[j];
				}
				
				String outputLine = "";
				
				try{
					
					String url = "https://maps.googleapis.com/maps/api/geocode/json"
								+"?key="+apiKey.get(i%30)
								+"&address="+java.net.URLEncoder.encode(address.split("號")[0]+"號","UTF-8")
								+"&sensor=true";
					InputStream in = new URL(url).openStream();
					String buf = "";
					int k;
					char c;
					while((k=in.read())!=-1){c=(char)k;buf+=c;}
					in.close();
					
					JSONObject element = new JSONObject(buf);
					JSONObject location = element.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
					dataItem.add(location.getDouble("lat")+"");
					dataItem.add(location.getDouble("lng")+"");
					if("OK超商".equals(dataItem.get(0))){
						dataItem.add("ok.png");
					}else if("SEVEN".equals(dataItem.get(0))){
						dataItem.add("7-seven.png");
					}else if("全家".equals(dataItem.get(0))){
						dataItem.add("familymart.png");
					}else if("萊爾富".equals(dataItem.get(0))){
						dataItem.add("hilife.png");
					}else{
						dataItem.add("");
					}
					
					
					
					outputLine = model.replace("##type##", dataItem.get(0))
										.replace("##name##", dataItem.get(2))
										.replace("##address##", dataItem.get(3))
										.replace("##lng##", dataItem.get(4))
										.replace("##lat##", dataItem.get(5))
										.replace("##icon##", dataItem.get(6));
					
//					for(int j=0;j<dataItem.size();j++){
//						
//						outputLine +=dataItem.get(j);
//						outputLine += j==dataItem.size()-1 ?"\r\n":"\t";
//						
//					}
					out.write(outputLine);
					
				} catch (Exception e) {
					errorOut.write(line+"\r\n");
					logger.debug("notfound: "+address);
					e.printStackTrace(); 
				} 

				i++;
//				if(i>30)break;
				if(i%500==0){System.out.println(i);}
			} 
			errorOut.close();
			out.close();
			logger.debug(i); 
		} catch (Exception e) { 
			e.printStackTrace();
			logger.debug("full-range-log",e);
		} 
	}
	public static List<String> apiKey = Arrays.asList(
		"AIzaSyDc2oSzYl-UJ6brhxL3-BoNPvl3nbjNogk",
		"AIzaSyDdcK3WRL1YWAVnxjvY4zIBOo55nQlQIB8",
		"AIzaSyBN7PT_EZthT269xBDu0hDZRUeF0ejwN6Q",
		"AIzaSyDxMwdHSAOu1EX7g956_fgfu4BVLOqWrOY",
		"AIzaSyDEmNANk1de-xssF5LE0ydJZA7iX33VZaU",
		"AIzaSyCXReRl759MVd8Qu8535_dep6s1Pa1wgJw",
		"AIzaSyCCba840R44yvznDTMouSyvdVpz5Z3ZI90",
		"AIzaSyAKAYnrb6Ru6lFpEvxse4wsFYTlXbgHggA",
		"AIzaSyDu9qLpw-J9RPSQzs14CBZkLwWCCoBc69A",
		"AIzaSyBgfC4pegNOjzJsyyDTqy31BQiYAcJlrKM",
		"AIzaSyAc0jMzr6s7ZH9jVbtqQioupPVqE6Jq5WI",
		"AIzaSyCebqEmmXxl-7I5wAlQK6Zw2hEi7hB5ScU",
		"AIzaSyAKZ9TvLxEVfQMyMtj92VCr29ANUepx000",
		"AIzaSyAOYDN9qMW9wdowvPtuAJxXb8k5nAo6pu4",
		"AIzaSyCerqMwkFQBeWgMR_08sBGsPcY_WQqmgxk",
		"AIzaSyDCWGjRLxMTetFGtreoCWvJuOwGcObS_-8",
		"AIzaSyA_2mmh0xrcnEeHpD63NLBkl8jvCy046bQ",
		"AIzaSyC0o-S5gQDA4HSK19SFFSSwp68j5Of3gAc",
		"AIzaSyD04YnoAX3c2rZAn45oPH7QMPjXiZGleHA",
		"AIzaSyCSvCF7GBzTA_iG3k_1iYGe6BDTvrKGNfw",
		"AIzaSyBW8il92lnY-OExDq0i96yTyX47u2AbJwo",
		"AIzaSyBtULNchG1u7yW-mjqGuQpyk8caN9hUZ5w",
		"AIzaSyDRLosPqV8tDB-MF9junhYZNY_sqelq6Ac",
		"AIzaSyBC4hUHQU5Vl4bIIligqvui0bJO57N5dTM",
		"AIzaSyBzu50QSksJLP3T9uDbvtiSQv07_HAL1Vw",
		"AIzaSyB9ta1XyxMRNmgWFWjtHM_px6uaqVsphgI",
		"AIzaSyB-6fPSSjQab8LNkcAG8cM-0UyJJRr71sM",
		"AIzaSyAmtQKJ684Qrj8jhHp_KStYw3TYbZfW2SE",
		"AIzaSyApL0FSbOebC8gQ1xH8YExPG96SQ1x3AiE",
		"AIzaSyBwyQy7E1U4QGC0yQTDcpQm41Cr0sXVZ2U",
		"AIzaSyAXcR8d_yvVYB0XkeEYwerroCzFyJZBKvE",
		"AIzaSyA8jWb-0eAtIJeY_Fea8pfnPRn-uehU0wk",
		"AIzaSyCW_2y57KFirUOraQcxYsfGDONXzaizP8E",
		"AIzaSyC4XnJtVzo70MKP_ccYfZBTI8SiS2jqt5s",
		"AIzaSyCpYPWqgh14hz-zWxaa3SK1bUi-osK0bkk",
		"AIzaSyCkf_RLlFG-WXTurGgeEa-N5SH1mo0Ki3I"
	);
}
