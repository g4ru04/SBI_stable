package tw.com.sbi.trackingEmbed.controller;
import java.io.IOException;  

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;  
  
/** 
 * 这只是示例，可以参考MyWriter 
 * @author Administrator 
 * 
 */  
public class ServletOutputStreamEmbed extends ServletOutputStream {  
    private ServletOutputStream outputStream;  
      
    public ServletOutputStreamEmbed(ServletOutputStream outputStream) {  
        this.outputStream = outputStream;  
    }  
  
    @Override  
    public void write(int b) throws IOException {  
        outputStream.write(b);  
//        System.out.println("output1");  
    }  
  
    @Override  
    public void write(byte[] b, int off, int len) throws IOException {  
        super.write(b, off, len);  
//        System.out.println("output2");  
    }  
  
    @Override  
    public void write(byte[] b) throws IOException {  
        super.write(b);  
//        System.out.println("output3");  
    }

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setWriteListener(WriteListener arg0) {
	}  
  
}  