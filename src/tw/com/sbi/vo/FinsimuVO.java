package tw.com.sbi.vo;

import java.io.Serializable;
import java.util.Date;

public class FinsimuVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;// for set check message
	private String simulation_id;
	private String case_id;
	private String user_id;
	private Date f_date;
	private int f_type;
	private Boolean action;
	private Double amount;
	private int f_kind;
	private String description;
	private String strategy;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSimulation_id() {
		return simulation_id;
	}
	public void setSimulation_id(String simulation_id) {
		this.simulation_id = simulation_id;
	}
	public String getCase_id() {
		return case_id;
	}
	public void setCase_id(String case_id) {
		this.case_id = case_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public Date getF_date() {
		return f_date;
	}
	public void setF_date(Date f_date) {
		this.f_date = f_date;
	}
	public int getF_type() {
		return f_type;
	}
	public void setF_type(int f_type) {
		this.f_type = f_type;
	}
	public Boolean getAction() {
		return action;
	}
	public void setAction(Boolean action) {
		this.action = action;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public int getF_kind() {
		return f_kind;
	}
	public void setF_kind(int f_kind) {
		this.f_kind = f_kind;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStrategy() {
		return strategy;
	}
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}