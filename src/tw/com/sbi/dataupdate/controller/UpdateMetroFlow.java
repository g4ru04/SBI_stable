/**
 * 此捷運人流更新 只有getOpenDataStatus Ok 
 * 因為當初只是給我自己手動下載檔案對XXX.zip解壓縮出來的csv
 * 然後對csv檔處理 print出來  手動insert mysql而已
 * 
 * http://data.taipei/opendata/datalist/datasetMeta?oid=63f31c7e-7fc3-418b-bd82-b95158755b4d
 */

package tw.com.sbi.dataupdate.controller;

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateMetroFlow {
	// 捷運人流資料來源
	// http://data.taipei/opendata/datalist/datasetMeta/preview?id=63f31c7e-7fc3-418b-bd82-b95158755b4d&rid=5d3e8601-1214-4ba3-b079-c4b3ea6858c2
	private static final Logger logger = LogManager.getLogger(UpdateMetroFlow.class);
	public static String getOpenDataStatus(ServletConfig servletConfig) {
		String timeStr = "";
		String oriFolder = servletConfig.getServletContext().getInitParameter("tmpDIR") + "/oriFolder/";
		DownloadFiles.downlodFileSuccess(
				"http://data.taipei/opendata/datalist/datasetMeta?oid=63f31c7e-7fc3-418b-bd82-b95158755b4d",
				oriFolder + "/metroOpenDataPage.html");
		BufferedReader reader = null;
		String buffer = ""; // 不到100K的檔案 無妨 要改以後再改
		try {

			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(oriFolder + "/metroOpenDataPage.html"), "UTF-8")); // 指定讀取文件的編碼格式，以免出現中文亂碼

			String str = null;
			while ((str = reader.readLine()) != null) {
				buffer += str;
			}
			timeStr = buffer.split("收錄期間")[2].split("<td class=\"dataset-details\" property=\"rdf:value\">")[1].split("</td>")[0].trim();
			reader.close();
			
			new File(oriFolder + "/metroOpenDataPage.html").delete();
			
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			logger.debug("getOpenDataStatusErr: "+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("getOpenDataStatusErr: "+e.toString());
		}
		return timeStr;
	}

	public static void update(String[] args) {
		Map<String, String> getCode = new HashMap<>();
		getCode.put("動物園", "BR01");
		getCode.put("木柵", "BR02");
		getCode.put("萬芳社區", "BR03");
		getCode.put("萬芳醫院", "BR04");
		getCode.put("辛亥", "BR05");
		getCode.put("麟光", "BR06");
		getCode.put("六張犁", "BR07");
		getCode.put("科技大樓", "BR08");
		getCode.put("大安", "BR09");
		getCode.put("忠孝復興", "BR10");
		getCode.put("南京復興", "BR11");
		getCode.put("中山國中", "BR12");
		getCode.put("松山機場", "BR13");
		getCode.put("大直", "BR14");
		getCode.put("劍南路", "BR15");
		getCode.put("西湖", "BR16");
		getCode.put("港墘", "BR17");
		getCode.put("文德", "BR18");
		getCode.put("內湖", "BR19");
		getCode.put("大湖公園", "BR20");
		getCode.put("葫洲", "BR21");
		getCode.put("東湖", "BR22");
		getCode.put("南港軟體園區", "BR23");
		getCode.put("南港展覽館", "BR24");
		getCode.put("象山", "R02");
		getCode.put("台北101/世貿", "R03");
		getCode.put("信義安和", "R04");
		getCode.put("大安", "R05");
		getCode.put("大安森林公園", "R06");
		getCode.put("東門", "R07");
		getCode.put("中正紀念堂", "R08");
		getCode.put("台大醫院", "R09");
		getCode.put("台北車站", "R10");
		getCode.put("中山", "R11");
		getCode.put("雙連", "R12");
		getCode.put("民權西路", "R13");
		getCode.put("圓山", "R14");
		getCode.put("劍潭", "R15");
		getCode.put("士林", "R16");
		getCode.put("芝山", "R17");
		getCode.put("明德", "R18");
		getCode.put("石牌", "R19");
		getCode.put("唭哩岸", "R20");
		getCode.put("奇岩", "R21");
		getCode.put("北投", "R22");
		getCode.put("新北投", "R22A");
		getCode.put("復興崗", "R23");
		getCode.put("忠義", "R24");
		getCode.put("關渡", "R25");
		getCode.put("竹圍", "R26");
		getCode.put("紅樹林", "R27");
		getCode.put("淡水", "R28");
		getCode.put("新店", "G01");
		getCode.put("新店區公所", "G02");
		getCode.put("七張", "G03");
		getCode.put("小碧潭", "G03A");
		getCode.put("大坪林", "G04");
		getCode.put("景美", "G05");
		getCode.put("萬隆", "G06");
		getCode.put("公館", "G07");
		getCode.put("台電大樓", "G08");
		getCode.put("古亭", "G09");
		getCode.put("中正紀念堂", "G10");
		getCode.put("小南門", "G11");
		getCode.put("西門", "G12");
		getCode.put("北門", "G13");
		getCode.put("中山", "G14");
		getCode.put("松江南京", "G15");
		getCode.put("南京復興", "G16");
		getCode.put("台北小巨蛋", "G17");
		getCode.put("南京三民", "G18");
		getCode.put("松山", "G19");
		getCode.put("南勢角", "O01");
		getCode.put("景安", "O02");
		getCode.put("永安市場", "O03");
		getCode.put("頂溪", "O04");
		getCode.put("古亭", "O05");
		getCode.put("東門", "O06");
		getCode.put("忠孝新生", "O07");
		getCode.put("松江南京", "O08");
		getCode.put("行天宮", "O09");
		getCode.put("中山國小", "O10");
		getCode.put("民權西路", "O11");
		getCode.put("大橋頭站", "O12");
		getCode.put("台北橋", "O13");
		getCode.put("菜寮", "O14");
		getCode.put("三重", "O15");
		getCode.put("先嗇宮", "O16");
		getCode.put("頭前庄", "O17");
		getCode.put("新莊", "O18");
		getCode.put("輔大", "O19");
		getCode.put("丹鳳", "O20");
		getCode.put("迴龍", "O21");
		getCode.put("三重國小", "O50");
		getCode.put("三和國中", "O51");
		getCode.put("徐匯中學", "O52");
		getCode.put("三民高中", "O53");
		getCode.put("蘆洲", "O54");
		getCode.put("頂埔", "BL01");
		getCode.put("永寧", "BL02");
		getCode.put("土城", "BL03");
		getCode.put("海山", "BL04");
		getCode.put("亞東醫院", "BL05");
		getCode.put("府中", "BL06");
		getCode.put("板橋", "BL07");
		getCode.put("新埔", "BL08");
		getCode.put("江子翠", "BL09");
		getCode.put("龍山寺", "BL10");
		getCode.put("西門", "BL11");
		getCode.put("台北車站", "BL12");
		getCode.put("善導寺", "BL13");
		getCode.put("忠孝新生", "BL14");
		getCode.put("忠孝復興", "BL15");
		getCode.put("忠孝敦化", "BL16");
		getCode.put("國父紀念館", "BL17");
		getCode.put("市政府", "BL18");
		getCode.put("永春", "BL19");
		getCode.put("後山埤", "BL20");
		getCode.put("昆陽", "BL21");
		getCode.put("南港", "BL22");
		getCode.put("南港展覽館", "BL23");
		Map<String, Map<String, Map<String, Integer>>> whole_map = new HashMap<>();
		// System.out.println(getCode.get("大安"));

		try {
			// FileReader fr=new FileReader("metro_csv.txt");
			// BufferedReader br=new BufferedReader(fr);

			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("metro_csv.txt"), "UTF-8"));
			String day_tmp = "";
			String line;
			while ((line = br.readLine()) != null) {

				if (line.length() > 3) {
					insert_map(line, whole_map, getCode);

					if (!day_tmp.equals(line.split(",")[0])) {
						day_tmp = line.split(",")[0];
						// System.out.println(line);
						// System.out.println("=============="+day_tmp+"==============");
					}
				}
				// System.out.println(new
				// String(line.getBytes("UTF-8"),"UTF-8"));
			}

		} catch (IOException e) {
//			System.out.println(e);
			logger.debug("full-range-log",e);
		}
		// 輸出方法1
		// for (Map.Entry<String, Map<String, Map<String, Integer>>> day_entry :
		// whole_map.entrySet()) {
		//
		// for (Map.Entry<String, Map<String, Integer>> hour_entry :
		// day_entry.getValue().entrySet()) {
		// System.out.print(day_entry.getKey()+","+hour_entry.getKey()+",");
		// Map<String, Integer> flows = hour_entry.getValue();
		// for (Map.Entry<String, Integer> station_entry :
		// hour_entry.getValue().entrySet()) {
		// System.out.print("["+station_entry.getKey()+","+station_entry.getValue()+"]");
		// }
		// System.out.println("");
		// }
		// }

		// 輸出方法2 手寫有序
		int i, j;
		String[] days = { "2017-06-01", "2017-06-02", "2017-06-03", "2017-06-04", "2017-06-05", "2017-06-06",
				"2017-06-07", "2017-06-08", "2017-06-09", "2017-06-10", "2017-06-11", "2017-06-12", "2017-06-13",
				"2017-06-14", "2017-06-15", "2017-06-16", "2017-06-17", "2017-06-18", "2017-06-19", "2017-06-20",
				"2017-06-21", "2017-06-22", "2017-06-23", "2017-06-24", "2017-06-25", "2017-06-26", "2017-06-27",
				"2017-06-28", "2017-06-29", "2017-06-30" };
		String[] hours = { "00", "01", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
				"18", "19", "20", "21", "22", "23" };

		for (i = 0; i < days.length; i++) {
			Map<String, Map<String, Integer>> day_map = whole_map.get(days[i]);
			for (j = 0; j < hours.length; j++) {
				Map<String, Integer> flows = day_map.get(hours[j]);
				// 名字重複code只有一個的有
				// [中山,中正紀念堂,南京復興,南港展覽館,古亭,台北車站,大安,忠孝復興,忠孝新生,東門,松江南京,民權西路,西門]
				System.out.println((i * hours.length + j + 1) + "," + days[i] + "," + hours[j] + ","
						+ flows.get("BR01_in") + "," + flows.get("BR01_out") + "," + flows.get("BR02_in") + ","
						+ flows.get("BR02_out") + "," + flows.get("BR03_in") + "," + flows.get("BR03_out") + ","
						+ flows.get("BR04_in") + "," + flows.get("BR04_out") + "," + flows.get("BR05_in") + ","
						+ flows.get("BR05_out") + "," + flows.get("BR06_in") + "," + flows.get("BR06_out") + ","
						+ flows.get("BR07_in") + "," + flows.get("BR07_out") + "," + flows.get("BR08_in") + ","
						+ flows.get("BR08_out") + "," + flows.get("R05_in") + "," + flows.get("R05_out") + ","
						+ flows.get("BL15_in") + "," + flows.get("BL15_out") + "," + flows.get("G16_in") + ","
						+ flows.get("G16_out") + "," + flows.get("BR12_in") + "," + flows.get("BR12_out") + ","
						+ flows.get("BR13_in") + "," + flows.get("BR13_out") + "," + flows.get("BR14_in") + ","
						+ flows.get("BR14_out") + "," + flows.get("BR15_in") + "," + flows.get("BR15_out") + ","
						+ flows.get("BR16_in") + "," + flows.get("BR16_out") + "," + flows.get("BR17_in") + ","
						+ flows.get("BR17_out") + "," + flows.get("BR18_in") + "," + flows.get("BR18_out") + ","
						+ flows.get("BR19_in") + "," + flows.get("BR19_out") + "," + flows.get("BR20_in") + ","
						+ flows.get("BR20_out") + "," + flows.get("BR21_in") + "," + flows.get("BR21_out") + ","
						+ flows.get("BR22_in") + "," + flows.get("BR22_out") + "," + flows.get("BR23_in") + ","
						+ flows.get("BR23_out") + "," + flows.get("BL23_in") + "," + flows.get("BL23_out") + ","
						+ flows.get("R02_in") + "," + flows.get("R02_out") + "," + flows.get("R03_in") + ","
						+ flows.get("R03_out") + "," + flows.get("R04_in") + "," + flows.get("R04_out") + ","
						+ flows.get("R05_in") + "," + flows.get("R05_out") + "," + flows.get("R06_in") + ","
						+ flows.get("R06_out") + "," + flows.get("O06_in") + "," + flows.get("O06_out") + ","
						+ flows.get("G10_in") + "," + flows.get("G10_out") + "," + flows.get("R09_in") + ","
						+ flows.get("R09_out") + "," + flows.get("BL12_in") + "," + flows.get("BL12_out") + ","
						+ flows.get("G14_in") + "," + flows.get("G14_out") + "," + flows.get("R12_in") + ","
						+ flows.get("R12_out") + "," + flows.get("O11_in") + "," + flows.get("O11_out") + ","
						+ flows.get("R14_in") + "," + flows.get("R14_out") + "," + flows.get("R15_in") + ","
						+ flows.get("R15_out") + "," + flows.get("R16_in") + "," + flows.get("R16_out") + ","
						+ flows.get("R17_in") + "," + flows.get("R17_out") + "," + flows.get("R18_in") + ","
						+ flows.get("R18_out") + "," + flows.get("R19_in") + "," + flows.get("R19_out") + ","
						+ flows.get("R20_in") + "," + flows.get("R20_out") + "," + flows.get("R21_in") + ","
						+ flows.get("R21_out") + "," + flows.get("R22_in") + "," + flows.get("R22_out") + ","
						+ flows.get("R22A_in") + "," + flows.get("R22A_out") + "," + flows.get("R23_in") + ","
						+ flows.get("R23_out") + "," + flows.get("R24_in") + "," + flows.get("R24_out") + ","
						+ flows.get("R25_in") + "," + flows.get("R25_out") + "," + flows.get("R26_in") + ","
						+ flows.get("R26_out") + "," + flows.get("R27_in") + "," + flows.get("R27_out") + ","
						+ flows.get("R28_in") + "," + flows.get("R28_out") + "," + flows.get("G01_in") + ","
						+ flows.get("G01_out") + "," + flows.get("G02_in") + "," + flows.get("G02_out") + ","
						+ flows.get("G03_in") + "," + flows.get("G03_out") + "," + flows.get("G03A_in") + ","
						+ flows.get("G03A_out") + "," + flows.get("G04_in") + "," + flows.get("G04_out") + ","
						+ flows.get("G05_in") + "," + flows.get("G05_out") + "," + flows.get("G06_in") + ","
						+ flows.get("G06_out") + "," + flows.get("G07_in") + "," + flows.get("G07_out") + ","
						+ flows.get("G08_in") + "," + flows.get("G08_out") + "," + flows.get("O05_in") + ","
						+ flows.get("O05_out") + "," + flows.get("G10_in") + "," + flows.get("G10_out") + ","
						+ flows.get("G11_in") + "," + flows.get("G11_out") + "," + flows.get("BL11_in") + ","
						+ flows.get("BL11_out") + "," + flows.get("G13_in") + "," + flows.get("G13_out") + ","
						+ flows.get("G14_in") + "," + flows.get("G14_out") + "," + flows.get("O08_in") + ","
						+ flows.get("O08_out") + "," + flows.get("G16_in") + "," + flows.get("G16_out") + ","
						+ flows.get("G17_in") + "," + flows.get("G17_out") + "," + flows.get("G18_in") + ","
						+ flows.get("G18_out") + "," + flows.get("G19_in") + "," + flows.get("G19_out") + ","
						+ flows.get("O01_in") + "," + flows.get("O01_out") + "," + flows.get("O02_in") + ","
						+ flows.get("O02_out") + "," + flows.get("O03_in") + "," + flows.get("O03_out") + ","
						+ flows.get("O04_in") + "," + flows.get("O04_out") + "," + flows.get("O05_in") + ","
						+ flows.get("O05_out") + "," + flows.get("O06_in") + "," + flows.get("O06_out") + ","
						+ flows.get("BL14_in") + "," + flows.get("BL14_out") + "," + flows.get("O08_in") + ","
						+ flows.get("O08_out") + "," + flows.get("O09_in") + "," + flows.get("O09_out") + ","
						+ flows.get("O10_in") + "," + flows.get("O10_out") + "," + flows.get("O11_in") + ","
						+ flows.get("O11_out") + "," + flows.get("O12_in") + "," + flows.get("O12_out") + ","
						+ flows.get("O13_in") + "," + flows.get("O13_out") + "," + flows.get("O14_in") + ","
						+ flows.get("O14_out") + "," + flows.get("O15_in") + "," + flows.get("O15_out") + ","
						+ flows.get("O16_in") + "," + flows.get("O16_out") + "," + flows.get("O17_in") + ","
						+ flows.get("O17_out") + "," + flows.get("O18_in") + "," + flows.get("O18_out") + ","
						+ flows.get("O19_in") + "," + flows.get("O19_out") + "," + flows.get("O20_in") + ","
						+ flows.get("O20_out") + "," + flows.get("O21_in") + "," + flows.get("O21_out") + ","
						+ flows.get("O50_in") + "," + flows.get("O50_out") + "," + flows.get("O51_in") + ","
						+ flows.get("O51_out") + "," + flows.get("O52_in") + "," + flows.get("O52_out") + ","
						+ flows.get("O53_in") + "," + flows.get("O53_out") + "," + flows.get("O54_in") + ","
						+ flows.get("O54_out") + "," + flows.get("BL01_in") + "," + flows.get("BL01_out") + ","
						+ flows.get("BL02_in") + "," + flows.get("BL02_out") + "," + flows.get("BL03_in") + ","
						+ flows.get("BL03_out") + "," + flows.get("BL04_in") + "," + flows.get("BL04_out") + ","
						+ flows.get("BL05_in") + "," + flows.get("BL05_out") + "," + flows.get("BL06_in") + ","
						+ flows.get("BL06_out") + "," + flows.get("BL07_in") + "," + flows.get("BL07_out") + ","
						+ flows.get("BL08_in") + "," + flows.get("BL08_out") + "," + flows.get("BL09_in") + ","
						+ flows.get("BL09_out") + "," + flows.get("BL10_in") + "," + flows.get("BL10_out") + ","
						+ flows.get("BL11_in") + "," + flows.get("BL11_out") + "," + flows.get("BL12_in") + ","
						+ flows.get("BL12_out") + "," + flows.get("BL13_in") + "," + flows.get("BL13_out") + ","
						+ flows.get("BL14_in") + "," + flows.get("BL14_out") + "," + flows.get("BL15_in") + ","
						+ flows.get("BL15_out") + "," + flows.get("BL16_in") + "," + flows.get("BL16_out") + ","
						+ flows.get("BL17_in") + "," + flows.get("BL17_out") + "," + flows.get("BL18_in") + ","
						+ flows.get("BL18_out") + "," + flows.get("BL19_in") + "," + flows.get("BL19_out") + ","
						+ flows.get("BL20_in") + "," + flows.get("BL20_out") + "," + flows.get("BL21_in") + ","
						+ flows.get("BL21_out") + "," + flows.get("BL22_in") + "," + flows.get("BL22_out") + ","
						+ flows.get("BL23_in") + "," + flows.get("BL23_out"));
			}
		}

		return;
	}

	public static void insert_map(String str, Map<String, Map<String, Map<String, Integer>>> whole_map,
			Map<String, String> getCode) {
		String[] data_split = str.split(",");

		Map<String, Map<String, Integer>> day_map = whole_map.get(data_split[0]);
		if (day_map == null) {
			day_map = new HashMap<>();
			whole_map.put(data_split[0], day_map);
		}

		Map<String, Integer> hour_map = day_map.get(data_split[1]);
		if (hour_map == null) {
			hour_map = new HashMap<>();
			day_map.put(data_split[1], hour_map);
		}
		Integer in_flow = hour_map.get(getCode.get(data_split[2]) + "_in");
		Integer out_flow = hour_map.get(getCode.get(data_split[3]) + "_out");
		Integer move = Integer.valueOf(data_split[4]);

		in_flow = in_flow == null ? move : in_flow + move;
		out_flow = out_flow == null ? move : out_flow + move;

		hour_map.put((getCode.get(data_split[2]) + "_in"), in_flow);
		hour_map.put((getCode.get(data_split[3]) + "_out"), out_flow);
		// 兩種遍歷方法
		// for (Map.Entry<String, Integer> entry : hour_map.entrySet()) {
		// System.out.println("Key = " + entry.getKey() + ", Value = " +
		// entry.getValue());
		// }
		// for (Integer value : hour_map.values()) {
		//
		// System.out.print(" "+ value+" ");
		//
		// }
	}
}
