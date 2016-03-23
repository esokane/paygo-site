package com.paygo;

import org.springframework.web.context.support.HttpRequestHandlerServlet;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class com.paygo.ReportHttpServlet
 */
@WebServlet(description = "Http Servlet for backend", urlPatterns = { "/reportServlet/*" }, name = "reportServletHandler")
public class ReportHttpServlet extends HttpRequestHandlerServlet {

    private static final long serialVersionUID = 1L;
}
