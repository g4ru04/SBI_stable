package tw.com.sbi.vo;

import java.io.Serializable;

public class IndustryVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;// for set check message
	private String country;
	private String industryType;
	private String source;
	private String subsource;
	private String categories;
	private String categoriesYear;
	private String categoriesData;
	private String unit;
	private String dataSource;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getIndustryType() {
		return industryType;
	}
	public void setIndustryType(String industryType) {
		this.industryType = industryType;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSubsource() {
		return subsource;
	}
	public void setSubsource(String subsource) {
		this.subsource = subsource;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	public String getCategoriesYear() {
		return categoriesYear;
	}
	public void setCategoriesYear(String categoriesYear) {
		this.categoriesYear = categoriesYear;
	}
	public String getCategoriesData() {
		return categoriesData;
	}
	public void setCategoriesData(String categoriesData) {
		this.categoriesData = categoriesData;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
