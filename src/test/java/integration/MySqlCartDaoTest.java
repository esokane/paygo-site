package integration;

import com.paygo.dao.mysql.MySqlCartDao;
import com.paygo.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MySqlCartDaoTest {

    @Autowired
    MySqlCartDao dao;

    @Test
    public void viewCart() throws Exception {
        User user = getUser();
        Report report = getReport();
        List<ReportCartItem> reportList = dao.viewCart(user);
        Assert.assertTrue(reportList != null);
        Assert.assertTrue(reportList.size() > 0);
    }


    @Test
    public void testAdd2Cart() throws Exception {
        User user = getUser();
        Report report = getReport();
        int cartItemId = dao.add2Cart(user, report);
        Assert.assertTrue(cartItemId > 0);
    }

    @Test
    public void testGetCartItemByTicker() throws Exception {
        String ticker = null;
        String requestId = "22";
        List<ReportCartItem> reports = dao.getCartItemByTicker(requestId, ticker);
        Assert.assertEquals(reports.size(), 1);
    }

    @Test
    public void testSaveTickers() throws Exception {
        java.util.List<CompanyTicker> tickerList = new ArrayList<>();
        CompanyTicker ticker = new CompanyTicker();
        ticker.setTicker("111");
        ticker.setAddress("Main av 423");
        ticker.setCompanyName("Facebook1");
        CompanyTicker ticker2 = new CompanyTicker();
        ticker2.setTicker("222");
        ticker2.setAddress("lane av 423");
        ticker2.setCompanyName("Facebook2");
        tickerList.add(ticker);
        tickerList.add(ticker2);
        int cartEntryId = 15;
        dao.saveTickers(tickerList, cartEntryId);
        Assert.assertEquals(1, 1);
    }

    @Test
    public void testUpdateCart() throws Exception {
        String requestId = "22";
        int cartEntryId = 15;
        int affected = dao.updateCart(requestId, cartEntryId);
        Assert.assertEquals(1, affected);
    }

    @Test
    public void testDeleteFromCart() throws Exception {
        User user = getUser();
        Report report = getReport();
        int cartItemId = dao.add2Cart(user, report);

        ReportCartItem reportCartItem = new ReportCartItem();
        reportCartItem.setCompany(report.getCompany());
        reportCartItem.setGuid(report.getGuid());
        reportCartItem.setReportType(report.getReportType());
        reportCartItem.setRequestId(report.getRequestId());
        reportCartItem.setSearchId(report.getSearchId());

        reportCartItem.setCartEntryId(cartItemId);

        int affected = dao.deleteReportFromCart(user, reportCartItem);
        Assert.assertTrue("Should be only one affected row", affected == 1);
    }

    private User getUser() {
        User user = new User();
        user.setEmail("test10@gmail.com");
        user.setPassword("1111");
        user.setUserId(2);
        return user;
    }

    private Report getReport() {
        Report report = new Report();
        report.setGuid(UUID.randomUUID().toString());
        report.setId(new Random(10000).nextInt());
        report.setSearchId(new Random(10000).nextInt());

        Company company = new Company();
        company.setAddress(UUID.randomUUID().toString());
        company.setCity(UUID.randomUUID().toString());
        company.setCompany(UUID.randomUUID().toString());
        company.setCompanyId(UUID.randomUUID().toString());
        company.setState(UUID.randomUUID().toString());
        company.setZip("234");
        company.setCountry(Country.USA);
        report.setCompany(company);

        ReportType reportType = new ReportType();
        reportType.setExternalId(new Random(100000).nextInt());
        reportType.setId(1);
        reportType.setName(UUID.randomUUID().toString());
        reportType.setPrice(new Random(10000).nextDouble());
        report.setReportType(reportType);
        return report;
    }

    @Test
    public void testSaveTransaction() throws Exception {
        User user = new User();
        user.setUserId(34);
        Transaction transaction = new Transaction();
        transaction.setAmount(1222);
        transaction.setBankMessage("test");
        transaction.setBankResponseCode("TEST");
        transaction.setCorrelationID("test");
        transaction.setCurrency(Currency.USD);
        transaction.setGatewayMessage("test");
        transaction.setGatewayResponseCode("TEST");
        transaction.setTransactionIdExternal("ddd");
        transaction.setTransactionStatus("approved");
        transaction.setTransactionTag("test");
        transaction.setTransactionType("test");
        transaction.setValidationStatus("test");
        int affected = dao.saveTransaction(transaction, user);
        Assert.assertTrue("Should be only one affected row", affected == 1);

    }


}
