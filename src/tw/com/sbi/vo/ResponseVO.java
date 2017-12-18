package tw.com.sbi.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResponseVO<T> {
	private boolean blnSuccess;
	private String strMessage; //執行動作後回傳訊息
	private String strErrorMessage; //系統面 同時系統面也只有錯誤的時候會寫
	private int intResultSize;
	private List<T> lstResult;

	// 以下 constructor 和 easy method
	public ResponseVO() {
		blnSuccess = false;
		strErrorMessage = "";
	}

	public void setErrorMessage(String strErrorMessage) {
		this.setSuccess(false);
		this.setErrorMessage(strErrorMessage);
	}
	
	public void setSuccessMessage(String strSuccessMessage) {
		this.setSuccess(true);
		this.setErrorMessage(strSuccessMessage);
	}

	public void setSuccessObjList(List<T> lstResult) {
		this.setSuccess(true);
		this.setLstResult(lstResult);
		this.setIntResultSize(lstResult.size());
	}

	public void setSuccessObjList(T[] arrResult) {
		List<T> lvoResult = new ArrayList<T>();
		for (int i = 0; i < arrResult.length; i++) {
			lvoResult.add(arrResult[i]);
		}
		this.setSuccessObjList(lvoResult);
	}

	@SuppressWarnings("unchecked")
	public void setSuccessObjList(Object objResult) {
		
		if (objResult == null) {
			this.setSuccess(true);
			
		} else if (objResult instanceof java.util.List) {
			this.setSuccessObjList((List<T>) objResult);
			
		} else if (objResult.getClass().isArray()) {
			this.setSuccessObjList((T[]) objResult);
			
		} else {
			List<T> lstResult = Arrays.asList((T) objResult);
			this.setSuccessObjList(lstResult);
			
		}
		
	}

	// 以下getter setter
	public boolean isSuccess() {
		return blnSuccess;
	}

	public void setSuccess(boolean blnSuccess) {
		this.blnSuccess = blnSuccess;
	}

	public String getMessage() {
		return strMessage;
	}

	public void setMessage(String strMessage) {
		this.strMessage = strMessage;
	}

	public String getErrorMessage() {
		return strErrorMessage;
	}

	public void setstrErrorMessage(String strErrorMessage) {
		this.strErrorMessage = strErrorMessage;
	}

	public List<T> getLstResult() {
		return lstResult;
	}

	public void setLstResult(List<T> lstResult) {
		this.lstResult = lstResult;
	}

	public int getIntResultSize() {
		return intResultSize;
	}

	public void setIntResultSize(int intResultSize) {
		this.intResultSize = intResultSize;
	}
}
