package com.paygo.dao;

import com.paygo.domain.Address;
import com.paygo.domain.Card;
import com.paygo.domain.Result;
import com.paygo.domain.User;

/**
 * dao for account operations in DB
 */
public interface AccountDao {

    int createOrUpdateUser(User user) throws Exception;
    User getUserInfo(User user);
    String getUserPass(User user);
    int createOrUpdateAddress(Address address, int userId) throws Exception;
    Result createOrUpdateCard(Card card, int userId)  throws Exception;
    int deleteUser(User user) throws Exception;
}
