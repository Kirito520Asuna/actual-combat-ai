package im.token;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author yan
 * @Date 2023/8/29 0029 13:24
 * @Description
 */
@Slf4j
public class TokenTest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        private Long id;
        private String username;
    }

    public static void main(String[] args) throws InterruptedException {

        User user = new User(System.currentTimeMillis(), UUID.randomUUID().toString());
//        String token = TokenUtils.createToken(user);
//        System.out.println(token);
        log.info("{}",BeanUtil.beanToMap(user,false,false));
        String x = "===================";

        String generalToken = TokenUtils.generalToken(user);

        String token1 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJITUQ1In0.eyJvYmplY3RNYXAiOnsiaWQiOjEsInVzZXJuYW1lIjoiemhhbyJ9LCJpc3MiOiJ5YW4iLCJqdGkiOiIwYzdiNGVlZC01YWRmLTQzYzYtODUxZi0wNjg2MmRlMjhlY2QiLCJpYXQiOjE2OTM1NTk0MzI5NDUsImV4cCI6MTY5MzY0NTgzMjk0NSwibmJmIjoxNjkzNTU5NDMyOTQ1fQ.irKxfIme1Wkeul-XV-hqAA";
//        token1=generalToken;
        log.error("\n{} \n{}\n\n{} \n{} ",x,token1,generalToken,x);
        List<String> tokens = Stream.of(token1,generalToken).collect(Collectors.toList());
        soutTokenList(tokens);
//        soutTokenOne(token1);
//        soutTokenOne(generalToken);


    }
    private static void soutTokenList(List<String> tokens) throws InterruptedException {
        if (CollectionUtils.isEmpty(tokens)){
            return;
        }
        for (String token : tokens) {
            soutTokenOne(token);
        }
    }

    private static void soutTokenOne(String token)  {
        long l = System.currentTimeMillis();
        String s = "=========start" + l + "=============";

        JWT jwt = TokenUtils.parseToken(token);
        JSONObject payloads = jwt.getPayloads();
        long exp = payloads.getLong("exp");
        Date now = new Date();
        long time = now.getTime();


        boolean signer = TokenUtils.verifySigner(token);

        String o = payloads.get(TokenUtils.Object_Map).toString();


        User user1 = JSONUtil.toBean(o, User.class);
        String s1 = "=============end" + l + "=========";

        log.info("\n{}\n{}\n{}\n{}\n{}\n{}\n{}\n{}\n{}"
                ,s
                ,payloads,exp,time,(exp - time),signer,o,user1
                ,s1);
    }
}
