package integration;


import com.firstdata.firstapi.client.FirstAPIClientV2Helper;
import com.firstdata.firstapi.client.domain.TransactionType;
import com.firstdata.firstapi.client.domain.v2.Address;
import com.firstdata.firstapi.client.domain.v2.Card;
import com.firstdata.firstapi.client.domain.v2.Token;
import com.firstdata.firstapi.client.domain.v2.TransactionRequest;
import com.firstdata.firstapi.client.domain.v2.TransactionResponse;
import com.firstdata.firstapi.client.domain.v2.Transarmor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"classpath:applicationContext_test.xml"})
public class FirstDataAPIClientV2Test {

    private static final Logger log = LoggerFactory.getLogger(FirstDataAPIClientV2Test.class);
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FirstAPIClientV2Helper paymentClient;


    @Test
    public void doPostTATokenCapture() throws Exception {
        log.info("+++++++++++++++++++++++++++++++++++++ start ++++++++++++++++++");
        // Generate Token
        assertNotNull("RESTTemplate is null:", restTemplate);
        assertNotNull("clietn is null:", paymentClient);
        TransactionRequest transreq = getPrimaryTransaction();

        transreq.setType("FDToken");

        transreq.getCard().setNumber("5424180279791732");
        transreq.getCard().setName("John Smith");
        transreq.getCard().setExpiryDt("0416");
        transreq.getCard().setCvv("123");
        transreq.getCard().setType("mastercard");

        transreq.setAuth("false");
        transreq.setTa_token(FirstAPIClientV2Helper.TA_TOKEN_VALUE);

        transreq.setToken(null);
        transreq.setBilling(null);
        transreq.setTransactionType(null);
        transreq.setPaymentMethod(null);
        transreq.setAmount(null);
        transreq.setCurrency(null);

        TransactionResponse responseToken = paymentClient.postTokenTransaction(transreq);
        assertNotNull("Response is null ", responseToken);
        assertNull("Error in response", responseToken.getError());
        log.info("FD TOken : " + responseToken.getToken().getTokenData().getValue());

        //authorize
        assertNotNull("RESTTemplate is null:", restTemplate);
        assertNotNull("clietn is null:", paymentClient);

        TransactionRequest trans = getPrimaryTransaction();

        trans.setReferenceNo("abc1412096293369");
        trans.setTransactionType("authorize");
        trans.setPaymentMethod("token");
        trans.setAmount("1");
        trans.setCurrency("USD");

        Token token = new Token();
        Transarmor ta = new Transarmor();

        ta.setValue(responseToken.getToken().getTokenData().getValue());
        ta.setName(responseToken.getToken().getTokenData().getName());
        ta.setExpiryDt(responseToken.getToken().getTokenData().getExpiryDt());
        ta.setType(responseToken.getToken().getTokenData().getType());
        token.setTokenData(ta);
        token.setTokenType("FDToken");
        trans.setToken(token);

        trans.setCard(null);
        trans.setBilling(null);

        TransactionResponse response = paymentClient.postTokenTransaction(trans);
        assertNotNull("Response is null ", response);
        assertNull("Error in response", response.getError());
        log.info("FD TOken : " + responseToken.getToken().getTokenData().getValue());


        //Capture
        assertNotNull("RESTTemplate is null:", restTemplate);
        assertNotNull("clietn is null:", paymentClient);
        trans = getPrimaryTransaction();

        trans.setReferenceNo("abc1412096293369");
        trans.setTransactionTag("1871007");
        trans.setTransactionType(TransactionType.CAPTURE.name());
        trans.setPaymentMethod("token");
        trans.setAmount("1");
        trans.setCurrency("USD");
        trans.setTransactionTag(response.getTransactionTag());
        trans.setId(response.getTransactionId());

        token = new Token();
        ta = new Transarmor();

        ta.setValue(responseToken.getToken().getTokenData().getValue());
        ta.setName(responseToken.getToken().getTokenData().getName());
        ta.setExpiryDt(responseToken.getToken().getTokenData().getExpiryDt());
        ta.setType(responseToken.getToken().getTokenData().getType());

        token.setTokenData(ta);
        token.setTokenType("FDToken");
        trans.setToken(token);
        trans.setCard(null);
        trans.setBilling(null);

        TransactionResponse response2 = paymentClient.postTokenTransaction(trans);
        assertNotNull("Response is null ", response2);
        assertNull("Error in response", response2.getError());
        log.info("Transaction Tag:{} Transaction id:{}", response2.getTransactionTag(), response2.getTransactionId());
        log.info("++++++++++++++++++++++++++++++++++++++ end +++++++++++++++++");
    }


