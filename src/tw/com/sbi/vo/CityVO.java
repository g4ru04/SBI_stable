package tw.com.sbi.vo;

public class CityVO{

	private String city;
	private String structure;
	private String dimensions;
	private String target;
	private String secondTarget;
	private String unit;
	private String type;
	private String data;
	
	public String getCity() {
		return city;
	}

	public String getStructure() {
		return structure;
	}

	public String getDimensions() {
		return dimensions;
	}

	public String getTarget() {
		return target;
	}

	public String getSecondTarget() {
		return secondTarget;
	}

	public String getUnit() {
		return unit;
	}

	public String getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setSecondTarget(String secondTarget) {
		this.secondTarget = secondTarget;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setCity(Object city) {
		this.city = parseString(city);
	}

	public void setStructure(Object structure) {
		this.structure = parseString(structure);
	}

	public void setDimensions(Object dimensions) {
		this.dimensions = parseString(dimensions);
	}

	public void setTarget(Object target) {
		this.target = parseString(target);
	}

	public void setSecondTarget(Object secondTarget) {
		this.secondTarget = parseString(secondTarget);
	}

	public void setUnit(Object unit) {
		this.unit = parseString(unit);
	}

	public void setType(Object type) {
		this.type = parseString(type);
	}

	public void setData(Object data) {
		this.data = parseString(data);
	}

	private String parseString(Object obj){
		if(obj==null){
			return null;
		}else{
			return obj.toString();
		}
	}
}
