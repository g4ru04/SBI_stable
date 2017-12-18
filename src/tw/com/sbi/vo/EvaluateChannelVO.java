package tw.com.sbi.vo;

import java.io.Serializable;

public class EvaluateChannelVO implements Serializable {

	private static final long serialVersionUID = 1L;
	//tb_evaluate_channel
	private String evaluate_channel_id;
	private String channel_id;
	private String user_id;
	private String evaluate_reason;
	private String weight;
	private String user_authority;
	private String evaluate_point;
	private String evaluate_1_point;
	private String evaluate_seq;
	//tb_case_channel
	private String evaluate_no;
	private String evaluate_1_no;
	private String evaluate;
	private String evaluate_1;
	// tb_user
	private String user_name;	

	public String getEvaluate_no() {
		return evaluate_no;
	}

	public void setEvaluate_no(String evaluate_no) {
		this.evaluate_no = evaluate_no;
	}

	public String getEvaluate_1_no() {
		return evaluate_1_no;
	}

	public void setEvaluate_1_no(String evaluate_1_no) {
		this.evaluate_1_no = evaluate_1_no;
	}

	public String getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(String evaluate) {
		this.evaluate = evaluate;
	}

	public String getEvaluate_1() {
		return evaluate_1;
	}

	public void setEvaluate_1(String evaluate_1) {
		this.evaluate_1 = evaluate_1;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getEvaluate_channel_id() {
		return evaluate_channel_id;
	}

	public void setEvaluate_channel_id(String evaluate_channel_id) {
		this.evaluate_channel_id = evaluate_channel_id;
	}

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getEvaluate_reason() {
		return evaluate_reason;
	}

	public void setEvaluate_reason(String evaluate_reason) {
		this.evaluate_reason = evaluate_reason;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getUser_authority() {
		return user_authority;
	}

	public void setUser_authority(String user_authority) {
		this.user_authority = user_authority;
	}

	public String getEvaluate_point() {
		return evaluate_point;
	}

	public void setEvaluate_point(String evaluate_point) {
		this.evaluate_point = evaluate_point;
	}

	public String getEvaluate_1_point() {
		return evaluate_1_point;
	}

	public void setEvaluate_1_point(String evaluate_1_point) {
		this.evaluate_1_point = evaluate_1_point;
	}

	public String getEvaluate_seq() {
		return evaluate_seq;
	}

	public void setEvaluate_seq(String evaluate_seq) {
		this.evaluate_seq = evaluate_seq;
	}

}
