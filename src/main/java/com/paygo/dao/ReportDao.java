package com.paygo.dao;

import com.paygo.domain.Report;
import com.paygo.domain.ReportCartItem;
import com.paygo.domain.ReportType;
import com.paygo.domain.User;

import java.util.List;

/**
 * interface for db requests on reports' issues
 */
public interface ReportDao {


    ReportType getReportType(int reportId);
    int createReport(ReportCartItem report, User user) throws Exception;
    Report getReport(String guid);
    List<Report> getUserReports(User user);



}
