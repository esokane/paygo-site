package com.paygo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paygo.dao.AccountDao;
import com.paygo.dao.CryptUtils;
import com.paygo.domain.Constants;
import com.paygo.domain.Result;
import com.paygo.domain.ResultCode;
import com.paygo.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.paygo.utils.JSONUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * class for operations with user account
 */
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private JSONUtils jsonUtils;
    private AccountDao accountDao;

    public String authorize(HttpServletRequest request) {
        User user;
        HttpSession session = request.getSession();
        try {
            user = jsonUtils.processJSON2Object(request.getInputStream(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ServiceConstants.INCORRECT_DATA_JSON_ERROR;
        }
        if (ServiceConstants.ACTION_LOGIN.equals(user.getAction())) {
            return login(user, session);
        } else {
            return createAccount(user, session);
        }
    }

    private String createAccount(User requestUser, HttpSession session) {
        logger.info("createAccount([{}],[{}]) -> started", requestUser, session);
        User user = accountDao.getUserInfo(requestUser);
        if (user != null) {
            return jsonUtils.error2Json(Constants.DUPLICATE_LOGIN_MSG);
        } else {
            try {
                String pass;
                try {
                    pass = CryptUtils.hashPassword(requestUser.getPassword());
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    logger.error("Error occurred while trying encode password", e);
                    return jsonUtils.error2Json(Constants.ERROR_MSG);
                }
                requestUser.setPassword(pass);
                accountDao.createOrUpdateUser(requestUser);
            } catch (Exception e) {
                logger.error("Error occurred while creating user", e);
                return jsonUtils.error2Json(Constants.ERROR_MSG);
            }
        }
        session.setAttribute(Constants.SESSION_ATTRIBUTE_USER, requestUser);
        String result = null;
        try {
            result = jsonUtils.processObject2Json(requestUser);
        } catch (JsonProcessingException e) {
            logger.error("Failed to process object to JSON ({})", requestUser, e);
            return ServiceConstants.USER_NOT_CREATED_JSON_ERROR;
        }

        logger.info("createAccount -> ended. result: [{}]", result);
        return result;
    }

    private Result checkPass(String enteredPass, String pass) {
        try {
            boolean isCorrectPass = CryptUtils.validatePassword(enteredPass, pass);
            // if password is correct return OK, otherwise return error
            if (!isCorrectPass) {
                logger.info("login -> wrong password");
                return new Result(ResultCode.ERROR, "Wrong password");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while validating password", e);
            return new Result(ResultCode.ERROR, "Exception occurred while validating password");
        }
        return new Result(ResultCode.OK, "");
    }

    private String login(User user, HttpSession session) {
        logger.info("login([{}],[{}]) -> started", user, session);
        User userInfo = accountDao.getUserInfo(user);
        if (userInfo == null){
            logger.error("Failed to retrieve user from database [{}]", user);
            return ServiceConstants.USER_NOT_FOUND_JSON_ERROR;
        }
        String pass = user.getPassword();
        Result checkPassResult = checkPass(pass, userInfo.getPassword());
        if (checkPassResult.getCode().equals(ResultCode.OK)) {
            userInfo.setPassword("");
        } else {
            logger.info("login -> wrong password for user: [{}]", user);
            return jsonUtils.error2Json(checkPassResult.getMsg());
        }
        session.setAttribute(Constants.SESSION_ATTRIBUTE_USER, userInfo);
        String result;
        try {
            result = jsonUtils.processObject2Json(userInfo);
        } catch (JsonProcessingException e) {
            logger.error("Failed to process object to JSON [{}]", userInfo, e);
            return ServiceConstants.USER_NOT_FOUND_JSON_ERROR;
        }
        logger.info("login -> ended. result:[{}]", result);
        return result;
    }

    public void setJsonUtils(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public String deleteUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        try {
            accountDao.deleteUser(user);
        } catch (Exception e) {
            logger.error("Failed to delete user ", e);
            return ServiceConstants.USER_DELETE_FAILED_JSON_ERROR;
        }
        return ServiceConstants.JSON_SUCCESS;
    }

    public String saveUser(HttpServletRequest request) {
        logger.info("saveUser([{}]) -> started", request);
        User userInfo;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_USER);
        try {
            userInfo = jsonUtils.processJSON2Object(request.getInputStream(), User.class);
        } catch (IOException e) {
            logger.error("Failed to process object to JSON", e);
            return ServiceConstants.USER_INFO_SAVING_FAILED_JSON_ERROR;
        }
        /*userId, addressId, cardId filling from server session */
        userInfo.setUserId(user.getUserId());
        userInfo.getAddress().setAddressId(user.getAddress().getAddressId());
        userInfo.getCard().setCardId(user.getCard().getCardId());
        if ((userInfo.getNewPass() != null) && (!userInfo.getNewPass().isEmpty())) {
            String enteredPass = userInfo.getPassword();
            String pass = accountDao.getUserPass(userInfo);
            Result checkPassResult = checkPass(enteredPass, pass);
            /*if pass is correct can change pass*/
            if (ResultCode.OK.equals(checkPassResult.getCode())) {
                if ((userInfo.getNewPass() != null) && (userInfo.getNewPass().equals(userInfo.getNewPassConfirm()))) {
                    String hashedPass;
                    try {
                        hashedPass = CryptUtils.hashPassword(userInfo.getNewPass());
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        logger.error("Error occurred while trying encode password", e);
                        return jsonUtils.error2Json(Constants.ERROR_MSG);
                    }
                    userInfo.setPassword(hashedPass);
                    try {
                        accountDao.createOrUpdateUser(userInfo);
                    } catch (Exception e) {
                        logger.error("Failed to update user", e);
                        return ServiceConstants.USER_INFO_SAVING_FAILED_JSON_ERROR;
                    }
                } else {
                    logger.error(ServiceConstants.PASSWORDS_NOT_SAME_JSON_ERROR + "({})", userInfo);
                    return ServiceConstants.PASSWORDS_NOT_SAME_JSON_ERROR;
                }
            } else {
                return jsonUtils.error2Json(checkPassResult.getMsg());
            }
        }
        /* set to null for security reasons*/
        userInfo.setPassword("");
        try {
            accountDao.createOrUpdateAddress(userInfo.getAddress(), userInfo.getUserId());
            accountDao.createOrUpdateCard(userInfo.getCard(), userInfo.getUserId());
        } catch (Exception e) {
            logger.error("Failed to update user", e);
            return ServiceConstants.USER_INFO_SAVING_FAILED_JSON_ERROR;
        }

        session.setAttribute(Constants.SESSION_ATTRIBUTE_USER, userInfo);
        String result = null;
        try {
            result = jsonUtils.processObject2Json(userInfo);
        } catch (JsonProcessingException e) {
            logger.error("Failed to process object to JSON ({})", userInfo, e);
            return ServiceConstants.USER_INFO_SAVING_FAILED_JSON_ERROR;
        }

        logger.info("saveUser -> ended. result: [{}]", result);
        return result;
    }
}
