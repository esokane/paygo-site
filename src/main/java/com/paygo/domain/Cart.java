package com.paygo.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for user's cart
 */
public class Cart {

    private List<Report> reports = new ArrayList<Report>();

    public List<Report> getReports() { return reports; }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void add(Report request) {
        reports.add(request);
    }

}
