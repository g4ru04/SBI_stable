package tw.com.sbi.trackingEmbed.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseEmbedFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponseEmbed responseWrapper = new HttpServletResponseEmbed((HttpServletResponse) response);
		chain.doFilter(request, responseWrapper);
		PrintWriterEmbed writer = responseWrapper.getMyWriter();
		if (writer != null) {
			String content = writer.getContent();
//			Do What Ever You Want For Response
//			content = content.replace("公司", "國家大事");
			response.getWriter().write(content);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}