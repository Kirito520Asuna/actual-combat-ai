package im;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;


//@SpringBootTest(classes = {ImlearnApplication.class})
class BasicImApplicationTests {
    @Resource
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        t1();
    }
    void setPasswordEncoder(){
        String url = "123";
        System.out.println(url);
        String encode = passwordEncoder.encode(url);
        System.out.println(encode);
        boolean matches = passwordEncoder.matches(url, encode);
        System.out.println(matches);
    }


    void t1() {
        String u = "18507378849";
        int length = u.length();
        String substring = u.substring(length - 4, length);
        System.out.println(u.substring(3,7));
        System.out.println(u.replace(u.substring(3, 7), "****"));
        System.out.println(substring);
    }

    public static void main(String[] args) {

    }

}
