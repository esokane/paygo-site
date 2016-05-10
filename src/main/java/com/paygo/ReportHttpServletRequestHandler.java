package com.paygo;

import com.paygo.domain.Constants;
import com.paygo.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestHandler;
import com.paygo.utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * servlet handler for all requests from http
 */
public class ReportHttpServletRequestHandler implements HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReportHttpServletRequestHandler.class);

    private static final String SEARCH_RESULTS = "/reportServlet/searchResults";
    private static final String CREATE_ACCOUNT = "/reportServlet/createAccount";
    private static final String ADD_2_CART = "/reportServlet/add2Cart";
    private static final String VIEW_CART = "/reportServlet/viewCart";
    private static final String DELETE_FROM_CART = "/reportServlet/deleteFromCart";
    private static final String CHECKOUT = "/reportServlet/checkout";
    private static final String LOGOUT = "/reportServlet/logout";
    private static final String GET_USER_INFO = "/reportServlet/getUserInfo";
    private static final String DOWNLOAD = "/reportServlet/download";
    private static final String GET_RECENT_REPORTS = "/reportServlet/getRecentReports";
    private static final String GET_REPORTS = "/reportServlet/getReports";
    private static final String DELETE_USER = "/reportServlet/deleteUser";
    private static final String SAVE_USER = "/reportServlet/saveUser";
    private static final String SELECT_TICKER = "/reportServlet/selectTicker";
    private static final String GOOGLE_SIGNING = "/reportServlet/googleSignIn";


    private ReportService reportService;
    private CartService cartService;
    private AccountService accountService;

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = ServiceConstants.JSON_SUCCESS;
        logRequest(request);

        if (isSessionExpired(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result = ServiceConstants.UNAUTHORIZED_JSON_ERROR;
            logger.error(ServiceConstants.UNAUTHORIZED_JSON_ERROR);
        } else {
            try {
            /*the content type and status can be modified below*/
                response.setContentType("json");
                response.setStatus(HttpStatus.OK.value());
                String actionPath = request.getRequestURI();
                switch (actionPath) {
                    case SEARCH_RESULTS: {
                        result = searchResults(request);
                        break;
                    }
                    case CREATE_ACCOUNT: {
                        result = authorize(request);
                        break;
                    }
                    case GOOGLE_SIGNING: {
                        result = googleSignIn(request);
                        break;
                    }
                    case ADD_2_CART: {
                        result = add2Cart(request);
                        break;
                    }
                    case VIEW_CART: {
                        result = viewCart(request);
                        break;
                    }
                    case DELETE_FROM_CART: {
                        result = deleteFromCart(request);
                        break;
                    }
                    case CHECKOUT: {
                        result = checkout(request);
                        break;
                    }
                    case GET_USER_INFO: {
                        result = getUserInfo(request);
                        break;
                    }
                    case LOGOUT: {
                        logout(request);
                        break;
                    }
                    case DOWNLOAD: {
                        download(request, response);
                        break;
                    }
                    case GET_RECENT_REPORTS: {
                        result = getRecentReport(request);
                        break;
                    }
                    case GET_REPORTS: {
                        result = getReports(request);
                        break;
                    }
                    case DELETE_USER: {
                        result = deleteUser(request);
                        break;
                    }
                    case SAVE_USER: {
                        result = saveUser(request);
                        break;
                    }
                    case SELECT_TICKER: {
                        result = selectTicker(request);
                        break;
                    }
                    default: {
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        PrintWriter writer = response.getWriter();
                        writer.write("Method " + actionPath + " not found");
                        writer.flush();
                    }
                }
            } catch (Exception e) {
                logger.error("Internal error", e);
                result = ServiceConstants.UNEXPECTED_JSON_ERROR;
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        HttpUtils.writeResponse(response, result);
        logger.info(request.getRequestURI() + " -> ended.");
    }

    private String googleSignIn(HttpServletRequest request) throws IOException{
        return accountService.googleSignIn(request);
    }

    private boolean isSessionExpired(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        if (request.getHeader("cookie").contains("userData")) {
            if (user == null) {
                return true;
            } else {
                String[] cookies = request.getHeader("cookie").split(";");
                for (String cookieString : cookies) {
                    String[] cookie = cookieString.trim().split("=");
                    if ((cookie[0].trim().equals("userData"))&&(cookie[1].trim().equals("undefined"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void logRequest(HttpServletRequest request) {
        logger.info(request.getRequestURI() + " [{}] -> started", request.getHeader("cookie"));
    }

    private String selectTicker(HttpServletRequest request) throws IOException {
        return reportService.selectTicker(request);
    }

    private String getReports(HttpServletRequest request) throws IOException {
        return reportService.getReports(request);
    }

    private String saveUser(HttpServletRequest request) throws IOException {
        return accountService.saveUser(request);
    }

    private String deleteUser(HttpServletRequest request) throws IOException {
        return accountService.deleteUser(request);
    }

    // only for order-complete.html. Just bought reports
    private String getRecentReport(HttpServletRequest request) throws IOException {
        return reportService.getRecentReport(request);
    }

    private String getUserInfo(HttpServletRequest request) throws IOException {
        return reportService.getUserInfo(request);
    }

    private String checkout(HttpServletRequest request) throws ServletException, IOException {
        return reportService.checkout(request);
    }

    private void logout(HttpServletRequest request) throws ServletException, IOException {
        request.getSession().invalidate();
    }

    private String searchResults(HttpServletRequest request) throws IOException {
        return reportService.searchCompanies(request);
    }

    private String authorize(HttpServletRequest request) throws IOException {
        return accountService.authorize(request);
    }

    private String add2Cart(HttpServletRequest request) {
        return cartService.add2Cart(request);
    }

    private String viewCart(HttpServletRequest request) {
        return cartService.viewCart(request);
    }

    private String deleteFromCart(HttpServletRequest request) {
        return cartService.deleteFromCart(request);
    }

    private void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String guid = request.getParameter("guid");
        byte[] file = reportService.download(guid);
        if ((file == null) || (file.length == 0)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            HttpUtils.writeResponse(response, "file not found");
        } else {
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition",
                    "attachment; filename=\"Report.pdf\"");
            response.setHeader("Content-Length", String.valueOf(file.length));
            ServletOutputStream op = response.getOutputStream();
            op.write(file);
            op.flush();
        }
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}