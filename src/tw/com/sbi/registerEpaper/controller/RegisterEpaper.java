package tw.com.sbi.registerEpaper.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import tw.com.sbi.common.MySQLAccess;
import tw.com.sbi.common.SharedFunction;
import tw.com.sbi.vo.RegisterEpaperVO;
import tw.com.sbi.vo.ResponseVO;

public class RegisterEpaper extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(RegisterEpaper.class);
	
	private RegisterEpaperDAO dao = null;//new RegisterEpaperDAO();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("[no Get]");
		response.sendRedirect("./404.jsp");
	}

	protected void doPost(HttpServletRequest rqsClientRequest, HttpServletResponse rpnClientResponse)
			throws ServletException, IOException {

		rqsClientRequest.setCharacterEncoding("UTF-8");
		rpnClientResponse.setCharacterEncoding("UTF-8");
		rpnClientResponse.setContentType("text/text");
		
		try {
			// Bind input as Object
			dao = new RegisterEpaperDAO();
			Map<String, String> mapRequestPara = SharedFunction.getFormatedRqstPara(rqsClientRequest);

			String strAction = mapRequestPara.get("action");

			RegisterEpaperVO vloRqstVO = new RegisterEpaperVO();

			ResponseVO<RegisterEpaperVO> vloResponseVO = new ResponseVO<RegisterEpaperVO>();
			vloRqstVO = new RegisterEpaperVO(
				null, 
				mapRequestPara.get("nickname"), 
				mapRequestPara.get("email"),
				mapRequestPara.get("occupation"), 
				null, 
				mapRequestPara.get("backstage"),
				mapRequestPara.get("keyword")
			);

			// Deal with Actions
			if ("insertNewRegister".equals(strAction)) {
				// insert
				if (this.haveOrdered(vloRqstVO)) {
					// current ordering
					vloResponseVO.setMessage("isduplicate");
				} else {
					// without order now
					String strRegisterId = this.haveEverOrder(vloRqstVO);
					if (strRegisterId != null) {
						if ("true".equals(vloRqstVO.getBackstage())) {
							// update (only backstage can do)
							vloRqstVO.setId(strRegisterId);
							vloRqstVO.setIs_order("1");
							boolean blnUpdateSuccess = dao.update(vloRqstVO);
							if (blnUpdateSuccess) {
								vloResponseVO.setMessage("success");
							}
						} else {
							// can't register twice even it has canceled
							vloResponseVO.setMessage("isexist");
						}
					} else {
						// normally insert
						vloRqstVO.setIs_order("1");

						boolean blnInsertSuccess = dao.insert(vloRqstVO);
						if (blnInsertSuccess) {
							vloResponseVO.setMessage("success");
						}
					}
				}

			} else if ("cancelRegister".equals(strAction)) {
				// cancel
				String strRegisterId = this.haveEverOrder(vloRqstVO);
				vloRqstVO.setId(strRegisterId);
				if (strRegisterId == null) {
					vloResponseVO.setMessage("haveNeverRegister");
				} else {
					if (!this.haveOrdered(vloRqstVO)) {
						vloResponseVO.setMessage("haveCancelBefore");
					} else {
						if (this.cancel(vloRqstVO)) {
							vloResponseVO.setMessage("cancelSuccess");
						}
					}
				}

			} else if ("selectWithKeyword".equals(strAction)) {
				// select
				List<RegisterEpaperVO> lstEpaperList = dao.selectKeyword(vloRqstVO);
				vloResponseVO = new ResponseVO<RegisterEpaperVO>();
				vloResponseVO.setSuccessObjList(lstEpaperList);
			} else {
				logger.debug("[TransToJSP]");
				rpnClientResponse.sendRedirect("./registerEpaper.jsp");
			}

			vloResponseVO.setSuccess(true);
			String retStr = new Gson().toJson(vloResponseVO);
			logger.info("[Output]: " + retStr);
			rpnClientResponse.getWriter().write(retStr);

		} catch (Exception e) {
			logger.error("RegisterEpaperError", e);
		}
	}

	// Common method * 3
	private boolean haveOrdered(RegisterEpaperVO vloRequestVO) throws Exception {
		List<RegisterEpaperVO> lstEpaperOrderList = dao.selectOrder();
		Boolean blnHaveOrdered = false;
		for (int i = 0; i < lstEpaperOrderList.size(); i++) {
			if (vloRequestVO.getE_mail().equals(lstEpaperOrderList.get(i).getE_mail())) {
				blnHaveOrdered = true;
			}
		}
		return blnHaveOrdered;
	}

	private String haveEverOrder(RegisterEpaperVO vloRequestVO) throws Exception {
		List<RegisterEpaperVO> lstEpaperRegList = dao.selectAll();
		String strRegisterId = null;
		for (int i = 0; i < lstEpaperRegList.size(); i++) {
			if (vloRequestVO.getE_mail().equals(lstEpaperRegList.get(i).getE_mail())) {
				strRegisterId = lstEpaperRegList.get(i).getId();
			}
		}
		return strRegisterId;
	}
 
	private boolean cancel(RegisterEpaperVO vloRequestVO) throws Exception {
		List<RegisterEpaperVO> lstEpaperRegisterList = dao.selectAll();
		for (int i = 0; i < lstEpaperRegisterList.size(); i++) {
			if (vloRequestVO.getId().equals(lstEpaperRegisterList.get(i).getId())) {
				lstEpaperRegisterList.get(i).setIs_order("0");
				return dao.update(lstEpaperRegisterList.get(i));
			}
		}
		return false;
	}

	//DAO
	class RegisterEpaperDAO {
		
		private MySQLAccess<RegisterEpaperVO> conMysql = new MySQLAccess<RegisterEpaperVO>(getServletConfig(),RegisterEpaperVO.class);

		public List<RegisterEpaperVO> selectKeyword(RegisterEpaperVO vloRequestVO) throws Exception {
			List<String> lstRequestList = Arrays.asList(vloRequestVO.getKeyword());
			List<RegisterEpaperVO> lvoList = conMysql.queryMySql("call sp_select_epaper_backstage(?)", lstRequestList);
			return lvoList;
		}

		public List<RegisterEpaperVO> selectOrder() throws Exception {
			List<RegisterEpaperVO> lvoList = conMysql.queryMySql("call sp_select_EpaperOrder_User()", null);
			return lvoList;
		}

		public List<RegisterEpaperVO> selectAll() throws Exception {
			List<RegisterEpaperVO> lvoList = conMysql.queryMySql("call sp_select_Epaper_Order()", null);
			return lvoList;
		}

		public boolean insert(RegisterEpaperVO vloRequestVO) throws Exception {
			List<String> lstRequestList = Arrays.asList(
					vloRequestVO.getNick_name(), 
					vloRequestVO.getOccupation(), 
					vloRequestVO.getE_mail(),
					vloRequestVO.getIs_order()
				);
			conMysql.queryMySql("call sp_insert_Epaper_Order(?,?,?,?)", lstRequestList);
			return true;
		}

		public boolean update(RegisterEpaperVO vloRequestVO) throws Exception {
			List<String> lstRequestList = Arrays.asList(
					vloRequestVO.getId(), 
					vloRequestVO.getNick_name(), 
					vloRequestVO.getOccupation(),
					vloRequestVO.getE_mail(), 
					vloRequestVO.getIs_order()
				);
			conMysql.queryMySql("call sp_update_Epaper_Order(?,?,?,?,?)", lstRequestList);
			return true;
		}
		
	}
	
}