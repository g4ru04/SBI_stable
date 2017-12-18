package tw.com.sbi.vo;

import java.io.Serializable;

public class AgentAuthVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;// for set check message
	private String group_id;
	private String product_id;
	private String product_spec;
	private String agent_id;
	private String agent_name;
	private String region_code;
	private String auth_quantity;
	private String sale_quantity;
	private String register_quantity;
	private String seed;
	private String auth_code;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getProduct_spec() {
		return product_spec;
	}
	public void setProduct_spec(String product_spec) {
		this.product_spec = product_spec;
	}
	public String getAgent_id() {
		return agent_id;
	}
	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}
	public String getAgent_name() {
		return agent_name;
	}
	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}
	public String getRegion_code() {
		return region_code;
	}
	public void setRegion_code(String region_code) {
		this.region_code = region_code;
	}
	public String getAuth_quantity() {
		return auth_quantity;
	}
	public void setAuth_quantity(String auth_quantity) {
		this.auth_quantity = auth_quantity;
	}
	public String getSale_quantity() {
		return sale_quantity;
	}
	public void setSale_quantity(String sale_quantity) {
		this.sale_quantity = sale_quantity;
	}
	public String getRegister_quantity() {
		return register_quantity;
	}
	public void setRegister_quantity(String register_quantity) {
		this.register_quantity = register_quantity;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
}
