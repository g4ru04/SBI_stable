
package tw.com.sbi.common;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class MySQLAccess<T> {

	private static final Logger logger = LogManager.getLogger(MySQLAccess.class);
	
	private String strDbUrl = null;
	private String strDbUserName = null;
	private String strDbPassword = null;

	private Connection conMysql = null;
	private Class<T> clzSingleVo = null;
	
	public MySQLAccess(ServletConfig cfgServletConfig,Class<T> clzSingleVo) {
		if(cfgServletConfig==null){
			logger.error("Initial MySQLAccess Error. (Check if created before servlet init.)");
		}
		this.strDbUrl = cfgServletConfig.getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		this.strDbUserName = cfgServletConfig.getServletContext().getInitParameter("dbUserName");
		this.strDbPassword = cfgServletConfig.getServletContext().getInitParameter("dbPassword");
		this.clzSingleVo = clzSingleVo;
	}

	private void openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conMysql = DriverManager.getConnection(strDbUrl, strDbUserName, strDbPassword);
	}

	private void closeConnection() throws SQLException {
		if (this.conMysql != null) {
			this.conMysql.close();
		}
	}

	public List<T> queryMySql(String strStoreprocedureQuery, List<?> lstRqstList) throws Exception {
		
		CallableStatement stmPrepareQuery = null;
		ResultSet rssMysqlDataset = null;
		List<T> lvoResultSet = null;
		try {
			this.openConnection();
			stmPrepareQuery = this.conMysql.prepareCall(strStoreprocedureQuery);
			
//			SharedFunction.myLogDebug("exeMYSQL: " + strStoreprocedureQuery + " withPara: "
//					+ (lstRqstList == null ? "[]" : new Gson().toJson(lstRqstList)));
			
			for (int i = 0; lstRqstList != null && i < lstRqstList.size(); i++) {
				MySQLAccess.setParaSwitch(stmPrepareQuery, i+1, lstRqstList.get(i));
			}

			rssMysqlDataset = stmPrepareQuery.executeQuery();
			lvoResultSet = this.transResultsetToObject(rssMysqlDataset);
			
//			SharedFunction.myLogDebug("retValue: " + SharedFunction.listObjectsFormater(lvoResultSet));
			
			this.closeConnection();
		} catch (SQLException se) {
			throw se;
		} finally {
			if (rssMysqlDataset != null) {
				rssMysqlDataset.close();
			}
			if (stmPrepareQuery != null) {
				stmPrepareQuery.close();
			}
		}

		return lvoResultSet;
	}
	
	private static <E> void setParaSwitch(PreparedStatement stmPrepareQuery, int index, E genParameter) throws SQLException {

		if (genParameter == null) {
			stmPrepareQuery.setNull(index, java.sql.Types.INTEGER);
		} else if (genParameter.getClass() == String.class) {
			stmPrepareQuery.setString(index, (String) genParameter);
		} else if (genParameter.getClass() == int.class) {
			stmPrepareQuery.setInt(index, (int) genParameter);
		} else if (genParameter.getClass() == long.class) {
			stmPrepareQuery.setLong(index, (long) genParameter);
		} else if (genParameter.getClass() == Double.class) {
			stmPrepareQuery.setDouble(index, (Double) genParameter);
		} else if (genParameter.getClass() == java.sql.Date.class) {
			stmPrepareQuery.setDate(index, (java.sql.Date) genParameter);
		} else if (genParameter.getClass() == Float.class) {
			stmPrepareQuery.setFloat(index, (Float) genParameter);
		} else if (genParameter.getClass() == Short.class) {
			stmPrepareQuery.setShort(index, (Short) genParameter);
		} else if (genParameter.getClass() == byte[].class) {
			stmPrepareQuery.setBytes(index, (byte[]) genParameter);
		} else if (genParameter.getClass() == boolean.class) {
			stmPrepareQuery.setBoolean(index, (boolean) genParameter);
		} else if (genParameter.getClass() == java.sql.Blob.class) {
			stmPrepareQuery.setBlob(index, (java.sql.Blob) genParameter);
		} else if (genParameter.getClass() == byte.class) {
			stmPrepareQuery.setByte(index, (byte) genParameter);
		} else {
			throw new SQLException("Storeprocedure set Statement type not found");
		}
	}
	
	private List<T> transResultsetToObject(ResultSet rssMysqlDataset) throws Exception {
		Class<T> clz = this.clzSingleVo;
		List<T> lvoMysqlDataset = new ArrayList<T>();
		T vloInstance = null;
		
		List<String> lstFields = new ArrayList<String>();
		Field[] fields= clz.getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			lstFields.add(fields[i].getName());
		}
		
		ResultSetMetaData rsmMetaData = rssMysqlDataset.getMetaData();
		int intColumnCount = rsmMetaData.getColumnCount();
		while (intColumnCount != 0 && rssMysqlDataset.next()) {
			try{
				vloInstance = clz.newInstance();
			}catch(Exception e){
				logger.debug("VO with Specific Constructor cause Error",e);
				throw e;
			}
			for (int i = 1; i <= intColumnCount; i++) {
				if(lstFields.contains(rsmMetaData.getColumnName(i))){
					Field fld = clz.getDeclaredField(rsmMetaData.getColumnName(i));
					fld.setAccessible(true);
					fld.set(vloInstance,rssMysqlDataset.getString(i));
				}
			}
			lvoMysqlDataset.add(vloInstance);
		}
		
		return lvoMysqlDataset;
		
	}
	
}