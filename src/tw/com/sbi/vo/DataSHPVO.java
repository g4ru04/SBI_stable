package tw.com.sbi.vo;

import java.io.Serializable;

public class DataSHPVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sn;
	private String level;
	private String district_id;
	private String district_name;
	private String geom;
	private String parent_district_id;
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getDistrict_id() {
		return district_id;
	}
	public void setDistrict_id(String district_id) {
		this.district_id = district_id;
	}
	public String getDistrict_name() {
		return district_name;
	}
	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}
	public String getGeom() {
		return geom;
	}
	public void setGeom(String geom) {
		this.geom = geom;
	}
	public String getParent_district_id() {
		return parent_district_id;
	}
	public void setParent_district_id(String parent_district_id) {
		this.parent_district_id = parent_district_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
