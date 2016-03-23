package integration;

import com.paygo.email.EmailSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"classpath:applicationContext_test.xml"})
public class MailSenderTest {

    @Autowired
    EmailSender emailSender;

    @Test
    public void generateAndSendEmailTest() throws MessagingException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("company_name", "Company Name");
        parameters.put("firstname", "Firstname");
        parameters.put("lastname", "Lastname");
        parameters.put("order_date", "20.02.2016");
        parameters.put("experian_bin", "2656");
        parameters.put("card_type", "debit");
        parameters.put("card_expire", "2018");
        parameters.put("payment_id", "123456");
        parameters.put("order_ip_address", "192.168.0.1");
        parameters.put("order_total", "200");

        emailSender.sendEmail("natakaravaeva@mail.ru", parameters);
    }

}
