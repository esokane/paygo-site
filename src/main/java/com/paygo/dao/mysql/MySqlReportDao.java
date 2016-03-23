package com.paygo.dao.mysql;

import com.paygo.dao.ReportDao;
import com.paygo.dao.mappers.ReportMapper;
import com.paygo.dao.mappers.ReportTypeMapper;
import com.paygo.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Natali on 09.02.2016.
 */
public class MySqlReportDao extends NamedParameterJdbcDaoSupport implements ReportDao {
    private static final Logger logger = LoggerFactory.getLogger(MySqlCartDao.class);

    public final static String GET_REPORT_TYPE_SQL = "select report_type_id, name, price, external_id from reporttypes where external_id = ?;";
    public final static String GET_REPORT_BY_ID_SQL = "select report_id, report_guid, company_name, company_address, " +
            " company_city, company_country, company_zip, company_state, r.external_id, rt.report_type_id, " +
            " name, price, rt.external_id as report_type_external_id " +
            " from reports r " +
            " left join reporttypes rt on rt.report_type_id =  r.report_type_id " +
            " where  report_guid = :report_guid ";

    public final static String GET_REPORT_BY_USER_SQL = "select report_id, report_guid, company_name, company_address, " +
            " company_city, company_country, company_zip, company_state, r.external_id, rt.report_type_id, " +
            " name, price, rt.external_id as report_type_external_id " +
            " from reports r " +
            " left join reporttypes rt on rt.report_type_id =  r.report_type_id " +
            " where  user_id = :user_id ";


    /**
     * @param dataSource
     */
    public MySqlReportDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    // get report type by reportId
    public ReportType getReportType(int reportId) {
        logger.debug("getReportType([{}], [{}]) -> started", reportId);
        List<ReportType> reports;
        reports = getJdbcTemplate().query(GET_REPORT_TYPE_SQL, new Object[]{reportId}, new ReportTypeMapper());
        if (!reports.isEmpty()) {
            return reports.get(0);
        } else {
            return null;
        }
    }

    @Override
    public int createReport(ReportCartItem report, User user) throws Exception {
        logger.debug("createReport([{}], [{}]) -> started", report, user);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate()).
                withProcedureName("create_report");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getUserId());
        params.put("company_name", report.getCompany().getCompany());
        params.put("company_address", report.getCompany().getAddress());
        params.put("company_city", report.getCompany().getCity());
        params.put("company_state", report.getCompany().getState());
        params.put("company_country", report.getCompany().getCountry().getName());
        params.put("company_zip", report.getCompany().getZip());
        params.put("report_type_id", report.getReportType().getId());
        params.put("report_guid", report.getGuid());
        params.put("external_id", report.getRequestId());
        SqlParameterSource in = new MapSqlParameterSource().addValues(params);
        Map<String, Object> out;
        try {
            out = jdbcCall.execute(in);
        } catch (Exception e) {
            logger.error("Error occurred wile creating report", e);
            throw e;
        }
        int result = (int) out.get("report_id");
        logger.debug("createReport -> ended. result: [{}]", result);
        return result;
    }


    public Report getReport(String report_guid) {
        logger.debug("getReport [{}]) -> started", report_guid);
        List<Report> reports = getNamedParameterJdbcTemplate().query(GET_REPORT_BY_ID_SQL, Collections.singletonMap("report_guid", report_guid), new ReportMapper());
        Report result;
        if (!reports.isEmpty()) {
            result = reports.get(0);
        } else {
            result = null;
        }
        logger.debug("getReport [{}]) -> ended", result);
        return result;
    }


    public List<Report> getUserReports(User user) {
        logger.debug("getUserReports [{}]) -> started", user);
        int userId = user.getUserId();
        List<Report> reports = getNamedParameterJdbcTemplate().query(GET_REPORT_BY_USER_SQL, Collections.singletonMap("user_id", userId), new ReportMapper());
        logger.debug("getUserReports [{}]) -> ended", reports);
        if (!reports.isEmpty()) {
            return reports;
        } else {
            return null;
        }
    }

}
