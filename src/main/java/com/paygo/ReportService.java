package com.paygo;

import com.paygo.client.ReportWsClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.paygocreditreport.paygobackend.ws.reportws.*;
import com.paygo.dao.AccountDao;
import com.paygo.dao.ReportDao;
import com.paygo.domain.*;
import com.paygo.domain.CompanyTicker;
import com.paygo.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.paygo.utils.JSONUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * class for report operations
 */
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private ReportWsClient reportWSClient;
    private JSONUtils jsonUtils;
    private AccountDao accountDao;
    private ReportDao reportDao;
    private CartService cartService;
    private  EmailSender emailSender;

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public String searchCompanies(HttpServletRequest request) {
        int country;
        Company company;
        try {
            company = jsonUtils.processJSON2Object(request.getInputStream(), Company.class);
            String inputState = company.getState();
            if (inputState.length() == 3) {
                company.setState(inputState.substring(0, 2));
                country = Integer.parseInt(inputState.substring(2, 3));
            } else {
                return ServiceConstants.INCORRECT_STATE_JSON_ERROR;
            }
            HttpSession session = request.getSession();
            if (country == Country.USA.getValue()) {
                return searchUSACompanies(company.getState(), company.getCompany(), company.getCity(), session);
            } else {
                return searchCanadaCompanies(company.getState(), company.getCompany(), company.getCity(), session);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
        }
    }

    private String searchCanadaCompanies(String state, String compName, String city, HttpSession session) throws JsonProcessingException {
        CanadaSearchRequest request = new CanadaSearchRequest();
        request.setProvince(state);
        request.setCompany(compName);
        request.setCity(city);
        CanadaSearchResponse response = reportWSClient.canadaSearch(request);

        if (response.getCanadaSearchResult().getCompletionCode() == CompletionCodeEnum.SUCCESS) {
            session.setAttribute(Constants.SESSION_ATTRIBUTE_USA_SEARCH_RESPONSE, null);  // only the last search stored
            session.setAttribute(Constants.SESSION_ATTRIBUTE_CANADA_SEARCH_RESPONSE, response);
            return jsonUtils.processObject2Json(fillCanadaCompanyList(response));
        } else {
            return jsonUtils.processObject2Json(fillCompletionCode(response.getCanadaSearchResult()));
        }
    }

    private String searchUSACompanies(String state, String compName, String city, HttpSession session) throws JsonProcessingException {
        USASearchRequest request = new USASearchRequest();
        request.setState(state);
        request.setCompany(compName);
        request.setCity(city);
        USASearchSubject subject = new USASearchSubject();
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(System.currentTimeMillis());
        /*dob is a mandatory field, send any date */
        XMLGregorianCalendar dob = null;
        try {
            dob = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        subject.setDOB(dob);
        request.setSubject(subject);
        request.setSubject2(subject);
        request.setSubject3(subject);
        request.setSubject4(subject);
        request.setPermissiblePurpose(PermissiblePurposeEnum.NONE);
        USASearchResponse response = reportWSClient.usaSearch(request);
        if (response.getUSASearchResult().getCompletionCode() == CompletionCodeEnum.SUCCESS) {
            session.setAttribute(Constants.SESSION_ATTRIBUTE_CANADA_SEARCH_RESPONSE, null);
            session.setAttribute(Constants.SESSION_ATTRIBUTE_USA_SEARCH_RESPONSE, response);
            return jsonUtils.processObject2Json(fillUSACompanyList(response));
        } else if (response.getUSASearchResult().getCompletionCode() == CompletionCodeEnum.ERROR) {
            return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
        } else {
            return ServiceConstants.NOT_FOUND_JSON_ERROR;
        }
    }


    private List<Company> fillUSACompanyList(USASearchResponse response) {
        List<Company> jsonList = new ArrayList<Company>();
        for (USACompanyInfo comp : response.getUSASearchResult().getCompanies().getUSACompanyInfo()) {
            Company jsonComp = new Company();
            jsonComp.setAddress(comp.getStreetAddress());
            jsonComp.setCompany(comp.getBusinessName());
            jsonComp.setCity(comp.getCity());
            jsonComp.setZip(comp.getZip());
            jsonComp.setState(comp.getState());
            jsonComp.setCompanyId(comp.getCompanyId());
            jsonList.add(jsonComp);
        }
        return jsonList;
    }

    private List<Company> fillCanadaCompanyList(CanadaSearchResponse response) {
        List<Company> jsonList = new ArrayList<>();
        for (CanadaCompanyInfo comp : response.getCanadaSearchResult().getCompanies().getCanadaCompanyInfo()) {
            Company jsonComp = new Company();
            jsonComp.setAddress(comp.getAddress());
            jsonComp.setCompany(comp.getCompanyName());
            jsonComp.setCity(comp.getCity());
            jsonComp.setZip(comp.getPostalCode());
            jsonComp.setState(comp.getProvince());
            jsonComp.setCompanyId(comp.getCompanyId());
            jsonList.add(jsonComp);
        }
        return jsonList;
    }

    private List<CompletionCode> fillCompletionCode(WSResponse response) {
        List<CompletionCode> jsonList = new ArrayList<CompletionCode>();
        CompletionCode completionCode = new CompletionCode();
        completionCode.setCode(response.getErrorCode());
        completionCode.setName(response.getCompletionCode().name());
        completionCode.setMessage(response.getInternalErrorDescription());
        jsonList.add(completionCode);
        return jsonList;
    }

    public void setReportWSClient(ReportWsClient reportWSClient) {
        this.reportWSClient = reportWSClient;
    }


    public String checkout(HttpServletRequest incomeRequest) {
        HttpSession session = incomeRequest.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        List<ReportCartItem> cart = cartService.getCart(user);
        try {
            User userJSON = jsonUtils.processJSON2Object(incomeRequest.getInputStream(), User.class);
            fillUser(user, userJSON);
        } catch (IOException e) {
            logger.error("Failed to obtain input stream from request", e);
            return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
        }
               /*user updated address while paying for the report*/
        try {
             /*user updated address while paying for the report*/
            accountDao.createOrUpdateAddress(user.getAddress(), user.getUserId());
            /*user updated card while paying for the report*/
            accountDao.createOrUpdateCard(user.getCard(), user.getUserId());
            /*updating user in session */
            session.setAttribute(Constants.SESSION_ATTRIBUTE_USER, user);
        } catch (Exception e) {
            logger.error("Checkout error. Error while adding user information to the database", e);
            return ServiceConstants.UNEXPECTED_JSON_ERROR;
        }
        /*list of tickers for user to choose. Every OrderReport response can have a list of tickers  */
        List<ReportCartItem> tickerList = new ArrayList<>();
        try {
            // todo: processing payment card method
            payment();
            for (ReportCartItem reportCartItem : cart) {
                /*fill the request for the order report web service */
                OrderReportRequest request = fillOrderReportRequest(reportCartItem);
                /* request the web service for the report */
                OrderReportResponse response = reportWSClient.orderReport(request);
                switch (response.getOrderReportResult().getCompletionCode()) {
                    case SUCCESS: {
                        Report report = saveReport(reportCartItem, user, response);
                        user.getReports().add(report);
                        break;
                    }
                    case SELECT_COMPANY_TICKER: {
                        String requestId = response.getOrderReportResult().getRequestId();
                        List<com.paygo.domain.CompanyTicker> tickers = prepareTickers(response.getOrderReportResult().getCompanyTickers().getCompanyTicker());
                        reportCartItem.setRequestId(requestId);
                        reportCartItem.setTickers(tickers);
                        cartService.saveTickers(reportCartItem);
                        tickerList.add(reportCartItem);
                        break;
                    }
                    default: {
                        logger.error("OrderReport returned error ({})", response.getOrderReportResult());
                        return jsonUtils.error2Json(
                                response.getOrderReportResult().getErrorDescription());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Checkout error. Error while processing OrderReportRequest", e);
            return ServiceConstants.UNEXPECTED_JSON_ERROR;
        }
        String result;
        if (tickerList.isEmpty()) {
            result = ServiceConstants.JSON_SUCCESS;
            /* emptying user cart  */
            try {
                deleteCart(user);
            } catch (Exception e) {
                logger.error("Failed to delete cart", e);
            }
        } else {
            try {
                result = jsonUtils.processObject2Json(tickerList);
            } catch (JsonProcessingException e) {
                logger.error("Failed to process object to JSON ({})", tickerList, e);
                return ServiceConstants.UNEXPECTED_JSON_ERROR;
            }
        }
        return result;
    }


    public String selectTicker(HttpServletRequest incomeRequest) {
        HttpSession session = incomeRequest.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        List<ReportCartItem> tickerMap;
        try {
            tickerMap = jsonUtils.processJSON2List(incomeRequest.getInputStream(),
                    TypeFactory.defaultInstance()
                            .constructCollectionType(List.class, ReportCartItem.class));
        } catch (IOException e) {
            logger.error("Failed to obtain input stream from request", e);
            return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
        }
        try {
            for (ReportCartItem entry : tickerMap) {
                List<ReportCartItem> reports;
                try {
                    reports = cartService.getCartItemByTicker(entry.getRequestId(), entry.getTickers().get(0).getTicker());
                    if (reports.size() != 1) {
                        logger.error("Incorrect ticker or requestId in request");
                        return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
                    }
                } catch (Exception e) {
                    logger.error("Failed to find ticket in database", e);
                    return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
                }
                try {
                    ReportCartItem reportCartItem = reports.get(0);
             /*fill the request for the OrderReport web service */
                    OrderReportRequest request = fillOrderReportRequestWithTicker(reportCartItem, entry.getRequestId(),
                            entry.getTickers().get(0).getTicker());
                /* request the web service for the report */
                    OrderReportResponse response = reportWSClient.orderReport(request);
                    switch (response.getOrderReportResult().getCompletionCode()) {
                        case SUCCESS: {
                            Report report = saveReport(reportCartItem, user, response);
                            user.getReports().add(report);
                            break;
                        }
                        default: {
                            logger.error("OrderReport returned error ({})", response.getOrderReportResult());
                            return jsonUtils.error2Json(
                                    response.getOrderReportResult().getErrorDescription());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Failed get OrderReportResponse with requestId ({})", entry.getRequestId(), e);
                    return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to iterate tikers map", e);
            return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
        }
            /* emptying user cart  */
        try {
            deleteCart(user);
        } catch (Exception e) {
            logger.error("Failed to delete cart", e);
        }
        try {
            sendEmail(user, incomeRequest);
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
        }
        return ServiceConstants.JSON_SUCCESS;
    }

    private void sendEmail(User user, HttpServletRequest incomeRequest) throws MessagingException {
        emailSender.sendEmail(user.getEmail(), getMailParamsMap(user, incomeRequest));

    }

    public static Map<String, String> getMailParamsMap(User user, HttpServletRequest incomeRequest) {
        Map<String, String> parameters = new HashMap<>();
        List<Report> reports = user.getReports();
        String companyList = reports.get(0).getCompany().getCompany();
        double orderTotal = 0;
        for (Report report : reports) {
            companyList += ", " + report.getCompany().getCompany();
            orderTotal += report.getReportType().getPrice();
        }
        parameters.put("company_name", companyList);
        parameters.put("firstname", user.getFirstName());
        parameters.put("lastname", user.getLastName());
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        parameters.put("order_date", sdf.format(now));
        // todo with card processing
        parameters.put("card_type", UUID.randomUUID().toString());
        parameters.put("card_expire", String.valueOf(user.getCard().getExpireMM()) + "." + String.valueOf(user.getCard().getExpireYY()));
        // todo with card processing
        parameters.put("payment_id", UUID.randomUUID().toString());
        String ipAddress = incomeRequest.getRemoteAddr();
        parameters.put("order_ip_address", ipAddress);
        parameters.put("order_total", String.valueOf(orderTotal));
        return parameters;
    }

    private OrderReportRequest fillOrderReportRequestWithTicker(ReportCartItem reportCartItem, String requestId, String ticker) {
        OrderReportRequest request = new OrderReportRequest();
        request.setReportId(reportCartItem.getReportType().getId());
        if ((ticker != null) && (!ticker.isEmpty())) {
            request.setStage(StageEnum.SELECT_TICKER);
            request.setTickerSelection(ticker);
        } else {
            request.setStage(StageEnum.SKIP_TICKER);
        }
        request.setIncludeUCCFilings(true);
        request.setRequestId(Integer.valueOf(requestId));
        return request;
    }


    private List<CompanyTicker> prepareTickers(List<com.paygocreditreport.paygobackend.ws.reportws.CompanyTicker> externalTickers) {
        List<CompanyTicker> companyTickers = new ArrayList<>();
        for (com.paygocreditreport.paygobackend.ws.reportws.CompanyTicker externalTicker : externalTickers) {
            CompanyTicker ticker = new CompanyTicker();
            ticker.setCompanyName(externalTicker.getCompanyName());
            ticker.setAddress(externalTicker.getAddress());
            ticker.setTicker(externalTicker.getTicker());
            companyTickers.add(ticker);
        }
        return companyTickers;
    }

    private Report saveReport(ReportCartItem reportCartItem, User user, OrderReportResponse response) throws Exception {
        reportCartItem.setRequestId(response.getOrderReportResult().getRequestId());
        reportCartItem.setGuid(UUID.randomUUID().toString());
        Report report = new Report();
                /* save report info in the database, report will be linked to user */
        report.setId(reportDao.createReport(reportCartItem, user));
                /* save reports to user object to show them on order-complete.html */
        reportCartItem2Report(reportCartItem, report);
        return report;
    }

    private void reportCartItem2Report(ReportCartItem reportCartItem, Report report) {
        report.setRequestId(reportCartItem.getRequestId());
        report.setGuid(reportCartItem.getGuid());
        report.setSearchId(reportCartItem.getSearchId());
        report.setCompany(reportCartItem.getCompany());
        report.setReportType(reportCartItem.getReportType());
    }

    /*clear Ids for security reasons. returned object will be set to response*/
    private List<Report> prepareReports4Response(List<Report> reports) {
        for (Report report : reports) {
            report.setId(0);
            report.setRequestId("");
        }
        return reports;
    }


    private void deleteCart(User user) throws Exception {
        cartService.deleteCart(user);
    }

    private void payment() {
        // todo: process credit card payment
    }

    /*
    * user updated its data (new data - userJSON, old data - user).
      * if user previously had address or card update them with old ids. If user didn't have card or address before
      * add new data(ids = 0)
    * */
    private void fillUser(User user, User userJSON) {
        if (user.getAddress() != null) {
            int address_id = user.getAddress().getAddressId();
            user.setAddress(userJSON.getAddress());
            user.getAddress().setAddressId(address_id);
        } else {
            user.setAddress(userJSON.getAddress());
        }
        if (user.getCard() != null) {
            int card_id = user.getCard().getCardId();
            user.setCard(userJSON.getCard());
            user.getCard().setCardId(card_id);
        } else {
            user.setCard(userJSON.getCard());
        }
    }

    private OrderReportRequest fillOrderReportRequest(ReportCartItem report) {
        OrderReportRequest request = new OrderReportRequest();
        request.setReportId(report.getReportType().getId());
        request.setCompanyName(report.getCompany().getCompany());
        request.setSearchId(report.getSearchId());
        request.setStage(StageEnum.BEGINNING);
        request.setIncludeUCCFilings(true);
        request.setCompanyZip(report.getCompany().getZip());
        request.setCompanyState(report.getCompany().getState());
        request.setCompanyId(report.getCompany().getCompanyId());
        request.setCompanyAddress(report.getCompany().getAddress());
        request.setCompanyCity(report.getCompany().getCity());
        request.setCompanyCountry(report.getCompany().getCountry().getName());
        return request;
    }


    /*  this web service's method is currently not supported */
    private String createBCRAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CreateAccountRequest accountRequest = new CreateAccountRequest();
        accountRequest.setUserEmail(request.getParameter("email"));
        accountRequest.setUserFirstName(request.getParameter("firstname"));
        accountRequest.setUserLastName(request.getParameter("lastname"));
        accountRequest.setUserPassword(request.getParameter("password"));
        CreateAccountResponse accountResponse = reportWSClient.createAccount(accountRequest);
        return jsonUtils.processObject2Json(fillCompletionCode(accountResponse.getCreateAccountResult()));
    }

    public String getUserInfo(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        try {
            return jsonUtils.processObject2Json(user);
        } catch (JsonProcessingException e) {
            logger.error("Failed to process object to JSON ({})", user, e);
            return ServiceConstants.USER_INFO_FAIL_JSON_ERROR;
        }
    }

    public byte[] download(String guid) throws IOException {
        Report report = reportDao.getReport(guid);
        GetReportRequest request = new GetReportRequest();
            /*here is a mistake in a documentation : field GetReportRequest.RequestId is string in doc and int in wsdl.  */
        request.setRequestId(Integer.parseInt(report.getRequestId()));
        request.setReportFormat(ReportFormatEnum.PDF);
        GetReportResponse response = reportWSClient.getReport(request);
        if (response.getGetReportResult().getCompletionCode() == CompletionCodeEnum.SUCCESS)
            return response.getGetReportResult().getPDF();
        else {
            return null;
        }
    }

    public String getRecentReport(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        try {
            List<Report> reports = user.getReports();
            /*reset reports to null, because this list only for order-complete.html */
            user.setReports(new ArrayList<>());
            return jsonUtils.processObject2Json(prepareReports4Response(reports));
        } catch (JsonProcessingException e) {
            logger.error("Failed to process object to JSON ({})", user.getReports(), e);
            return ServiceConstants.GET_REPORTS_FAIL_JSON_ERROR;
        }
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void setJsonUtils(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public String getReports(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        try {
            return jsonUtils.processObject2Json(prepareReports4Response(reportDao.getUserReports(user)));
        } catch (JsonProcessingException e) {
            logger.error("Failed to process object to JSON", e);
            return ServiceConstants.GET_REPORTS_FAIL_JSON_ERROR;

        }
    }

}




