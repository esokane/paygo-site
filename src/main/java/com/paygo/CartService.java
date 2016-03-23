package com.paygo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paygocreditreport.paygobackend.ws.reportws.CanadaCompanyInfo;
import com.paygocreditreport.paygobackend.ws.reportws.CanadaSearchResponse;
import com.paygocreditreport.paygobackend.ws.reportws.USACompanyInfo;
import com.paygocreditreport.paygobackend.ws.reportws.USASearchResponse;
import com.paygo.dao.CartDao;
import com.paygo.dao.ReportDao;
import com.paygo.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.paygo.utils.JSONUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * class for operations with cart
 */
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private JSONUtils jsonUtils;
    private CartDao cartDao;
    private ReportDao reportDao;


    public String add2Cart(HttpServletRequest request) {
        Report orderReport;
        try {
            InputStream is = request.getInputStream();
            orderReport = jsonUtils.processJSON2Object(is, Report.class);
        } catch (IOException e) {
            logger.error("Failed to obtain input stream from request", e);
            return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
        }
        ReportType reportType = reportDao.getReportType(orderReport.getReportType().getId());
        orderReport.setReportType(reportType);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        USASearchResponse usaSearchResponse = (USASearchResponse) session.getAttribute(Constants.SESSION_ATTRIBUTE_USA_SEARCH_RESPONSE);
        if (usaSearchResponse != null) {
            fillOrderReportFromUSARequest(orderReport, usaSearchResponse);
        } else {
            CanadaSearchResponse canadaSearchResponse = (CanadaSearchResponse) session.getAttribute(Constants.SESSION_ATTRIBUTE_CANADA_SEARCH_RESPONSE);
            if (canadaSearchResponse != null) {
                fillOrderReportFromCanadaRequest(orderReport, canadaSearchResponse);
            } else {
                return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
            }
        }
        try {
            cartDao.add2Cart(user, orderReport);
        } catch (Exception e) {
            logger.error("Failed to add to cart", e);
            return ServiceConstants.CANT_ADD_2_CART_JSON_ERROR;
        }

        return ServiceConstants.JSON_SUCCESS;
    }

    private void fillOrderReportFromUSARequest(Report orderReport,
                                               USASearchResponse usaSearchResponse) {
        USACompanyInfo company = null;
        for (USACompanyInfo comp : usaSearchResponse.getUSASearchResult().getCompanies().getUSACompanyInfo()) {
            if (comp.getCompanyId().equals(String.valueOf(orderReport.getCompany().getCompanyId()))) {
                company = comp;
                break;
            }
        }
        if (company == null) {// user can't order the report without previous search companies
            return;
        }
        orderReport.getCompany().setAddress(company.getStreetAddress());
        orderReport.getCompany().setCity(company.getCity());
        orderReport.getCompany().setCountry(Country.USA);
        orderReport.getCompany().setCompany(company.getBusinessName());
        orderReport.getCompany().setCompanyId(company.getCompanyId());
        orderReport.getCompany().setState(company.getState());
        orderReport.getCompany().setZip(company.getZip());
        orderReport.setSearchId(Integer.parseInt(usaSearchResponse.getUSASearchResult().getSearchId()));
    }

    private void fillOrderReportFromCanadaRequest(Report orderReport,
                                                  CanadaSearchResponse canadaSearchResponse) {
        CanadaCompanyInfo company = null;
        for (CanadaCompanyInfo comp : canadaSearchResponse.getCanadaSearchResult().getCompanies().getCanadaCompanyInfo()) {
            if (comp.getCompanyId().equals(orderReport.getCompany().getCompanyId())) {
                company = comp;
                break;
            }
        }
        if (company == null) { // user can't order the report without previous search companies
            return;
        }
        orderReport.getCompany().setAddress(company.getAddress());
        orderReport.getCompany().setCity(company.getCity());
        orderReport.getCompany().setCountry(Country.CANADA);
        orderReport.getCompany().setCompany(company.getCompanyName());
        orderReport.getCompany().setCompanyId(company.getCompanyId());
        orderReport.getCompany().setState(company.getProvince());
        orderReport.getCompany().setZip(company.getPostalCode());
        orderReport.setSearchId(Integer.parseInt(canadaSearchResponse.getCanadaSearchResult().getSearchId()));
    }

    public String viewCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        try {
            return jsonUtils.processObject2Json(getCart(user));
        } catch (JsonProcessingException e) {
            logger.error("Failed to process object to JSON", e);
            return ServiceConstants.VIEW_CART_FAIL_JSON_ERROR;

        }
    }

    public List<ReportCartItem> getCart(User user) {
        return cartDao.viewCart(user);
    }

    public List<ReportCartItem> getCartItemByTicker(String requestId, String ticker) throws Exception{
        return cartDao.getCartItemByTicker(requestId, ticker);
    }

    public void saveTickers(ReportCartItem reportCartItem) throws Exception {
        cartDao.updateCart(reportCartItem.getRequestId(), reportCartItem.getCartEntryId());
        cartDao.saveTickers(reportCartItem.getTickers(), reportCartItem.getCartEntryId());
    }

    public String deleteFromCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        InputStream is;
        try {
            is = request.getInputStream();
        } catch (IOException e) {
            logger.error("Failed to obtain input stream from request", e);
            return ServiceConstants.DELETE_FAILED_JSON_ERROR;
        }
        ReportCartItem reportCartItem = null;
        try {
            reportCartItem = jsonUtils.processJSON2Object(is, ReportCartItem.class);
        } catch (IOException e) {
            logger.error("Failed to process json to object", e);
            return ServiceConstants.DELETE_FAILED_JSON_ERROR;
        }
        try {
            cartDao.deleteReportFromCart(user, reportCartItem);
        } catch (Exception e) {
            logger.error("Failed to delete report from cart", e);
            return ServiceConstants.DELETE_FAILED_JSON_ERROR;
        }
        return ServiceConstants.JSON_SUCCESS;
    }

    public void setJsonUtils(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public void setCartDao(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public void deleteCart(User user) throws Exception {
        cartDao.deleteCart(user);
    }
}
