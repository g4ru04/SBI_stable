package tw.com.sbi.product.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class imageIcon extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(imageIcon.class);
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("image/png");
		
		//因為一個icon一次request的話 就算瀏覽器有cache 也很汙染log檔
		String message = "";
		try{
			
			String action = request.getParameter("action");
			message += "[action: "+action+"] ";
			String path = "";
			if("getPoiIconPath".equals(action)){
				path=getServletConfig().getServletContext().getInitParameter("poiIconPath");
			}else{
				logger.debug("[TransToJSP]");
				response.sendRedirect("./index.jsp" );
			}
			
			String pic_name_64 = request.getParameter("pic_name");
			message += "[pic_name by base64: "+pic_name_64+"] ";
			final Base64.Decoder decoder = Base64.getDecoder();
			
			String pic_name = new String(
				decoder.decode(
					null2Str(pic_name_64)
				)
			, "UTF-8");
			message += "[pic_name: "+pic_name+"]";
			
			if(pic_name!=null&&!pic_name.equals("")){
				message += "[to_read: "+path + "/" + pic_name+"] ";
				File f = new File(path + "/" + pic_name);
				BufferedImage bi = null;
				try{
					bi = ImageIO.read(f);
				}catch(Exception e) {
					f = new File(path + "default-marker.png");
					bi = ImageIO.read(f);
					logger.debug("POI-icon-not-found-solve-by-default: "+message);
				}
				
				OutputStream out = response.getOutputStream();
				ImageIO.write(bi, "png", out);
				out.close();
			}
		} catch (Exception e) {
			logger.debug("message: "+message);
			e.printStackTrace(System.err);	
			logger.error(e.toString());
		}
	}

	private String null2Str(Object object) {
		if (object instanceof Timestamp)
			return object == null ? "" : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(object);
		return object == null ? "" : object.toString().trim();
	}
}
