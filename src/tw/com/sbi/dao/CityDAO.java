package tw.com.sbi.dao;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletConfig;

import tw.com.sbi.common.MySQLAccessModel;
import tw.com.sbi.vo.CityVO;

class CityDAO extends MySQLAccessModel{
	private static final String sp_STAT_City = "call sp_STAT_City()";
	private static final String sp_STAT_City_Structure = "call sp_STAT_City_Structure(?)";
	private static final String sp_STAT_City_Dimensions = "call sp_STAT_City_Dimensions(?,?)";
	private static final String sp_STAT_City_Target = "call sp_STAT_City_Target(?,?,?)";
	private static final String sp_STAT_City_SecondTarget = "call sp_STAT_City_SecondTarget(?,?,?,?)";
	private static final String sp_STAT_City_TYPE = "call sp_STAT_City_TYPE(?,?,?,?,?)";
	private static final String sp_STAT_City_Chart = "call sp_STAT_City_Chart(?,?,?,?,?,?)";
	
	public CityDAO(ServletConfig cfgServletConfig) {
		//super(getServletConfig());
		super(cfgServletConfig);
	}
	
	public void setStatement(String strAccessAction, Map<String, String> mapRqstPara) throws SQLException{
		
		if("getCity".equals(strAccessAction)){
			
			this.stmQueryStatement = this.conMysql.prepareCall(sp_STAT_City);
			
		}else if("getStructure".equals(strAccessAction)){
			
			this.stmQueryStatement = this.conMysql.prepareCall(sp_STAT_City_Structure);
			this.stmQueryStatement.setString(1, mapRqstPara.get("city"));
			
		}else if("getDimensions".equals(strAccessAction)){
			
			this.stmQueryStatement = this.conMysql.prepareCall(sp_STAT_City_Dimensions);
			this.stmQueryStatement.setString(1, mapRqstPara.get("city"));
			this.stmQueryStatement.setString(2, mapRqstPara.get("structure"));
			
		}else if("getTarget".equals(strAccessAction)){
			
			this.stmQueryStatement = this.conMysql.prepareCall(sp_STAT_City_Target);
			this.stmQueryStatement.setString(1, mapRqstPara.get("city"));
			this.stmQueryStatement.setString(2, mapRqstPara.get("structure"));
			this.stmQueryStatement.setString(3, mapRqstPara.get("dimensions"));
			
		}else if("getSecondTarget".equals(strAccessAction)){
			
			this.stmQueryStatement = this.conMysql.prepareCall(sp_STAT_City_SecondTarget);
			this.stmQueryStatement.setString(1, mapRqstPara.get("city"));
			this.stmQueryStatement.setString(2, mapRqstPara.get("structure"));
			this.stmQueryStatement.setString(3, mapRqstPara.get("dimensions"));
			this.stmQueryStatement.setString(4, mapRqstPara.get("target"));
			
		}else if("getType".equals(strAccessAction)){
			
			this.stmQueryStatement = this.conMysql.prepareCall(sp_STAT_City_TYPE);
			this.stmQueryStatement.setString(1, mapRqstPara.get("city"));
			this.stmQueryStatement.setString(2, mapRqstPara.get("structure"));
			this.stmQueryStatement.setString(3, mapRqstPara.get("dimensions"));
			this.stmQueryStatement.setString(4, mapRqstPara.get("target"));
			this.stmQueryStatement.setString(5, mapRqstPara.get("second_target"));
			
		}else if("getChart".equals(strAccessAction)){
			
			this.stmQueryStatement = this.conMysql.prepareCall(sp_STAT_City_Chart);
			this.stmQueryStatement.setString(1, mapRqstPara.get("city"));
			this.stmQueryStatement.setString(2, mapRqstPara.get("structure"));
			this.stmQueryStatement.setString(3, mapRqstPara.get("dimensions"));
			this.stmQueryStatement.setString(4, mapRqstPara.get("target"));
			this.stmQueryStatement.setString(5, mapRqstPara.get("second_target"));
			this.stmQueryStatement.setString(6, mapRqstPara.get("type"));
			
		}
		
	}
	
	public Object convertRowResult(Map<String,Object> mapRowData){
		
		CityVO cityVO = new CityVO();
		cityVO.setCity(mapRowData.get("City"));
		cityVO.setStructure(mapRowData.get("Structure"));
		cityVO.setDimensions(mapRowData.get("Dimensions"));
		cityVO.setTarget(mapRowData.get("Target"));
		cityVO.setSecondTarget(mapRowData.get("Second_Target"));
		
		cityVO.setData(mapRowData.get("Data"));
		if(mapRowData.get("TYPE")!=null){
			cityVO.setType(mapRowData.get("TYPE"));
		}
		if(mapRowData.get("Type")!=null){
			cityVO.setType(mapRowData.get("Type"));
		}
		cityVO.setUnit(mapRowData.get("UNIT"));
		
		return cityVO;
	}
}