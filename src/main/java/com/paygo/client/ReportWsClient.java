package com.paygo.client;

import com.paygocreditreport.paygobackend.ws.reportws.*;

/**
 * client for BCR web service
 */
public interface ReportWsClient {

    USASearchResponse usaSearch(USASearchRequest request);

    CanadaSearchResponse canadaSearch(CanadaSearchRequest request);

    CreateAccountResponse createAccount(CreateAccountRequest request);

    GetReportResponse getReport(GetReportRequest request);

    OrderReportResponse orderReport(OrderReportRequest request);


}