    @Test
    public void doPostTATokenVoidVisa() throws Exception {
        log.info("+++++++++++++++++++++++++++++++++++++ start ++++++++++++++++++");

        // Generate Token
        assertNotNull("RESTTemplate is null:", restTemplate);
        assertNotNull("clietn is null:", paymentClient);
        TransactionRequest transreq = getPrimaryTransaction();

        transreq.setType("FDToken");

        transreq.getCard().setNumber("4012000033330026");
        transreq.getCard().setName("John Smith");
        transreq.getCard().setExpiryDt("0416");
        transreq.getCard().setCvv("123");
        transreq.getCard().setType("visa");

        transreq.setAuth("false");
        transreq.setTa_token(FirstAPIClientV2Helper.TA_TOKEN_VALUE);

        transreq.setToken(null);
        transreq.setBilling(null);
        transreq.setTransactionType(null);
        transreq.setPaymentMethod(null);
        transreq.setAmount(null);
        transreq.setCurrency(null);

        TransactionResponse responseToken = paymentClient.postTokenTransaction(transreq);
        assertNotNull("Response is null ", responseToken);
        assertNull("Error in response", responseToken.getError());
        //log.info("Transaction Tag:{} Transaction id:{}",responseToken.getTransactionTag(),responseToken.getTransactionId());
        log.info("FD Token : " + responseToken.getToken().getTokenData().getValue());

        //purchase
        assertNotNull("RESTTemplate is null:", restTemplate);
        assertNotNull("clietn is null:", paymentClient);
        TransactionRequest trans = getPrimaryTransaction();

        trans.setReferenceNo("abc1412096293369");
        trans.setTransactionType("purchase");
        trans.setPaymentMethod("token");
        trans.setAmount("1");
        trans.setCurrency("USD");

        Token token = new Token();
        Transarmor ta = new Transarmor();

        ta.setValue(responseToken.getToken().getTokenData().getValue());
        ta.setName(responseToken.getToken().getTokenData().getName());
        ta.setExpiryDt(responseToken.getToken().getTokenData().getExpiryDt());
        ta.setType(responseToken.getToken().getTokenData().getType());

        token.setTokenData(ta);
        token.setTokenType("FDToken");
        trans.setToken(token);

        trans.setCard(null);
        trans.setBilling(null);

        TransactionResponse response = paymentClient.postTokenTransaction(trans);
        assertNotNull("Response is null ", response);
        assertNull("Error in response", response.getError());
        log.info("Transaction Tag:{} Transaction id:{}", response.getTransactionTag(), response.getTransactionId());


        //void
        assertNotNull("RESTTemplate is null:", restTemplate);
        assertNotNull("clietn is null:", paymentClient);
        trans = getPrimaryTransaction();
        trans.setTransactionType(TransactionType.VOID.name());

        trans.setReferenceNo("abc1412096293369");
        trans.setPaymentMethod("token");
        trans.setAmount("1");
        trans.setCurrency("USD");

        trans.setTransactionTag(response.getTransactionTag());
        trans.setId(response.getTransactionId());

        token = new Token();
        ta = new Transarmor();

        ta.setValue(responseToken.getToken().getTokenData().getValue());
        ta.setName(responseToken.getToken().getTokenData().getName());
        ta.setExpiryDt(responseToken.getToken().getTokenData().getExpiryDt());
        ta.setType(responseToken.getToken().getTokenData().getType());

        token.setTokenData(ta);
        token.setTokenType("FDToken");
        trans.setToken(token);

        trans.setCard(null);
        trans.setBilling(null);

        TransactionResponse response2 = paymentClient.postTokenTransaction(trans);
        assertNotNull("Response is null ", response2);
        assertNull("Error in response", response2.getError());
        log.info("Transaction Tag:{} Transaction id:{}", response2.getTransactionTag(), response2.getTransactionId());
        log.info("++++++++++++++++++++++++++++++++++++++ end +++++++++++++++++");

    }


    private TransactionRequest getPrimaryTransaction() {
        log.info("+++++++++++++++++++++++++++++++++++++ start ++++++++++++++++++");
        TransactionRequest request = new TransactionRequest();
        request.setAmount("1100");
        request.setCurrency("USD");
        request.setPaymentMethod("credit_card");
        request.setTransactionType(TransactionType.AUTHORIZE.getValue());
        Card card = new Card();
        card.setCvv("123");
        card.setExpiryDt("1219");
        card.setName("Test data ");
        card.setType("visa");
        card.setNumber("4788250000028291");
        request.setCard(card);
        Address address = new Address();
        request.setBilling(address);
        address.setState("NY");
        address.setAddressLine1("sss");
        address.setZip("11747");
        address.setCountry("US");
        //request.setTa_token(null);
        log.info("++++++++++++++++++++++++++++++++++++++ end +++++++++++++++++");
        return request;
    }

}
