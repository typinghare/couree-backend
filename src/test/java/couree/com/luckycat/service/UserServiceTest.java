package couree.com.luckycat.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void sendCode() {
        userService.signUpSendCodeToPhone("+1&6173863271");
    }

    @Test
    public void getService() {
        System.out.println(1);
    }
}
