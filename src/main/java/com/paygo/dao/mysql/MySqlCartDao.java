package com.paygo.dao.mysql;

import com.paygo.dao.CartDao;
import com.paygo.dao.mappers.ReportCartItemMapper;
import com.paygo.domain.CompanyTicker;
import com.paygo.domain.Report;
import com.paygo.domain.ReportCartItem;
import com.paygo.domain.Transaction;
import com.paygo.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlCartDao extends NamedParameterJdbcDaoSupport implements CartDao {
    private static final Logger logger = LoggerFactory.getLogger(MySqlCartDao.class);

    public final static String GET_CART_BY_USER_SQL = "select cart_entry_id," +
            "user_id,\n" +
            "company_name,\n" +
            "company_address,\n" +
            "c.report_type_id,\n" +
            "search_id,\n" +
            "company_country,\n" +
            "company_city,\n" +
            "company_state,\n" +
            "company_zip, \n" +
            "request_id, \n" +
            "rt.report_type_id, \n" +
            "rt.name,\n" +
            "rt.price\n" +
            "from cart c \n" +
            "left join reporttypes rt on rt.report_type_id =  c.report_type_id \n" +
            "where  user_id = :user_id";

    public final static String DELETE_FROM_CART = "delete from cart where user_id = :user_id and cart_entry_id = :cart_entry_id";

    public final static String DELETE_CART = "delete from cart where user_id = :user_id";

    public final static String UPDATE_CART = "update cart c set c.request_id = :request_id " +
            " where c.cart_entry_id = :cart_entry_id";

    public final static String INSERT_TICKER = "INSERT INTO ticker (cart_entry_id, ticker, company_name, company_address) " +
            "VALUES (?, ?, ?, ?)";

    public final static String INSERT_TRANSACTION = "INSERT INTO transactions \n" +
            " (trans_tag, trans_id_external, trans_type, user_id, amount, currency, trans_status,\n" +
            " validation_status, bank_resp_code, bank_message, gateway_resp_code, gateway_message,\n" +
            " correlation_id)\n " +
            " VALUES(:trans_tag, :trans_id_external, :trans_type, :user_id, :amount, :currency, " +
            " :trans_status, :validation_status, :bank_resp_code, :bank_message," +
            " :gateway_resp_code, :gateway_message, :correlation_id) ";


    public final static String GET_CART_ITEM_BY_TICKER = "select DISTINCT c.cart_entry_id, " +
            "user_id,\n" +
            "c.company_name,\n" +
            "c.company_address,\n" +
            "c.report_type_id,\n" +
            "search_id,\n" +
            "company_country,\n" +
            "company_city,\n" +
            "company_state,\n" +
            "company_zip, \n" +
            "request_id, \n" +
            "rt.report_type_id, \n" +
            "rt.name,\n" +
            "rt.price\n" +
            " from cart c " +
            " join ticker t on c.cart_entry_id = t.cart_entry_id " +
            "left join reporttypes rt on rt.report_type_id =  c.report_type_id \n" +
            " where c.request_id = :request_id and (:ticker is null OR t.ticker = :ticker) ";

    public MySqlCartDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<ReportCartItem> viewCart(User user) {
        logger.debug("viewCart ({}) -> started", user);
        int userId = user.getUserId();
        List<ReportCartItem> reports = getNamedParameterJdbcTemplate().query(GET_CART_BY_USER_SQL, Collections.singletonMap("user_id", userId), new ReportCartItemMapper());
        logger.debug("viewCart -> ended. reportList: [{}]", reports);
        return reports;
    }

    public List<ReportCartItem> getCartItemByTicker(String requestId, String ticker) throws Exception {
        logger.debug("getCartItemByTicker ({},{}) -> started", requestId, ticker);
        List<ReportCartItem> reports;
        try {
            Map<String, String> namedParameters = new HashMap<>();
            namedParameters.put("request_id",requestId);
            namedParameters.put("ticker", ticker);
            reports = getNamedParameterJdbcTemplate().query(GET_CART_ITEM_BY_TICKER, namedParameters, new ReportCartItemMapper());
        } catch (Exception e) {
            logger.error("Failed to get cart item by ticker", e);
            throw e;
        }
        logger.debug("getCartItemByTicker -> ended. reportList: [{}]", reports);
        return reports;
    }

    public int add2Cart(User user, Report report) throws Exception {
        logger.debug("add2Cart([{}],[{}]) -> started", user, report);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate()).
                withProcedureName("add_2_cart");
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user.getUserId());
        params.put("company_name", report.getCompany().getCompany());
        params.put("company_address", report.getCompany().getAddress());
        params.put("report_type_id", report.getReportType().getId());
        params.put("search_id", report.getSearchId());
        params.put("company_country", report.getCompany().getCountry().getName());
        params.put("company_city", report.getCompany().getCity());
        params.put("company_state", report.getCompany().getState());
        params.put("company_zip", report.getCompany().getZip());
        SqlParameterSource in = new MapSqlParameterSource().addValues(params);
        Map<String, Object> out;
        try {
            out = jdbcCall.execute(in);
        } catch (Exception e) {
            logger.error("Error occurred while adding to cart", e);
            throw e;
        }
        int result = (int) out.get("cart_entry_id");
        logger.debug("add2Cart -> ended. result: [{}]", result);
        return result;
    }

    @Override
    public int deleteReportFromCart(User user, ReportCartItem report) throws Exception {
        logger.debug("deleteReportFromCart({}) -> started");
        int userId = user.getUserId();
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        params.put("cart_entry_id", String.valueOf(report.getCartEntryId()));
        int affected = 0;
        try {
            affected = getNamedParameterJdbcTemplate().update(DELETE_FROM_CART, params);
        } catch (Exception e) {
            logger.error("Error occurred while deleting from cart", e);
            throw e;
        }
        logger.debug("deleteReportFromCart -> ended. [{}] rows deleted", affected);
        return affected;
    }

    @Override
    public int deleteCart(User user) throws Exception {
        logger.debug("deleteReportFromCart({}) -> started");
        int userId = user.getUserId();
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        int affected = 0;
        try {
            affected = getNamedParameterJdbcTemplate().update(DELETE_CART, params);
        } catch (Exception e) {
            logger.error("Error occurred while deleting cart", e);
            throw e;
        }
        logger.debug("deleteCart -> ended. [{}] rows deleted", affected);
        return affected;
    }

    @Override
    public int updateCart(String requestId, int cartEntryId) throws Exception {
        logger.debug("updateCart({}) -> started");
        HashMap<String, String> params = new HashMap<>();
        params.put("request_id", String.valueOf(requestId));
        params.put("cart_entry_id", String.valueOf(cartEntryId));
        int affected = 0;
        try {
            affected = getNamedParameterJdbcTemplate().update(UPDATE_CART, params);
        } catch (Exception e) {
            logger.error("Error occurred while updating cart", e);
            throw e;
        }
        logger.debug("updateCart -> ended. [{}] rows deleted", affected);
        return affected;
    }

    @Override
    public int saveTransaction(Transaction transaction, User user) throws Exception {
        logger.debug("saveTransaction({}) -> started");
        HashMap<String, String> params = new HashMap<>();
        params.put("trans_id_external", transaction.getTransactionIdExternal());
        params.put("trans_type", transaction.getTransactionType());
        params.put("user_id", String.valueOf(user.getUserId()));
        params.put("amount", String.valueOf(transaction.getAmount()));
        params.put("currency", String.valueOf(transaction.getCurrency().getValue()));
        params.put("trans_status", transaction.getTransactionStatus());
        params.put("validation_status", transaction.getValidationStatus());
        params.put("bank_resp_code", transaction.getBankResponseCode());
        params.put("bank_message", transaction.getBankMessage());
        params.put("gateway_resp_code", transaction.getGatewayResponseCode());
        params.put("gateway_message", transaction.getGatewayMessage());
        params.put("correlation_id", transaction.getCorrelationID());
        params.put("trans_tag", transaction.getTransactionTag());
        int affected = 0;
        try {
            affected = getNamedParameterJdbcTemplate().update(INSERT_TRANSACTION, params);
        } catch (Exception e) {
            logger.error("Error occurred while saving transaction", e);
            throw e;
        }
        logger.debug("saveTransaction -> ended. [{}] rows inserted", affected);
        return affected;
    }

    @Override
    public void saveTickers(final List<CompanyTicker> tickers, int cartEntryId) throws Exception {
        logger.debug("saveTickers({}) -> started");
        try{
        getJdbcTemplate().batchUpdate(INSERT_TICKER,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        CompanyTicker ticker = tickers.get(i);
                        ps.setInt(1, cartEntryId);
                        ps.setString(2, ticker.getTicker());
                        ps.setString(3, ticker.getCompanyName());
                        ps.setString(4, ticker.getAddress());
                    }

                    @Override
                    public int getBatchSize() {
                        return tickers.size();
                    }
                });
        } catch (Exception e) {
            logger.error("Error occurred while saving ticker", e);
            throw e;
        }
        logger.debug("saveTickers -> ended.");
    }

}