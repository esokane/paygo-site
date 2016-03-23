package com.paygo.dao.mappers;

import com.paygo.domain.ReportType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mapper for dao
 */
public class ReportTypeMapper implements RowMapper<ReportType> {
    public ReportType mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReportType reportType = new ReportType();
        reportType.setId(rs.getInt("report_type_id"));
        reportType.setExternalId(rs.getInt("external_id"));
        reportType.setPrice(rs.getDouble("price"));
        reportType.setName(rs.getString("name"));
        return reportType;
    }
}
