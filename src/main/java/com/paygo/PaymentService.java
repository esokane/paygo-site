package com.paygo;

import com.firstdata.firstapi.client.FirstAPIClientV2Helper;
import com.firstdata.firstapi.client.domain.TransactionType;
import com.firstdata.firstapi.client.domain.v2.Address;
import com.firstdata.firstapi.client.domain.v2.Card;
import com.firstdata.firstapi.client.domain.v2.Token;
import com.firstdata.firstapi.client.domain.v2.TransactionRequest;
import com.firstdata.firstapi.client.domain.v2.TransactionResponse;
import com.firstdata.firstapi.client.domain.v2.Transarmor;
import com.paygo.domain.ReportCartItem;
import com.paygo.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * class for credit card payment processing
 */
public class PaymentService {


    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private RestTemplate restTemplate;

    private FirstAPIClientV2Helper paymentClient;


    /**
     * @param user
     * @param cart
     */
    public void authorize(User user, List<ReportCartItem> cart) throws Exception {
        logger.info("authorize([{}],[{}]) -> started", user, cart);
        // Generate Token
        Token responseToken = generateToken(user);
        // calculate sum to authorize
        double amount = cart.stream().mapToDouble((c) ->(c.getReportType().getPrice())).sum();
        TransactionResponse response = authorize(responseToken, amount);
        logger.info("authorize -> ended.transactionId:" + response.getTransactionId() +
                " transactionTag:" + response.getTransactionTag() );


    }

    public void capture(String transactionId, String transactionTag, double amount, Token responseToken) throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setReferenceNo("abc1412096293369");
        request.setTransactionType(TransactionType.CAPTURE.name());
        request.setPaymentMethod("token");
        request.setAmount(String.valueOf(Math.round(amount * 100)));
        request.setCurrency("USD");
        request.setTransactionTag(transactionTag);
        request.setId(transactionId);
        request.setToken(createTransarmorToken(responseToken));
        TransactionResponse response = paymentClient.postTokenTransaction(request);

    }

    private Token createTransarmorToken(Token responseToken) {
        Token token = new Token();
        Transarmor ta = new Transarmor();
        ta.setValue(responseToken.getTokenData().getValue());
        ta.setName(responseToken.getTokenData().getName());
        ta.setExpiryDt(responseToken.getTokenData().getExpiryDt());
        ta.setType(responseToken.getTokenData().getType());
        token.setTokenData(ta);
        token.setTokenType("FDToken");
        return token;
    }


    //authorize
    private TransactionResponse authorize(Token responseToken, double amount) throws Exception {
        logger.debug("authorize([{}],[{}]) -> started", responseToken, amount);
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.AUTHORIZE.getValue());
        request.setReferenceNo("abc1412096293369");
        request.setTransactionType("authorize");
        request.setPaymentMethod("token");
        request.setAmount("1");
        request.setCurrency("USD");
        request.setToken(createTransarmorToken(responseToken));
        TransactionResponse response = paymentClient.postTokenTransaction(request);
        logger.debug("authorize -> ended. Response: ", response);
        return response;
    }

    private Token generateToken(User user) throws Exception {
        logger.debug("generateToken([{}]) -> started", user);
        TransactionRequest request = new TransactionRequest();
        Card card = new Card();
        card.setCvv(String.valueOf(user.getCard().getCvv()));
        card.setExpiryDt("1219");
        card.setName(user.getCard().getFirstName().trim() + " " + user.getCard()
        .getLastName().trim());
        card.setType(user.getCard().getCardType());
        card.setNumber(user.getCard().getCardNumber());
        request.setCard(card);
        Address address = new Address();
        request.setBilling(address);
        address.setState(user.getAddress().getState());
        address.setAddressLine1(user.getAddress().getStreet1());
        address.setZip(user.getAddress().getZip());
        address.setCountry(user.getAddress().getCountry());
        request.setType("FDToken");
        request.setAuth("false");
       // request.setTa_token(FirstAPIClientV2Helper.TA_TOKEN_VALUE);
        TransactionResponse responseToken = null;
        try {
            responseToken = paymentClient.postTokenTransaction(request);
        } catch (Exception e) {
            logger.error("Error getting Token",e);
            throw e;
        }
        logger.debug("generateToken -> ended. Token: ", responseToken.getToken());
        return responseToken.getToken();
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setPaymentClient(FirstAPIClientV2Helper paymentClient) {
        this.paymentClient = paymentClient;
    }
}
