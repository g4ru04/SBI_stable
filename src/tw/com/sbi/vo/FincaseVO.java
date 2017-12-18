package tw.com.sbi.vo;

import java.io.Serializable;
import java.util.Date;

public class FincaseVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;// for set check message
	private String case_id;
	private String group_id;
	private String case_name;
	private Float Amount;
	private Float safety_money;
	private Date create_date;

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

	public String getCase_name() {
		return case_name;
	}

	public void setCase_name(String case_name) {
		this.case_name = case_name;
	}

	public Float getAmount() {
		return Amount;
	}

	public void setAmount(Float amount) {
		Amount = amount;
	}

	public Float getSafety_money() {
		return safety_money;
	}

	public void setSafety_money(Float safety_money) {
		this.safety_money = safety_money;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
}