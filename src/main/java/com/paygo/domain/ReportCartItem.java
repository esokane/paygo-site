package com.paygo.domain;

import java.util.List;

/**
 * reports chosen by user but not bought yet
 */
public class ReportCartItem extends AbstractReport{

    int cartEntryId;
    List<CompanyTicker> tickers;

    public List<CompanyTicker> getTickers() {
        return tickers;
    }

    public void setTickers(List<CompanyTicker> tickers) {
        this.tickers = tickers;
    }

    public ReportCartItem() {
    }

    public int getCartEntryId() {
        return cartEntryId;
    }

    public void setCartEntryId(int cartEntryId) {
        this.cartEntryId = cartEntryId;
    }
}



