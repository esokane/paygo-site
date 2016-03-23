package integration;

import com.paygo.dao.mysql.MySqlReportDao;
import com.paygo.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MySqlReportDaoTest {

    @Autowired
    MySqlReportDao dao;

    @Test
    public void testGetReportType() {
        int reportId = 64;
        ReportType report = dao.getReportType(reportId);
        Assert.assertNotNull(report);
    }

    @Test
    public void testCreateReport() throws Exception {
        ReportCartItem report = new ReportCartItem();
        Company company = new Company();
        company.setCompany("Facebook333");
        company.setAddress("Parklane");
        company.setCity("Palo-Alto");
        company.setCountry(Country.USA);
        company.setState("CA");
        company.setZip("b6 - 785");
        report.setCompany(company);
        report.setGuid(UUID.randomUUID().toString());
        report.setRequestId("jjjj");
        ReportType reportType = new ReportType();
        reportType.setId(2);
        report.setReportType(reportType);
        User user = new User();
        user.setUserId(18);
        int result = dao.createReport(report, user);
        Assert.assertNotEquals(0, result);
    }

    @Test
    public void testGetReport() {
        String guid = "2af9e739-d4b5-11e5-b636-2c600c6b5a04";
        Report report = dao.getReport(guid);
        Assert.assertNotNull(report);
    }

    @Test
    public void testGetUserReports() {
        User user = new User();
        user.setUserId(34);
        List<Report> reportList = dao.getUserReports(user);
        Assert.assertNotNull(reportList);
        Assert.assertTrue(reportList.size() > 1);
    }

}
