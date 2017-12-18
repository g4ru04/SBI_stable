package tw.com.sbi.vo;

import java.io.Serializable;

public class AgentVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;// for set check message
	private String group_id;
	private String agent_id;
	private String agent_name;
	private String web_site;
	private String region_code;
	private String contact_mail;
	private String contact_phone;
	private String seed;
	
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
	public String getWeb_site() {
		return web_site;
	}
	public void setWeb_site(String web_site) {
		this.web_site = web_site;
	}
	public String getRegion_code() {
		return region_code;
	}
	public void setRegion_code(String region_code) {
		this.region_code = region_code;
	}
	public String getContact_mail() {
		return contact_mail;
	}
	public void setContact_mail(String contact_mail) {
		this.contact_mail = contact_mail;
	}
	public String getContact_phone() {
		return contact_phone;
	}
	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
}
