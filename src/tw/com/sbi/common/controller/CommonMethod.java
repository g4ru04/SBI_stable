/**
 * 20170802 Ben
 * If you want to use it, Follow me~~~
 * 
 * import tw.com.sbi.common.controller.CommonMethod; 
 * 
 * CommonMethod.cleanSQLConnection(con, pstmt, rs);
 * 
 * xxx = CommonMethod.null2str(str);
 * 
 * xxx = CommonMethod.JSONstringify(obj);
 * 
 ** JSONArray requestVO = new JSONArray();
 ** requestVO.put("time");
 ** String outcome = new CommonMethod(getServletConfig()).useMysqlDBString("call sp_select_invoice_dimension(?)",requestVO);
 * request null -> JSONObject.NULL
 * 
 * new CommonMethod(getServletConfig()).useMysqlDB("call sp_selectall_menu()",null);
 * 
 * Map<String,String> map = CommonMethod.requestParameter(request);
 * 
 */
//還缺的method
//統一讀 GET POST parameter的參數 [因:讀參數 null2Str logger 步驟不算少 感覺可統一可不統一]

package tw.com.sbi.common.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

public class CommonMethod {
	private static Logger logger = LogManager.getLogger(CommonMethod.class);
	private String dbURL ;
	private String dbURLbatch ;
	private String dbUserName ;
	private String dbPassword ;
	private int batchCount = 0 ;
	public int restrictSQL = 1 ;
	
	public CommonMethod(ServletConfig servletConfig) {
		dbURL = servletConfig.getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		dbURLbatch = servletConfig.getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false&useServerPrepStmts=false&rewriteBatchedStatements=true";
		dbUserName = servletConfig.getServletContext().getInitParameter("dbUserName");
		dbPassword = servletConfig.getServletContext().getInitParameter("dbPassword");

	}
	
	public CommonMethod(String dbURL,String dbUserName,String dbPassword) {
		this.dbURL = dbURL
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		this.dbURLbatch = dbURL
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false&useServerPrepStmts=false&rewriteBatchedStatements=true";
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;

	}

