package tw.com.sbi.developer;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class MethodsForBen {
	//想一想其實是只有Ben才用得到非系統用的東西都放這兒
	private static final Logger logger = LogManager.getLogger(MethodsForBen.class);
	
	public static String listObjectsFormater(Object obj){
		try{
			List<?> list = (List<?>)obj;
			
			if(list.size()>0){
				String ex = new Gson().toJson(list.get(0));
				ex = ex.length()>1000?ex.substring(0, 1000)+"... ":ex;
				return list.size()+" of objects like -> "+ ex;
			}else{
				return "0 of objects";
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.debug("List_Format_Output失敗 ");
			return "";
		}
	}
	
	public static void myLogDebug(String strMessage) {
		Logger logger = LogManager.getLogger("transPosi");
		List<String> methodLayerClass = Arrays.asList(
			"tw.com.sbi.developer.MethodsForBen",
			"tw.com.sbi.common.MySQLAccessModel"
		);
		int intTraceLevel = 0;
		Exception expFakeException = new Exception();
		
		while (methodLayerClass.contains(expFakeException.getStackTrace()[intTraceLevel].getClassName())) {
			intTraceLevel++;
		}

		logger.debug("[ " + expFakeException.getStackTrace()[intTraceLevel].getFileName() + ":"
				+ String.format("%3d", expFakeException.getStackTrace()[intTraceLevel].getLineNumber() ) + " ]" + " >" + strMessage);
		
	}
	
	public Integer parseInt(Object obj) {
		
		if(obj==null || !parseString(obj).matches("-?\\d+(\\.\\d+)?")){
			return null;
		}else{
			return Integer.valueOf(parseString(obj));
		}
	}
	
	public String parseString(Object obj){
		if(obj==null){
			return null;
		}else{
			return obj.toString();
		}
	}
}
