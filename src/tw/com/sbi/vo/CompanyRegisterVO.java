package tw.com.sbi.vo;

import java.io.Serializable;

public class CompanyRegisterVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String source;
	private String EIN;
	private String company_name;
	private String address;
	private String representative;
	private String capital_amount;
	private String status;
	private String lat;
	private String lng;
	private String creation_date;
	private String modification_date;
	private String disbanded_date;
	private String memo;
	
	private String city_name;
	private String count_n;
	private String geom;
	
	private String creation_d;
	private String modification_d;
	private String disbanded_d;
	private String type;
	
	private String district_name;
	
	private String request_command;
	private String request_type;
	private String request_dataset;
	private String request_statistic;
	private String request_keyword;
	private String request_source;
	
	private String request_city;
	private String request_town;
	
	private String request_id;
	
	public String getDistrict_name() {
		return district_name;
	}
	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getCount_n() {
		return count_n;
	}
	public void setCount_n(String count_n) {
		this.count_n = count_n;
	}
	public String getGeom() {
		return geom;
	}
	public void setGeom(String geom) {
		this.geom = geom;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getEIN() {
		return EIN;
	}
	public void setEIN(String eIN) {
		EIN = eIN;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRepresentative() {
		return representative;
	}
	public void setRepresentative(String representative) {
		this.representative = representative;
	}
	public String getCapital_amount() {
		return capital_amount;
	}
	public void setCapital_amount(String capital_amount) {
		this.capital_amount = capital_amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}
	public String getModification_date() {
		return modification_date;
	}
	public void setModification_date(String modification_date) {
		this.modification_date = modification_date;
	}
	public String getDisbanded_date() {
		return disbanded_date;
	}
	public void setDisbanded_date(String disbanded_date) {
		this.disbanded_date = disbanded_date;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCreation_d() {
		return creation_d;
	}
	public void setCreation_d(String creation_d) {
		this.creation_d = creation_d;
	}
	public String getModification_d() {
		return modification_d;
	}
	public void setModification_d(String modification_d) {
		this.modification_d = modification_d;
	}
	public String getDisbanded_d() {
		return disbanded_d;
	}
	public void setDisbanded_d(String disbanded_d) {
		this.disbanded_d = disbanded_d;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRequest_command() {
		return request_command;
	}
	public void setRequest_command(String request_command) {
		this.request_command = request_command;
	}
	public String getRequest_type() {
		return request_type;
	}
	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}
	public String getRequest_dataset() {
		return request_dataset;
	}
	public void setRequest_dataset(String request_dataset) {
		this.request_dataset = request_dataset;
	}
	public String getRequest_statistic() {
		return request_statistic;
	}
	public void setRequest_statistic(String request_statistic) {
		this.request_statistic = request_statistic;
	}
	public String getRequest_keyword() {
		return request_keyword;
	}
	public void setRequest_keyword(String request_keyword) {
		this.request_keyword = request_keyword;
	}
	public String getRequest_source() {
		return request_source;
	}
	public void setRequest_source(String request_source) {
		this.request_source = request_source;
	}
	public String getRequest_city() {
		return request_city;
	}
	public void setRequest_city(String request_city) {
		this.request_city = request_city;
	}
	public String getRequest_town() {
		return request_town;
	}
	public void setRequest_town(String request_town) {
		this.request_town = request_town;
	}
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
