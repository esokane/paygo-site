package com.paygo.client;

import com.paygocreditreport.paygobackend.ws.reportws.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * mocked client for webservice.
 */
public class ReportWsClientMock implements ReportWsClient {

    private String wsAccountName;
    private String wsPassword;
    private String branch;

    public USASearchResponse usaSearch(USASearchRequest request) {
        USASearchResponse response = new USASearchResponse();
        USASearchResponse2 usaSearchResult = new USASearchResponse2();
        usaSearchResult.setSearchId("1");
        usaSearchResult.setCompletionCode(CompletionCodeEnum.SUCCESS);
        USACompanyInfo comp = new USACompanyInfo();
        comp.setStreetAddress("Main st.");
        comp.setBusinessName("Facebook");
        comp.setCity("San Diego");
        comp.setCompanyId("1");
        comp.setState("CA");
        comp.setZip("545321");
        USACompanyInfo comp1 = new USACompanyInfo();
        comp1.setStreetAddress("15th av.");
        comp1.setBusinessName("FacebookLTD");
        comp1.setCity("San Diego");
        comp1.setCompanyId("2");
        comp1.setState("PA");
        comp1.setZip("54532221");
        ArrayOfUSACompanyInfo arrayOfUSACompanyInfo = new ArrayOfUSACompanyInfo();
        arrayOfUSACompanyInfo.getUSACompanyInfo().add(comp);
        arrayOfUSACompanyInfo.getUSACompanyInfo().add(comp1);
        usaSearchResult.setCompanies(arrayOfUSACompanyInfo);
        response.setUSASearchResult(usaSearchResult);
        return response;
    }

    public CanadaSearchResponse canadaSearch(CanadaSearchRequest request) {
        return null;
    }

    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        return null;
    }

    public GetReportResponse getReport(GetReportRequest request) {
        GetReportResponse response = new GetReportResponse();
        GetReportResponse2 response2 = new GetReportResponse2();
        response.setGetReportResult(response2);
        Path newFile = Paths.get("D:\\IdeaProjects\\paygo\\src\\main\\resources\\BCR Advantage Fusion Web Service (1).pdf");
        if (Files.exists(newFile)) {
            try {
                byte[] bytes = Files.readAllBytes(newFile);
                response.getGetReportResult().setPDF(bytes);
                response.getGetReportResult().setCompletionCode(CompletionCodeEnum.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
                response.getGetReportResult().setCompletionCode(CompletionCodeEnum.ERROR);
            }
        }
        return response;
    }

    public OrderReportResponse orderReport2(OrderReportRequest request) {
        OrderReportResponse response = new OrderReportResponse();
        OrderReportResponse2 response2 = new OrderReportResponse2();
        response2.setRequestId("200");
        response.setOrderReportResult(response2);
        return response;
    }

    public OrderReportResponse orderReport(OrderReportRequest request) {
        OrderReportResponse response = new OrderReportResponse();
        OrderReportResponse2 response2 = new OrderReportResponse2();
        if (request.getRequestId() != 0){
            response2.setCompletionCode(CompletionCodeEnum.SUCCESS);
        }
        else {
            response2.setCompletionCode(CompletionCodeEnum.SELECT_COMPANY_TICKER);
            CompanyTicker ticker = new CompanyTicker();
            ticker.setTicker("111");
            ticker.setAddress("Main av 423");
            ticker.setCompanyName("Facebook1");
            CompanyTicker ticker2 = new CompanyTicker();
            ticker2.setTicker("222");
            ticker2.setAddress("lane av 423");
            ticker2.setCompanyName("Facebook2");
            ArrayOfCompanyTicker arrayOfCompanyTicker = new ArrayOfCompanyTicker();
            arrayOfCompanyTicker.getCompanyTicker().add(ticker);
            arrayOfCompanyTicker.getCompanyTicker().add(ticker2);
            response2.setCompanyTickers(arrayOfCompanyTicker);
        }
        response2.setRequestId(String.valueOf(new Random().nextInt(10000)));
        response.setOrderReportResult(response2);
        return response;
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
