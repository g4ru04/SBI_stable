package tw.com.sbi.vo;

public class RegisterEpaperVO {

	private String id;
	private String nick_name;
	private String e_mail;
	private String occupation;
	private String is_order;
	
	private String sys_name;
	private String order_name;
	private String keyword;
	private String source;
	private String backstage;
	
	private String message;
	
	public RegisterEpaperVO(){
		super();
	}
	
	public RegisterEpaperVO(String id, String nick_name, String e_mail, String occupation, String is_order,String backstage, String keyword) {
		super();
		this.setId(id);
		this.setNick_name(nick_name);
		this.setE_mail(e_mail);
		this.setOccupation(occupation);
		this.setIs_order(is_order);
		this.setBackstage(backstage);
		this.setKeyword(keyword);
	}

	public String getId() {
		return id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public String getE_mail() {
		return e_mail;
	}

	public String getOccupation() {
		return occupation;
	}

	public String getIs_order() {
		return is_order;
	}

	public String getSys_name() {
		return sys_name;
	}

	public String getOrder_name() {
		return order_name;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}

	public String getBackstage() {
		return backstage;
	}
	
	public void setId(String id) {
		this.id = strNulToStr(id);
	}

	public void setNick_name(String nick_name) {
		this.nick_name = strNulToStr(nick_name);
	}

	public void setE_mail(String e_mail) {
		this.e_mail = strNulToStr(e_mail);
	}

	public void setOccupation(String occupation) {
		this.occupation = strNulToStr(occupation);
	}

	public void setIs_order(String is_order) {
		this.is_order = strNulToStr(is_order);
	}

	public void setSys_name(String sys_name) {
		this.sys_name = strNulToStr(sys_name);
	}

	public void setOrder_name(String order_name) {
		this.order_name = strNulToStr(order_name);
	}

	public void setKeyword(String keyword) {
		this.keyword = strNulToStr(keyword);
	}

	public void setSource(String source) {
		this.source = strNulToStr(source);
	}

	public void setMessage(String message) {
		this.message = strNulToStr(message);
	}
	
	public void setBackstage(String backstage) {
		this.backstage = strNulToStr(backstage);
	}
	
	public void setId(Object id) {
		this.id = parseString(id);
	}

	public void setNick_name(Object nick_name) {
		this.nick_name = parseString(nick_name);
	}

	public void setEmail(Object e_mail) {
		this.e_mail = parseString(e_mail);
	}

	public void setOccupation(Object occupation) {
		this.occupation = parseString(occupation);
	}

	public void setIs_order(Object is_order) {
		this.is_order = parseString(is_order);
	}

	public void setSys_name(Object sys_name) {
		this.sys_name = parseString(sys_name);
	}

	public void setOrder_name(Object order_name) {
		this.order_name = parseString(order_name);
	}

	public void setKeyword(Object keyword) {
		this.keyword = parseString(keyword);
	}

	public void setSource(Object source) {
		this.source = parseString(source);
	}

	public void setMessage(Object message) {
		this.message = parseString(message);
	}
	
	public void setBackstage(Object backstage) {
		this.backstage = parseString(backstage);
	}
	
	private String parseString(Object obj){
		if(obj==null){
			return null;
		}else{
			return obj.toString();
		}
	}
	
	private String strNulToStr(Object obj) {
		if ("null".equals(obj)) {
			obj = null;
		}
		return obj == null ? "" : obj.toString().trim();
	}
}
