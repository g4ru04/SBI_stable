package tw.com.sbi.trackingEmbed.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ErrorServletEmbed extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ErrorServletEmbed.class);
	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		doGet(request, response);
	}
    public void doGet(HttpServletRequest request, HttpServletResponse response){
       Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
       logger.fatal("ComesHereWhileYouGuysLetJavaErrorGo.");
       logger.fatal("RecordFailOnCatch.",throwable);
    }
}