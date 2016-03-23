package com.paygo.dao.mappers;

import com.paygo.domain.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mapper for dao
 */
public class ReportCartItemMapper implements RowMapper<ReportCartItem> {

    public ReportCartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReportCartItem report = new ReportCartItem();
        report.setCartEntryId(rs.getInt("cart_entry_id"));
        Company company = new Company();
        company.setCompany(rs.getString("company_name"));
        company.setAddress(rs.getString("company_address"));
        company.setCity(rs.getString("company_city"));
        if (rs.getString("company_country") != null) {
            company.setCountry(Country.valueOf(rs.getString("company_country")));
        }
        company.setZip(rs.getString("company_zip"));
        company.setState(rs.getString("company_state"));
        report.setCompany(company);
        ReportType reportType = new ReportType();
        reportType.setId(rs.getInt("report_type_id"));
        reportType.setPrice(rs.getDouble("price"));
        reportType.setName(rs.getString("name"));
        report.setReportType(reportType);
        report.setSearchId(rs.getInt("search_id"));
        report.setRequestId(rs.getString("request_id"));
        return report;
    }
}
