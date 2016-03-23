package com.paygo.domain;

/**
 * company to choose by user when company matches were found in BCR’s stock ticker database
 * used in ordering report process(cart checkout)
 */
public class CompanyTicker {

    private String companyName;
    private String address;
    private String ticker;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
