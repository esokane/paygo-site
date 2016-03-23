package integration;

import com.paygo.dao.mysql.MySqlAccountDao;
import com.paygo.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MySqlAccountDaoTest {

    @Autowired
    MySqlAccountDao dao;

    @Test
    public void testGetUserInfo() {
        User user = new User();
        user.setEmail("test10@gmail.com");
        user.setPassword("1111");
        User userInfo = dao.getUserInfo(user);
        Assert.assertNotEquals(0, userInfo.getUserId());
    }

    @Test
    public void testGetUserPass() {
        User user = new User();
        user.setEmail("test10@gmail.com");
        String pass = dao.getUserPass(user);
        Assert.assertNotNull(pass);
        Assert.assertNotEquals("", pass);
    }


    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setUserId(8);
        int affected = dao.deleteUser(user);
        Assert.assertEquals(1, affected);
    }


    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setEmail("test8@test.com");
        user.setPassword("fhjdjs8478&&*9ddjfhd#:l");
        user.setFirstName("Mark");
        user.setLastName("Stergin");
        dao.createOrUpdateUser(user);
        Assert.assertNotEquals(0, user.getUserId());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setUserId(18);
        user.setEmail("test5@test.com");
        user.setFirstName("Eric");
        user.setLastName("Gant");
        user.setPassword("pass");
        dao.createOrUpdateUser(user);
        Assert.assertNotEquals(0, user.getUserId());
    }

    @Test
    public void testCreateAddress() throws Exception {
        Address address = new Address();
        address.setStreet1("Parklane");
        address.setCity("Palo-Alto");
        address.setCountry("USA");
        address.setState("CA");
        address.setStreet2("b6 - 785");
        address.setZip("ddd545621");
        address.setPhone("+154621545213");
        int userId = 34;
        dao.createOrUpdateAddress(address, userId);
        Assert.assertNotEquals(0, address.getAddressId());
    }

    @Test
    public void testUpdateAddress() throws Exception {
        Address address = new Address();
        address.setAddressId(3);
        address.setStreet1("Parklane");
        address.setCity("Palo-Alto");
        address.setCountry("USA");
        address.setState("CA");
        address.setStreet2("b6 - 785");
        int userId = 0;
        dao.createOrUpdateAddress(address, userId);
        Assert.assertNotEquals(0, address.getAddressId());
    }

    @Test
    public void testCreateCard() throws Exception {
        Card card = new Card();
        card.setFirstName("Jina");
        card.setLastName("Doe");
        card.setExpireMM(03);
        card.setExpireYY(2018);
        int userId = 34;
        Result result = dao.createOrUpdateCard(card, userId);
        Assert.assertEquals(ResultCode.OK, result.getCode());
        Assert.assertNotEquals(0, card.getCardId());
    }

    @Test
    public void testUpdateCard() throws Exception {
        Card card = new Card();
        card.setFirstName("Jina");
        card.setLastName("Doe");
        card.setCardNumber("0100123312452563");
        card.setExpireMM(03);
        card.setExpireYY(2018);
        card.setCardId(6);
        card.setCardType("Mastercard");
        int userId = 34;
        Result result = dao.createOrUpdateCard(card, userId);
        Assert.assertEquals(ResultCode.OK, result.getCode());
        Assert.assertNotEquals(0, card.getCardId());
    }

}
