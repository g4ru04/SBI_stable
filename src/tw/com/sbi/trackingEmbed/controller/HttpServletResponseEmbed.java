package tw.com.sbi.trackingEmbed.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HttpServletResponseEmbed extends HttpServletResponseWrapper {
	private PrintWriterEmbed myWriter;
	private ServletOutputStreamEmbed myOutputStream;

	public HttpServletResponseEmbed(HttpServletResponse response) {
		super(response);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		myWriter = new PrintWriterEmbed(super.getWriter());
		return myWriter;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		myOutputStream = new ServletOutputStreamEmbed(super.getOutputStream());
		return myOutputStream;
	}

	public PrintWriterEmbed getMyWriter() {
		return myWriter;
	}

	public ServletOutputStreamEmbed getMyOutputStream() {
		return myOutputStream;
	}

}