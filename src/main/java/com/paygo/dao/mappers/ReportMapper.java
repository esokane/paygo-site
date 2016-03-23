package com.paygo.dao.mappers;

import com.paygo.domain.Company;
import com.paygo.domain.Country;
import com.paygo.domain.Report;
import com.paygo.domain.ReportType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mapper for dao
 */
public class ReportMapper implements RowMapper<Report> {
    public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("report_id"));
        report.setGuid(rs.getString("report_guid"));
        Company company = new Company();
        company.setCompany(rs.getString("company_name"));
        company.setAddress(rs.getString("company_address"));
        company.setCity(rs.getString("company_city"));
        if (rs.getString("company_country") != null) {
            company.setCountry(Country.valueOf(rs.getString("company_country")));
        }
        company.setZip(rs.getString("company_zip"));
        company.setState(rs.getString("company_state"));
        report.setRequestId(rs.getString("external_id"));
        report.setCompany(company);
        ReportType reportType = new ReportType();
        reportType.setId(rs.getInt("report_type_id"));
        reportType.setExternalId(rs.getInt("report_type_external_id"));
        reportType.setPrice(rs.getDouble("price"));
        reportType.setName(rs.getString("name"));
        report.setReportType(reportType);
        return report;
    }
}
