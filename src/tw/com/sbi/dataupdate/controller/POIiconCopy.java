package tw.com.sbi.dataupdate.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.servlet.ServletConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class POIiconCopy {
	private static final Logger logger = LogManager.getLogger(POIiconCopy.class);
	private String poiIconPath ;
	public POIiconCopy(ServletConfig servletConfig){
		poiIconPath = servletConfig.getServletContext().getInitParameter("poiIconPath");
	}
	public int copyFolder() {
		
		try {
			String sysString = UpdateFreewayFlow.class.getProtectionDomain().getCodeSource().getLocation().getPath(); 
			String newPath= sysString.split(sysString.split("/")[sysString.split("/").length - 2])[0]+"images/poiIconUpdate/";
			
			logger.debug("更新poi-from: "+newPath);
			logger.debug("更新poi- to : "+poiIconPath);
			(new File(poiIconPath)).mkdirs();
			File a = new File(newPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (newPath.endsWith(File.separator)) {
					temp = new File(newPath + file[i]);
				} else {
					temp = new File(newPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(poiIconPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
			}
			logger.debug("POIIcon更新成功");
			return file.length;
		} catch (Exception e) {
			logger.debug("POIIcon更新出錯: "+e.toString());
			e.printStackTrace();
			return -1;
		}

	}
}
