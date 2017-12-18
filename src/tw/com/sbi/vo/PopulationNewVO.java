package tw.com.sbi.vo;

import java.io.Serializable;
import java.util.List;

public class PopulationNewVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String county_name;
	private String county_data;
	private String county_data_percent;
	
	private String request_table;
	private String request_sex;
	private String request_data_name;
	private String request_data_info_time;
	
	public String getCounty_name() {
		return county_name;
	}
	public void setCounty_name(String county_name) {
		this.county_name = county_name;
	}
	public String getCounty_data() {
		return county_data;
	}
	public void setCounty_data(String county_data) {
		this.county_data = county_data;
	}
	public String getCounty_data_percent() {
		return county_data_percent;
	}
	public void setCounty_data_percent(String county_data_percent) {
		this.county_data_percent = county_data_percent;
	}
	public String getRequest_table() {
		return request_table;
	}
	public void setRequest_table(String request_table) {
		this.request_table = request_table;
	}
	public String getRequest_sex() {
		return request_sex;
	}
	public void setRequest_sex(String request_sex) {
		this.request_sex = request_sex;
	}
	public String getRequest_data_name() {
		return request_data_name;
	}
	public void setRequest_data_name(String request_data_name) {
		this.request_data_name = request_data_name;
	}
	public String getRequest_data_info_time() {
		return request_data_info_time;
	}
	public void setRequest_data_info_time(String request_data_info_time) {
		this.request_data_info_time = request_data_info_time;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
//	public class ageCountyData implements Serializable {//3
//		private static final long serialVersionUID = 1L;
//		private String county_id;
//		private String county_name;
//		private String info_time;
//		private String ageCountyData0_4;
//		private String ageCountyData5_9;
//		private String ageCountyData10_14;
//		private String ageCountyData15_19;
//		private String ageCountyData20_24;
//		private String ageCountyData25_29;
//		private String ageCountyData30_34;
//		private String ageCountyData35_39;
//		private String ageCountyData40_44;
//		private String ageCountyData45_49;
//		private String ageCountyData50_54;
//		private String ageCountyData55_59;
//		private String ageCountyData60_64;
//		private String ageCountyData65_69;
//		private String ageCountyData70_74;
//		private String ageCountyData75_79;
//		private String ageCountyData80_84;
//		private String ageCountyData85_89;
//		private String ageCountyData90_94;
//		private String ageCountyData95_99;
//		private String ageCountyData100up;
//	}
//	public class eduCountyData implements Serializable {
//		private static final long serialVersionUID = 1L;
//		
//		private String eduCountyDatacounty_id;
//		private String eduCountyDatacounty_name;
//		private String eduCountyDatainfo_time;
//		private String eduCountyData_phd;
//		private String eduCountyData_master;
//		private String eduCountyData_university;
//		private String eduCountyData_college;
//		private String eduCountyData_senior;
//		private String eduCountyData_junior;
//		private String eduCountyData_primary;
//		private String eduCountyData_self;
//		private String eduCountyData_no;
//	}
//	public class ageEduCountyData implements Serializable {
//		private static final long serialVersionUID = 1L;
//		private String ageEduCountyDatacounty_id;
//		private String ageEduCountyDatacounty_name;
//		private String ageEduCountyDatainfo_time;
//		private String ageEduCountyData_15_19_phd;
//		private String ageEduCountyData_15_19_master;
//		private String ageEduCountyData_15_19_university;
//		private String ageEduCountyData_15_19_college;
//		private String ageEduCountyData_15_19_senior;
//		private String ageEduCountyData_15_19_junior;
//		private String ageEduCountyData_15_19_primary;
//		private String ageEduCountyData_15_19_self;
//		private String ageEduCountyData_15_19_no;
//		private String ageEduCountyData_20_24_phd;
//		private String ageEduCountyData_20_24_master;
//		private String ageEduCountyData_20_24_university;
//		private String ageEduCountyData_20_24_college;
//		private String ageEduCountyData_20_24_senior;
//		private String ageEduCountyData_20_24_junior;
//		private String ageEduCountyData_20_24_primary;
//		private String ageEduCountyData_20_24_self;
//		private String ageEduCountyData_20_24_no;
//		private String ageEduCountyData_25_29_phd;
//		private String ageEduCountyData_25_29_master;
//		private String ageEduCountyData_25_29_university;
//		private String ageEduCountyData_25_29_college;
//		private String ageEduCountyData_25_29_senior;
//		private String ageEduCountyData_25_29_junior;
//		private String ageEduCountyData_25_29_primary;
//		private String ageEduCountyData_25_29_self;
//		private String ageEduCountyData_25_29_no;
//		private String ageEduCountyData_30_34_phd;
//		private String ageEduCountyData_30_34_master;
//		private String ageEduCountyData_30_34_university;
//		private String ageEduCountyData_30_34_college;
//		private String ageEduCountyData_30_34_senior;
//		private String ageEduCountyData_30_34_junior;
//		private String ageEduCountyData_30_34_primary;
//		private String ageEduCountyData_30_34_self;
//		private String ageEduCountyData_30_34_no;
//		private String ageEduCountyData_35_39_phd;
//		private String ageEduCountyData_35_39_master;
//		private String ageEduCountyData_35_39_university;
//		private String ageEduCountyData_35_39_college;
//		private String ageEduCountyData_35_39_senior;
//		private String ageEduCountyData_35_39_junior;
//		private String ageEduCountyData_35_39_primary;
//		private String ageEduCountyData_35_39_self;
//		private String ageEduCountyData_35_39_no;
//		private String ageEduCountyData_40_44_phd;
//		private String ageEduCountyData_40_44_master;
//		private String ageEduCountyData_40_44_university;
//		private String ageEduCountyData_40_44_college;
//		private String ageEduCountyData_40_44_senior;
//		private String ageEduCountyData_40_44_junior;
//		private String ageEduCountyData_40_44_primary;
//		private String ageEduCountyData_40_44_self;
//		private String ageEduCountyData_40_44_no;
//		private String ageEduCountyData_45_49_phd;
//		private String ageEduCountyData_45_49_master;
//		private String ageEduCountyData_45_49_universuty;
//		private String ageEduCountyData_45_49_college;
//		private String ageEduCountyData_45_49_senior;
//		private String ageEduCountyData_45_49_junior;
//		private String ageEduCountyData_45_49_primary;
//		private String ageEduCountyData_45_49_self;
//		private String ageEduCountyData_45_49_no;
//		private String ageEduCountyData_50_54_phd;
//		private String ageEduCountyData_50_54_master;
//		private String ageEduCountyData_50_54_university;
//		private String ageEduCountyData_50_54_college;
//		private String ageEduCountyData_50_54_senior;
//		private String ageEduCountyData_50_54_junior;
//		private String ageEduCountyData_50_54_primary;
//		private String ageEduCountyData_50_54_self;
//		private String ageEduCountyData_50_54_no;
//		private String ageEduCountyData_55_59_phd;
//		private String ageEduCountyData_55_59_master;
//		private String ageEduCountyData_55_59_university;
//		private String ageEduCountyData_55_59_college;
//		private String ageEduCountyData_55_59_senior;
//		private String ageEduCountyData_55_59_junior;
//		private String ageEduCountyData_55_59_primary;
//		private String ageEduCountyData_55_59_self;
//		private String ageEduCountyData_55_59_no;
//		private String ageEduCountyData_60_64_phd;
//		private String ageEduCountyData_60_64_master;
//		private String ageEduCountyData_60_64_university;
//		private String ageEduCountyData_60_64_college;
//		private String ageEduCountyData_60_64_senior;
//		private String ageEduCountyData_60_64_junior;
//		private String ageEduCountyData_60_64_primary;
//		private String ageEduCountyData_60_64_self;
//		private String ageEduCountyData_60_64_no;
//		private String ageEduCountyData_65_phd;
//		private String ageEduCountyData_65_master;
//		private String ageEduCountyData_65_university;
//		private String ageEduCountyData_65_college;
//		private String ageEduCountyData_65_senior;
//		private String ageEduCountyData_65_junior;
//		private String ageEduCountyData_65_primary;
//		private String ageEduCountyData_65_self;
//		private String ageEduCountyData_65_no;
//	}
}
