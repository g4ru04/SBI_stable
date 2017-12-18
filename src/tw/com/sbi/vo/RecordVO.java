package tw.com.sbi.vo;

import java.io.Serializable;
import java.util.List;
import tw.com.sbi.vo.ScenarioResultVO;
public class RecordVO implements Serializable {

	private static final long serialVersionUID = 1L;
	//_scenario_name, _scenario_explanation, flow_seq, flow_name, explanation, _result
	private String scenario_name;
	private String scenario_explanation;
	private String flow_seq;
	private String flow_name;
	private String flow_explanation;
	private List<ScenarioResultVO> results;
	
	public String getScenario_name() {
		return scenario_name;
	}

	public void setScenario_name(String scenario_name) {
		this.scenario_name = scenario_name;
	}

	public String getScenario_explanation() {
		return scenario_explanation;
	}

	public void setScenario_explanation(String scenario_explanation) {
		this.scenario_explanation = scenario_explanation;
	}

	public String getFlow_seq() {
		return flow_seq;
	}

	public void setFlow_seq(String flow_seq) {
		this.flow_seq = flow_seq;
	}

	public String getFlow_name() {
		return flow_name;
	}

	public void setFlow_name(String flow_name) {
		this.flow_name = flow_name;
	}

	public String getFlow_explanation() {
		return flow_explanation;
	}

	public void setFlow_explanation(String flow_explanation) {
		this.flow_explanation = flow_explanation;
	}

	public List<ScenarioResultVO> getResults() {
		return results;
	}

	public void setResults(List<ScenarioResultVO> results) {
		this.results = results;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
