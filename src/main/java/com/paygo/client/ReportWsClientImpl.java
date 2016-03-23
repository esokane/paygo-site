package com.paygo.client;

import com.paygocreditreport.paygobackend.ws.reportws.*;
import org.springframework.ws.client.core.WebServiceTemplate;

public class ReportWsClientImpl implements ReportWsClient {

    private WebServiceTemplate webServiceTemplate;

    private String wsAccountName;
    private String wsPassword;
    private String branch;

    public ReportWsClientImpl(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public USASearchResponse usaSearch(USASearchRequest request) {
        USASearch usaSearch = new USASearch();
        usaSearch.setRequest(fillWSRequest(request));
        USASearchResponse response = (USASearchResponse) webServiceTemplate.marshalSendAndReceive(usaSearch);
        return response;
    }

    public CanadaSearchResponse canadaSearch(CanadaSearchRequest request) {
        CanadaSearch canadaSearch = new CanadaSearch();
        canadaSearch.setRequest(fillWSRequest(request));
        CanadaSearchResponse response = (CanadaSearchResponse) webServiceTemplate.marshalSendAndReceive(canadaSearch);
        return response;
    }

    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        CreateAccount createAccount = new CreateAccount();
        createAccount.setRequest(fillWSRequest(request));
        CreateAccountResponse response = (CreateAccountResponse) webServiceTemplate.marshalSendAndReceive(createAccount);
        return response;


    }

    public GetReportResponse getReport(GetReportRequest request) {
        GetReport getReport = new GetReport();
        getReport.setRequest(fillWSRequest(request));
        GetReportResponse response = (GetReportResponse) webServiceTemplate.marshalSendAndReceive(getReport);
        return response;

    }

    public OrderReportResponse orderReport(OrderReportRequest request) {
        OrderReport orderReport = new OrderReport();
        orderReport.setRequest(fillWSRequest(request));
        OrderReportResponse response = (OrderReportResponse) webServiceTemplate.marshalSendAndReceive(orderReport);
        return response;
    }

    private <T extends WSRequest> T fillWSRequest(T request) {
        request.setBranch(branch);
        request.setWSAccountName(wsAccountName);
        request.setWSPassword(wsPassword);
        request.setTestMode(true);
        return request;
    }

    public void setWsPassword(String wsPassword) {
        this.wsPassword = wsPassword;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setWsAccountName(String wsAccountName) {
        this.wsAccountName = wsAccountName;
    }


}
