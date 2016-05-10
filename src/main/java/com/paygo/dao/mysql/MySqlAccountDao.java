package com.paygo.dao.mysql;

import com.paygo.dao.AccountDao;
import com.paygo.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MySqlAccountDao extends NamedParameterJdbcDaoSupport implements AccountDao {
    private static final Logger logger = LoggerFactory.getLogger(MySqlCartDao.class);

    public final static String GET_USER_SQL = "select u.id as user_id , u.first_name , u.last_name, u.pass, c.id as card_id,\n" +
            "    c.card_number, c.first_name, c.last_name, c.card_type, c.card_exp_month,\n" +
            "    c.card_exp_year, c.security_code, a.id as address_id, a.street1, a.city, a.zip, a.state,\n" +
            "    a.country, a.phone, a.street2, u.login \n" +
            "from users u\n" +
            "left join users_addresses ua on ua.user_id = u.id\n" +
            "left join addresses a on a.id = ua.address_id\n" +
            "left join users_cards uc on uc.user_id = u.id\n" +
            "left join cards c on c.id = uc.card_id where login = ? ";

    public final static String GET_USER_PASS = "select u.pass " +
            "from users u where login = ? ";

    public final static String DELETE_USER_SQL = "DELETE FROM users WHERE id = :user_id;";

    /**
     * Instantiates a new employee jdbc com.paygo.dao.
     *
     * @param dataSource the employee data source
     */
    public MySqlAccountDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public int deleteUser(User user) throws Exception {
        logger.debug("deleteUser([{}]) -> started", user);
        int affected = 0;
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.getUserId()));
        try {
            affected = getNamedParameterJdbcTemplate().update(DELETE_USER_SQL, params);
        } catch (Exception e) {
            logger.error("Error occurred while deleting user", e);
            throw e;
        }
        logger.debug("deleteUser -> ended. [{}] rows deleted", affected);
        return affected;

    }


    // creating or updating user in DB
    public int createOrUpdateUser(User user) throws Exception {
        logger.debug("createOrUpdateUser([{}]) -> started", user);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate()).
                withProcedureName("create_upd_user");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getUserId());
        params.put("login", user.getEmail());
        params.put("first_name", user.getFirstName());
        params.put("last_name", user.getLastName());
        params.put("pass", user.getPassword());
        SqlParameterSource in = new MapSqlParameterSource().addValues(params);
        Map<String, Object> out;
        try {
            out = jdbcCall.execute(in);
        } catch (Exception e) {
            logger.error("Error occurred wile adding to cart", e);
            throw e;
        }
        int userId = (int) out.get("user_id");
        user.setUserId(userId);
        logger.debug("createOrUpdateUser -> ended. userId: [{}]", userId);
        return userId;
    }

    // get user info from db by email
    public User getUserInfo(User user) {
        logger.debug("getUserInfo([{}]) -> started", user);
        List<User> users = getJdbcTemplate().query(GET_USER_SQL, new Object[]{user.getEmail()}, new UserMapper());
        User userDB;
        if (users.isEmpty()) {
            logger.debug("getUserInfo() -> ended. User [{}] not found", user);
            return null;
        } else {
            userDB = users.get(0);
            logger.debug("getUserInfo() -> ended. user: [{}]", userDB);
            return userDB;
        }
    }

    @Override
    public String getUserPass(User user) {
        logger.debug("getUserPass([{}]) -> started", user);
        List<String> passList = getJdbcTemplate().query(GET_USER_PASS, new Object[]{user.getEmail()}, (rs, rowNum) -> {
            return rs.getString("pass");
        });
        String pass;
        if (passList.isEmpty()) {
            logger.debug("getUserPass() -> ended. User [{}] not found", user);
            return null;
        } else {
            pass = passList.get(0);
            logger.debug("getUserPass() -> ended.");
            return pass;
        }
    }

    // creating or updating user address in DB
    public int createOrUpdateAddress(Address address, int userId) throws Exception {
        logger.debug("createOrUpdateAddress([{}],([{}])) -> started", address, userId);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate()).
                withProcedureName("create_upd_address");
        Map<String, Object> out;
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("address_id", address.getAddressId());
            params.put("user_id", userId);
            params.put("street1", address.getStreet1());
            params.put("street2", address.getStreet2());
            params.put("country", address.getCountry());
            params.put("city", address.getCity());
            params.put("zip", address.getZip());
            params.put("state", address.getState());
            params.put("phone", address.getPhone());
            SqlParameterSource in = new MapSqlParameterSource().addValues(params);

            out = jdbcCall.execute(in);
        } catch (Exception e) {
            logger.error("Error occurred while updating address", e);
            throw e;
        }
        int result = (int) out.get("address_id");
        address.setAddressId(result);
        logger.debug("createOrUpdateAddress -> ended. Address_id: ([{}])", address, userId);
        return result;
    }

    // creating or updating user card in DB
    public Result createOrUpdateCard(Card card, int userId) throws Exception{
        logger.debug("createOrUpdateCard([{}],([{}])) -> started", card, userId);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate()).
                withProcedureName("create_upd_card");
        Map<String, Object> out;
        try{
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("card_id", card.getCardId());
        params.put("user_id", userId);
        params.put("card_number", card.getCardNumber());
        params.put("first_name", card.getFirstName());
        params.put("last_name", card.getLastName());
        params.put("card_exp_year", card.getExpireYY());
        params.put("card_exp_month", card.getExpireMM());
        params.put("card_type", card.getCardType());
        SqlParameterSource in = new MapSqlParameterSource().addValues(params);
        out = jdbcCall.execute(in);
        } catch (Exception e) {
            logger.error("Error occurred while updating address", e);
            throw e;
        }
        while (!out.values().isEmpty()) {
            Object msg = out.get("msg");
            if (msg != null) {
                String errorMsg = out.get("msg").toString();
                if (!errorMsg.isEmpty()) {
                    return new Result(ResultCode.ERROR, Constants.ERROR_MSG);
                } else {
                    return new Result(ResultCode.ERROR, Constants.NOT_FOUND_MSG);
                }
            } else {
                card.setCardId((Integer) out.get("card_id"));
                return new Result(ResultCode.OK, "");
            }
        }
        return new Result(ResultCode.ERROR, Constants.NOT_FOUND_MSG);
    }


    private static final class UserMapper implements RowMapper<User> {

        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPassword(rs.getString("pass"));
            user.setEmail(rs.getString("login"));
            if (rs.getInt("card_id") != 0) {
                Card card = new Card();
                card.setCardId(rs.getInt("card_id"));
                card.setCardNumber(rs.getString("card_number"));
                card.setFirstName(rs.getString("first_name"));
                card.setLastName(rs.getString("last_name"));
                card.setCardType(rs.getString("card_type"));
                card.setExpireMM(rs.getInt("card_exp_month"));
                card.setExpireYY(rs.getInt("card_exp_year"));
                user.setCard(card);
            }
            if (rs.getInt("address_id") != 0) {
                Address address = new Address();
                address.setAddressId(rs.getInt("address_id"));
                address.setPhone(rs.getString("phone"));
                address.setZip(rs.getString("zip"));
                address.setStreet1(rs.getString("street1"));
                address.setStreet2(rs.getString("street2"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setCountry(rs.getString("country"));
                user.setAddress(address);
            }
            return user;
        }
    }

}
