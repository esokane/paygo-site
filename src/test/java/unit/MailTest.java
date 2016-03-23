package unit;

import com.paygo.email.EmailSender;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class MailTest {

    private static Map<String, String> mailParams;

    @BeforeClass
    public static void configure(){
        mailParams = getMailParamsMap();
    }


    @Test
    public void loadBodyTemplateTest() throws IOException {
        EmailSender emailSender = new EmailSender("", "", "", "/basic.html");
        String bodyTemplate = emailSender.loadBodyTemplate("/basic.html");
        assertTrue("Body template should be loaded!", bodyTemplate.length() > 0);
    }

    @Test
    public void generateMailTest() {
        EmailSender emailSender = new EmailSender("", "", "", "/basic.html");

        String bodyTemplate = emailSender.loadBodyTemplate("/basic.html");
        String body = emailSender.generateMail(bodyTemplate, mailParams);

        assertTrue(body.contains(mailParams.get("company_name")));
        assertTrue(body.contains(mailParams.get("firstname")));
        assertTrue(body.contains(mailParams.get("lastname")));
        assertTrue(body.contains(mailParams.get("order_date")));
        assertTrue(body.contains(mailParams.get("experian_bin")));
        assertTrue(body.contains(mailParams.get("card_type")));
        assertTrue(body.contains(mailParams.get("card_expire")));
        assertTrue(body.contains(mailParams.get("payment_id")));
        assertTrue(body.contains(mailParams.get("order_ip_address")));
        assertTrue(body.contains(mailParams.get("order_total")));
    }

    public static Map<String, String> getMailParamsMap() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("company_name", UUID.randomUUID().toString());
        parameters.put("firstname", UUID.randomUUID().toString());
        parameters.put("lastname", UUID.randomUUID().toString());
        parameters.put("order_date", UUID.randomUUID().toString());
        parameters.put("experian_bin", UUID.randomUUID().toString());
        parameters.put("card_type", UUID.randomUUID().toString());
        parameters.put("card_expire", UUID.randomUUID().toString());
        parameters.put("payment_id", UUID.randomUUID().toString());
        parameters.put("order_ip_address", UUID.randomUUID().toString());
        parameters.put("order_total", UUID.randomUUID().toString());

        return parameters;
    }

}
