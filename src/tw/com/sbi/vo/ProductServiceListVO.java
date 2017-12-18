package tw.com.sbi.vo;

import java.io.Serializable;
import java.util.List;

public class ProductServiceListVO implements Serializable {

	private static final long serialVersionUID = 1L;
	List<ProductServiceVO> listNull;
	List<ProductServiceVO> listAgent;
	
	public List<ProductServiceVO> getListNull() {
		return listNull;
	}
	public void setListNull(List<ProductServiceVO> listNull) {
		this.listNull = listNull;
	}
	public List<ProductServiceVO> getListAgent() {
		return listAgent;
	}
	public void setListAgent(List<ProductServiceVO> listAgent) {
		this.listAgent = listAgent;
	}
}