	public static void cleanSQLConnection( Connection con , PreparedStatement pstmt , ResultSet rs ){
		// Clean up JDBC resources
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException se) {
				se.printStackTrace(System.err);
				logger.debug("full-range-log",se);
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException se) {
				se.printStackTrace(System.err);
				logger.debug("full-range-log",se);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
				e.printStackTrace(System.err);
				logger.debug("full-range-log",e);
			}
		}
	}

	
	public static String null2str(Object object) {
		if("null".equals(object)){
			object = null;
		}
		return object == null ? "" : object.toString().trim();
	}
	
	public static String JSONStringify(Object object){
		Gson gson = new Gson();
		return  gson.toJson(object);
	}
	
	//有槓掉的地方都是 
	// 1. JSONArray改成List<>
	// 2. JsonObject改成HashMap
	//	因為 JSONArray 多出myArrayList 不喜歡 可是比較想用它 
	//     JSONObject 同理多出map
	
	//有兩個地方需要改寫 改天有空再寫
	//1. 或許不適用update的request
	//2. 沒有answer (概念同1,也可能該從sp下手)
	//3. 肯定只有一個list的結果可能可以寫另一個func直接回傳 List<String>
	//4. 還有像 sp_checkuser 那種 parameter out的sp 可能需要另寫一個 或者通用
	public List<Map<String,String>> useMysqlDB(String sp,JSONArray requestVO){
//	public JSONArray useMysqlDB(String sp,JSONArray requestVO){
		Date begin=new Date();
		List<String> requestVO_list = new ArrayList<String>();
		for (int i=0; requestVO!=null && i<requestVO.length(); i++) {
			requestVO_list.add( requestVO.getString(i) );
		}
		String para = JSONStringify(requestVO_list);
		para = para.length()>1000?para.substring(0, 1000)+"... ":para;
		
//		logger.debug("[ "+ex_for_trace.getStackTrace()[trace_lvl].getFileName()+":"+ex_for_trace.getStackTrace()[trace_lvl].getLineNumber()+" ]"+
		logger_debug("CommonMethod-useMysqlDB: "+ sp + " -> para: " + para );
		
		//是否該檔一下不是call sp的指令
		if(!sp.contains("call") && restrictSQL==1){
			logger.debug("Command won't be executed without sp.");
			return null;
		}
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		Map<String,String> vo = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
			pstmt = con.prepareStatement(sp);
			for( int i=0 ; requestVO!=null && i<requestVO.length() ; i++ ){
				pstmt.setString((i+1), requestVO.isNull(i)?null:requestVO.getString(i));
			}
			
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (columnCount!=0 && rs.next()) {
				vo = new LinkedHashMap<String,String>();
				for (int i = 1; i <= columnCount; i++ ) {
					vo.put(null2str(rsmd.getColumnName(i)),null2str(rs.getString(i)));
				}
				list.add(vo);
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException("A database error occured. " + cnfe.getMessage());
		} finally {
			CommonMethod.cleanSQLConnection(con, pstmt, rs);
		}
		
		Date end = new Date();
		long cost_time = end.getTime()-begin.getTime();
		if(list.size()>0){
//			logger.debug("[ "+ex_for_trace.getStackTrace()[trace_lvl].getFileName()+":"+ex_for_trace.getStackTrace()[trace_lvl].getLineNumber()+" ]"
			logger_debug("CommonMethod-useMysqlDB: "+(cost_time>3000?"cost "+cost_time+" ms to ":"")+"return " + lotsOfObjectFormat(list) );
		}else{
			logger_debug("Return nothing.");
		}
		return list;
	}
	
	public static String lotsOfObjectFormat(Object obj){
		try{
			List<?> list = new ArrayList<String>();
			
			if("JSONArray".equals(obj.getClass().getSimpleName())){
				List<Map<String,String>> listTrans = new ArrayList<Map<String,String>>();
				Map<String,String> vo = null;
				
				JSONArray jsonArrObj = (JSONArray) obj;
				for(int i=0;i<jsonArrObj.length();i++){
					JSONObject jsonObj = jsonArrObj.getJSONObject(i);
					vo =  new LinkedHashMap<String,String>();
					for (Iterator iter = jsonObj.keys(); iter.hasNext();) {
						String key = (String)iter.next();
						if("String".equals(jsonObj.get(key).getClass().getSimpleName())){
							vo.put(null2str(key),null2str(jsonObj.getString(key)));
						}else{
							vo.put(null2str(key),null2str(CommonMethod.JSONStringify(jsonObj.get(key))));
						}
					}
					listTrans.add(vo);
				}
				list = listTrans;
				
			}else if("ArrayList".equals(obj.getClass().getSimpleName())){
				list = (List<?>) obj;
			}else{
				logger.debug(obj.getClass().getSimpleName());
				logger.debug("轉換格式既非JsonArray，也非List。");
				return "轉換格式異常";
			}
			
			if(list.size()>0){
				String ex = JSONStringify(list.get(0));
				ex = ex.length()>1000?ex.substring(0, 1000)+"... ":ex;
				return list.size()+" of objects like -> "+ ex;
			}else{
				return list.size()+" of objects";
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.debug("full-range-log",e);
			logger.debug("List_Format_Output失敗 ");
			return "";
		}
	}
	
	
	public String useMysqlDBString(String sp,JSONArray requestVO){
		return JSONStringify(useMysqlDB(sp,requestVO));
	}
	
	//======================以下概念性建構===========================
	public String[] GetToBeUpdateList(String sp,JSONArray requestVO){
		return null;
	}
	
	public String GetToBeUpdateFunction(String update_item){
		return null;
	}
	
	public String EnvironmentChecking(String update_item){
		//執行後顯示諸如 DB有沒有接通 pythonwebservice有沒有
		//部屬時間 版本 各method有沒有通 幾個大類中 哪類頁面有error
		//建立初始化設定 例如說 哪些資料夾存不存在 圖片檔案檢查 (可能要跟前端合作)
		//應該要分出去另一個Java檔
		return null;
	}
	
	public boolean batchCMD(String sp,JSONArray requestElements){  
		try{
	        Class.forName("com.mysql.jdbc.Driver");  
	        Connection conn = (Connection) DriverManager.getConnection(dbURLbatch, dbUserName,dbPassword);  
	        conn.setAutoCommit(false);
	        PreparedStatement psts = conn.prepareStatement(sp);  
	        Date begin=new Date();
	        for( int i=0 ; requestElements!=null && i<requestElements.length() ; i++ ){
	        	JSONObject element = requestElements.getJSONObject(i);
	        	int j=0;
				for (Iterator<String> iter = element.keys(); iter.hasNext();) {
					;
			        String key = (String)iter.next();
			        psts.setString(++j, element.getString(key));
				}
				psts.addBatch();
	            if((++batchCount)%100000==0){
	            	logger.debug("batch資料庫處理資料量達 "+batchCount+" 筆");
	            	psts.executeBatch();
		        	conn.commit();
		        }
			}
	        psts.executeBatch();
	        conn.commit();
	        Date end=new Date();

	        logger.debug("total "+batchCount+" duration_time: "+(end.getTime()-begin.getTime())+" ms");
	        conn.close();  
	        return true;
		}catch(Exception e){
			e.printStackTrace();
			logger.debug("full-range-log",e);
			logger.debug("批次MysqlCMD失敗: "+e.toString());
			return false;
		}
    }
	
	public static Map<String, String> requestParameter(HttpServletRequest request){
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<?> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();

			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					logger_debug(paramName + ": " + paramValue);
					map.put(paramName, paramValue);
				}
			}
		}
		return map ;
	}
	
	public static void logger_debug(String str){
		if(str!=null){
			logger.debug(str);
		}else{
			int trace_lvl=0;
			Exception ex_for_trace = new Exception();
			
			while(ex_for_trace.getStackTrace()[trace_lvl].getClassName().indexOf("CommonMethod")!=-1){trace_lvl++;}
			
			LogManager.getLogger(CommonMethod.class.getSimpleName()).debug("[ "+ex_for_trace.getStackTrace()[trace_lvl].getFileName()+":"+ex_for_trace.getStackTrace()[trace_lvl].getLineNumber()+" ] >"
					+""+str );
		}
	}
	
	public static Map<String, String> requestHeader(HttpServletRequest request){
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<?> paramNames = request.getHeaderNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			logger_debug(paramName + ": " + request.getHeader(paramName));
			map.put(paramName, request.getHeader(paramName));
		}
		return map ;
	}
	
	//CommonMethod.logErr
	public static void logErr(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		logger.debug(sw.toString());
	}
	public static void logErr(String title, Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		logger.debug(title+": "+sw.toString());
	}
}
