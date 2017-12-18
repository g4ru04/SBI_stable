package tw.com.sbi.vo;

import java.io.Serializable;

public class CaseVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;// for set check message
	private String case_id;
	private String group_id;
	private String city_id;
	private String bcircle_id;
	private String preference;
	private String evaluate_no;
	private String evaluate;
	private String evaluate_1_no;
	private String evaluate_1;
	private String ending_time;
	private String result;
	private String isfinish;
	private String v_country_id;
	private String v_country;
	private String v_city_name;
	private String v_bcircle_name;
	private String v_decision_proposal;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCase_id() {
		return case_id;
	}
	public void setCase_id(String case_id) {
		this.case_id = case_id;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getCity_id() {
		return city_id;
	}
	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}
	public String getBcircle_id() {
		return bcircle_id;
	}
	public void setBcircle_id(String bcircle_id) {
		this.bcircle_id = bcircle_id;
	}
	public String getPreference() {
		return preference;
	}
	public void setPreference(String preference) {
		this.preference = preference;
	}
	public String getEvaluate_no() {
		return evaluate_no;
	}
	public void setEvaluate_no(String evaluate_no) {
		this.evaluate_no = evaluate_no;
	}
	public String getEvaluate() {
		return evaluate;
	}
	public void setEvaluate(String evaluate) {
		this.evaluate = evaluate;
	}
	public String getEvaluate_1_no() {
		return evaluate_1_no;
	}
	public void setEvaluate_1_no(String evaluate_1_no) {
		this.evaluate_1_no = evaluate_1_no;
	}
	public String getEvaluate_1() {
		return evaluate_1;
	}
	public void setEvaluate_1(String evaluate_1) {
		this.evaluate_1 = evaluate_1;
	}
	public String getEnding_time() {
		return ending_time;
	}
	public void setEnding_time(String ending_time) {
		this.ending_time = ending_time;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getIsfinish() {
		return isfinish;
	}
	public void setIsfinish(String isfinish) {
		this.isfinish = isfinish;
	}
	public String getV_country_id() {
		return v_country_id;
	}
	public void setV_country_id(String v_country_id) {
		this.v_country_id = v_country_id;
	}
	public String getV_country() {
		return v_country;
	}
	public void setV_country(String v_country) {
		this.v_country = v_country;
	}
	public String getV_city_name() {
		return v_city_name;
	}
	public void setV_city_name(String v_city_name) {
		this.v_city_name = v_city_name;
	}
	public String getV_bcircle_name() {
		return v_bcircle_name;
	}
	public void setV_bcircle_name(String v_bcircle_name) {
		this.v_bcircle_name = v_bcircle_name;
	}
	public String getV_decision_proposal() {
		return v_decision_proposal;
	}
	public void setV_decision_proposal(String v_decision_proposal) {
		this.v_decision_proposal = v_decision_proposal;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
		
}
