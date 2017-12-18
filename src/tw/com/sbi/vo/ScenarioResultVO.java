package tw.com.sbi.vo;
import com.google.gson.Gson;
import java.util.List;

public class ScenarioResultVO implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String scenario_name;
	private String step;
	private String flow_name;
	private String page;
	private String category;
	private String result;
	private String png_name;
	private List<ScenarioResultVO> results;
	public ScenarioResultVO(){
		
	}
	public ScenarioResultVO(String scenario_name, String step, String flow_name, String page, String category,String result){
		this.scenario_name = scenario_name;
		this.step = step;
		this.flow_name = flow_name;
		this.page = page;
		this.category = category;
		this.result = result;
		this.results = null;
	}

	public String getScenario_name() {
		return scenario_name;
	}

	public void setScenario_name(String scenario_name) {
		this.scenario_name = scenario_name;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getFlow_name() {
		return flow_name;
	}

	public void setFlow_name(String flow_name) {
		this.flow_name = flow_name;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getPng_name() {
		return png_name;
	}
	
	public void setPng_name(String png_name) {
		this.png_name = png_name;
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