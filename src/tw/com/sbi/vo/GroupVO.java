package tw.com.sbi.vo;

import java.sql.Date;

public class GroupVO implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String group_id;
	private String group_name;
	private String user_id;
	
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
