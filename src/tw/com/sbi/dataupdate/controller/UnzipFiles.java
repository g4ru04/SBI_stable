package tw.com.sbi.dataupdate.controller;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.tar.TarEntry;  
import org.apache.tools.tar.TarInputStream;  
import org.apache.tools.tar.TarOutputStream;

import tw.com.sbi.common.controller.TestPlace;
public class UnzipFiles {
	/** 
	 * 解压tar.gz 文件 
	 * @param file 要解压的tar.gz文件对象 
	 * @param outputDir 要解压到某个指定的目录下 
	 * @throws IOException 
	 */  
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(UnzipFiles.class);
	public static void unTarGz(File file,String outputDir) throws IOException{  
	    TarInputStream tarIn = null;  
	    try{  
	        tarIn = new TarInputStream(new GZIPInputStream(  
	                new BufferedInputStream(new FileInputStream(file))),  
	                1024 * 2);  
	          
	        createDirectory(outputDir,null);//创建输出目录  

	        TarEntry entry = null;  
	        while( (entry = tarIn.getNextEntry()) != null ){  
	              
	            if(entry.isDirectory()){//是目录
	                entry.getName();
	                createDirectory(outputDir,entry.getName());//创建空目录  
	            }else{//是文件
	                File tmpFile = new File(outputDir + "/" + entry.getName());  
	                createDirectory(tmpFile.getParent() + "/",null);//创建输出目录  
	                OutputStream out = null;  
	                try{  
	                    out = new FileOutputStream(tmpFile);  
	                    int length = 0;  
	                      
	                    byte[] b = new byte[2048];  
	                      
	                    while((length = tarIn.read(b)) != -1){  
	                        out.write(b, 0, length);  
	                    }  
	                  
	                }catch(IOException ex){  
	                    throw ex;  
	                }finally{  
	                      
	                    if(out!=null)  
	                        out.close();  
	                }  
	            }
	        }  
	    }catch(IOException ex){  
	    	logger.debug("解压归档文件出现异常",ex);  
	    } finally{  
	        try{  
	            if(tarIn != null){  
	                tarIn.close();  
	            }  
	        }catch(IOException ex){  
	        	logger.debug("关闭tarFile出现异常",ex);  
	        }  
	    }  
	}
	public static void createDirectory(String outputDir,String subDir){
        File file = new File(outputDir);  
        if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空  
            file = new File(outputDir + "/" + subDir);  
        }  
        if(!file.exists()||!file.isDirectory()){
              if(!file.getParentFile().exists()||!file.getParentFile().isDirectory())
                  file.getParentFile().mkdirs();
            file.mkdirs();  
        }  
    }
}
