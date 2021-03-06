package tw.com.sbi.product.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class image extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(image.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public image() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try{
			String record_log = getServletConfig().getServletContext().getInitParameter("uploadpath")+"/log.txt";
			String processName =java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			String my_msg =(new SimpleDateFormat("yyyy-MM-dd(E) HH:mm:ss").format(new Date()))+":\r\n  I'm image-java with PID = "+ Long.parseLong(processName.split("@")[0])+".\r\n";
			FileWriter fw;
			try{
				fw = new FileWriter(record_log,true);
			}catch(FileNotFoundException e){
				logger.debug("full-range-log",e);
				fw = new FileWriter(record_log,false);
			}
			fw.write(my_msg);
			fw.close();
		}catch(Exception e){
			logger.debug("full-range-log",e);
			System.out.println("Error: "+e.toString());
		}
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("image/jpeg");

		String upload_root = getServletConfig().getServletContext().getInitParameter("photopath");
		
		if(request.getParameter("action") != null && request.getParameter("action").equals("qrcode")) {
			upload_root = getServletConfig().getServletContext().getInitParameter("qrcodephotopath");
		}
		
		
		
		String picname = request.getParameter("picname");
		if(picname!=null&&!picname.equals("")){
			logger.debug(upload_root + "/" + picname);
			File f = new File(upload_root + "/" + picname);
//			File loss = new File(upload_root+"/loss.gif");
			try{
				BufferedImage bi = ImageIO.read(f);
				OutputStream out = response.getOutputStream();
				ImageIO.write(bi, "jpg", out);
				out.close();
			}catch(Exception e){
				logger.debug("full-range-log",e);
				//String path = getServletContext().getRealPath("./");
				//System.out.println(path);
				RequestDispatcher successView = request.getRequestDispatcher("/images/loss.gif");
				successView.forward(request, response);
				System.out.println("name: "+f+" with: "+e.toString());
			}
		} else {
			System.out.println("沒有圖檔名稱");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
