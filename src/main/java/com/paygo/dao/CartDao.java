package com.paygo.dao;

import com.paygo.domain.CompanyTicker;
import com.paygo.domain.Report;
import com.paygo.domain.ReportCartItem;
import com.paygo.domain.User;

import java.util.List;

/**
 * dao for cart operations in DB
 */
public interface CartDao {

    /**
     * Returns cart by user
     *
     * @param user cart owner
     * @return List of reports in cart
     */
    List<ReportCartItem> viewCart(User user);

    /**
     * Adds report to cart
     *
     * @param user   cart owner
     * @param report report to add in cart
     * @return cart_entry_id identification of report in cart
     * @throws Exception
     */
    int add2Cart(User user, Report report) throws Exception;


    /**
     *
     * @param requestId - requestId from OrderReport response
     * @param ticker - ticker for company for second OrderReport request
     * @return report from cart
     * @throws Exception
     */
    List<ReportCartItem> getCartItemByTicker(String requestId, String ticker) throws Exception;

    /**
     * Deletes report from cart
     *
     * @param user   cart owner
     * @param report report for deletion from cart
     * @return the number of rows affected
     */
    int deleteReportFromCart(User user, ReportCartItem report) throws Exception ;

    /**
     * delete all reports from cart
     * @param user   cart owner
     * @return the number of rows affected
     */
    int deleteCart(User user) throws Exception ;

    /**
     * OrderReport request may return list of companies
     * for user to choose. Save list to database.
     * @param tickers
     * @param cartEntryId
     */
    void saveTickers(final List<CompanyTicker> tickers, int cartEntryId)  throws Exception ;

    /** update cart with requestId
     *
     * @param requestId
     * @param cartEntryId
     * @return
     */
    int updateCart(String requestId, int cartEntryId)  throws Exception ;

}
