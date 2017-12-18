package tw.com.sbi.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class MySQLAccessModel {
	private String strAccessAction;
	private Map<String, String> mapRqstPara;
	
	private String strDbUrl;
	private String strDbUserName;
	private String strDbPassword;
	
	protected Connection conMysql = null;
	protected CallableStatement stmQueryStatement = null;
	
	private String strInfo;
	private List<Object> lbjResponseVO = null;
	
	private static Logger logger = LogManager.getLogger(MySQLAccessModel.class);
	
	public MySQLAccessModel(ServletConfig cfgServletConfig) {

		super();
		ServletContext txtServletContext = cfgServletConfig.getServletContext();
		if(txtServletContext==null){
			return ;
		}
		
		strDbUrl = txtServletContext.getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		strDbUserName = txtServletContext.getInitParameter("dbUserName");
		strDbPassword = txtServletContext.getInitParameter("dbPassword");
		
	}
	
	public void setStatement(String strAccessAction, Map<String, String> mapRqstPara) throws SQLException{
		//When you inherit this model, please override this method.
	}
	
	public Object convertRowResult(Map<String,Object> mapRowData){
		//When you inherit this model, please override this method.
		return mapRowData;
	}
	
	public boolean execAccess(String strAccessAction){
		
		logger.debug(this.getClass().getSimpleName()+" do "+strAccessAction + ".");
		this.strAccessAction = strAccessAction;
		return queryMySql();
		
	}
	
	private boolean queryMySql(){
		
		this.lbjResponseVO = new ArrayList<Object>();
		ResultSet rssMysqlDataset = null;
		boolean blnQuerySuccess = false;
		
		try {
			this.openConnection();
			this.setStatement(this.strAccessAction,this.mapRqstPara);
			if(this.stmQueryStatement==null){
				throw new NullPointerException("(Check if METHOD:setStatement works or not!!)");
			}
			rssMysqlDataset = this.stmQueryStatement.executeQuery();
			
			ResultSetMetaData rsmMetaData = rssMysqlDataset.getMetaData();
	        int intColumnCount = rsmMetaData.getColumnCount();
	        
	        while (rssMysqlDataset.next()) {
	        	
	            Map<String,Object> mapRowData = new HashMap<String,Object>();
	            
	            for (int i = 1; i <= intColumnCount; i++) {  
	            	mapRowData.put(rsmMetaData.getColumnLabel(i), rssMysqlDataset.getObject(i));
	            }
	            Object objRowData = this.convertRowResult(mapRowData);
	            lbjResponseVO.add(objRowData);  
	            
	        }  
			
			blnQuerySuccess = true;
			
		} catch (SQLException excSQLException) {
			
			setInfo(excSQLException.toString());
			logger.debug("Execution SQL Error.",excSQLException);
			
		} catch (ClassNotFoundException excClassNotFoundException) {
			
			setInfo(excClassNotFoundException.toString());
			logger.debug("Initial DAO Config Error. (Check if created before servlet init.)",excClassNotFoundException);
			
		} catch (NullPointerException excNullException) {
			
			setInfo(excNullException.toString());
			logger.debug("Some Essential Parameter Is Null.",excNullException);
			
		} finally {
			
			try{
				if (rssMysqlDataset != null) {
					rssMysqlDataset.close();
				}
				if (this.stmQueryStatement != null) {
					this.stmQueryStatement.close();
				}
				this.closeConnection();
			} catch (SQLException excSQLException) {
				setInfo(excSQLException.toString());
				logger.debug("CloseError",excSQLException);
			}
			
		}
		return blnQuerySuccess;
	}
	
	private void openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		this.conMysql = DriverManager.getConnection(this.strDbUrl, this.strDbUserName, this.strDbPassword);
	}
	
	@SuppressWarnings("unused")
	private void openConnectionTomcat() throws NamingException, SQLException {
		Context ctxInitContext = new InitialContext();
		Context ctxEnvContext = (Context) ctxInitContext.lookup("java:/comp/env");
		DataSource ds = (DataSource) ctxEnvContext.lookup("jdbc/MySQL");
		this.conMysql = ds.getConnection();
	}
	
	private void closeConnection() throws SQLException {
		if (this.conMysql != null) {
			this.conMysql.close();
		}
	}

	public String getInfo() {
		return strInfo;
	}

	public void setInfo(String strInfo) {
		this.strInfo = strInfo;
	}
	
	public List<Object> getResponseList() {
		return lbjResponseVO;
	}
	
	public String getResponseJsonString() {
		return new Gson().toJson(lbjResponseVO);
	}
	
	public void setRequest(Map<String, String> mapRqstPara){
		this.mapRqstPara = mapRqstPara;
	}
	
}
