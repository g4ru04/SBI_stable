package tw.com.sbi.vo;

import java.io.Serializable;
import java.util.List;

public class ScenarioJobVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String job_id;
	private String job_name;
	private String group_id;
	private String flow_id;
	private String flow_name;
	private String flow_function;

	private String scenario_id;
	private String scenario_name;
	private String page;
	private String flow_seq;
	private String job_time;
	private String result;
	private String finished;
	private String finish_time;
	
	private String next_flow_id;
	private String next_flow_name;
	private String next_flow_page;
	private String next_flow_explanation;
	private String next_flow_guide;
	
	private String max_flow_seq;
	
	private List<ScenarioJobVO> child;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJob_id() {
		return job_id;
	}
	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}
	public String getJob_name() {
		return job_name;
	}
	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getFlow_id() {
		return flow_id;
	}
	public void setFlow_id(String flow_id) {
		this.flow_id = flow_id;
	}
	public String getFlow_name() {
		return flow_name;
	}
	public void setFlow_name(String flow_name) {
		this.flow_name = flow_name;
	}
	public String getFlow_function() {
		return flow_function;
	}
	public void setFlow_function(String flow_function) {
		this.flow_function = flow_function;
	}
	public String getNext_flow_explanation() {
		return next_flow_explanation;
	}
	public void setNext_flow_explanation(String next_flow_explanation) {
		this.next_flow_explanation = next_flow_explanation;
	}
	public String getNext_flow_guide() {
		return next_flow_guide;
	}
	public void setNext_flow_guide(String next_flow_guide) {
		this.next_flow_guide = next_flow_guide;
	}
	public String getScenario_id() {
		return scenario_id;
	}
	public void setScenario_id(String scenario_id) {
		this.scenario_id = scenario_id;
	}
	public String getScenario_name() {
		return scenario_name;
	}
	public void setScenario_name(String scenario_name) {
		this.scenario_name = scenario_name;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getFlow_seq() {
		return flow_seq;
	}
	public void setFlow_seq(String flow_seq) {
		this.flow_seq = flow_seq;
	}
	public String getJob_time() {
		return job_time;
	}
	public void setJob_time(String job_time) {
		this.job_time = job_time.replace(".0","");
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getFinished() {
		return finished;
	}
	public void setFinished(String finished) {
		this.finished = finished;
	}
	public String getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(String finish_time) {
		int len =finish_time.length()>11?11:finish_time.length();
		this.finish_time = finish_time.substring(0,len);
	}
	public String getNext_flow_id() {
		return next_flow_id;
	}
	public void setNext_flow_id(String next_flow_id) {
		this.next_flow_id = next_flow_id;
	}
	public String getNext_flow_name() {
		return next_flow_name;
	}
	public void setNext_flow_name(String next_flow_name) {
		this.next_flow_name = next_flow_name;
	}
	public String getNext_flow_page() {
		return next_flow_page;
	}
	public void setNext_flow_page(String next_flow_page) {
		this.next_flow_page = next_flow_page;
	}
	public String getMax_flow_seq() {
		return max_flow_seq;
	}
	public void setMax_flow_seq(String max_flow_seq) {
		this.max_flow_seq = max_flow_seq;
	}
	public List<ScenarioJobVO> getChild() {
		return child;
	}
	public void setChild(List<ScenarioJobVO> child) {
		this.child = child;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
