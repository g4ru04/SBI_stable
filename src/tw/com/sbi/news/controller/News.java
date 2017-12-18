package tw.com.sbi.news.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import tw.com.sbi.common.controller.CommonMethod;

public class News extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(News.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		if ("onload".equals(action)) {
			NewsService newsService = new NewsService();
			String result = newsService.getNews();
			response.getWriter().write(result);
			return;
		}
	}

	public class NewsService {
		private News_interface dao;

		public NewsService() {
			dao = new NewsDAO();
		}

		public String getNews() {
			logger.info("into NewsService getNews function");
			return dao.getNews();
		}
	}

	interface News_interface {
		public String getNews();
	}

	class NewsDAO implements News_interface {

		private final String newsWebservice = getServletConfig().getServletContext()
				.getInitParameter("pythonNewsWebservice");

		@Override
		public String getNews() {
			logger.debug("get:"+newsWebservice);
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(newsWebservice);
			String content = null;
			try {
				client.executeMethod(method);
			} catch (Exception e) {
				logger.debug("\nexecute pythonNewsWebservice : {}", e.toString());
				return "{}";
			}
			try {
				StringWriter writer = new StringWriter();
				IOUtils.copy(method.getResponseBodyAsStream(), writer, "UTF-8");
				content = writer.toString();

				// Verify that the return type is correct
				
				try {
					JSONArray jsonarr = new JSONArray(content);
//					CommonMethod.lotsOfObjectFormat(jsonarr);
					logger.debug("content : {}", CommonMethod.lotsOfObjectFormat(jsonarr));
					Type type = new TypeToken<List<NewsPOJO>>() {
					}.getType();
					new Gson().fromJson(content, type);
				} catch (JsonParseException e) {
					logger.debug("\ncontent parse : {}", e.toString());
					return "{}";
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("\ngetNews : {}", e.toString());
					return "{}";
				}
			} catch (Exception e) {
				logger.debug("\ngetNews : {}", e.toString());
				return "{}";
			}
			method.releaseConnection();
			return content;
		}
	}
}

class NewsPOJO {
	String Url;
	String Type;
	String Title;
	String source;
}
