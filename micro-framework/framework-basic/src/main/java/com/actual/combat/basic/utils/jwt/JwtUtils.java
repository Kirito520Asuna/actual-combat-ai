package com.actual.combat.basic.utils.jwt;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.enums.ApiCode;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2023/5/12 0012 15:02
 * @Description JWT工具类
 */
@Data
@Component
@ConfigurationProperties(prefix = "common.jwt")
public class JwtUtils {
    //有效期为
    public static final long JWT_TTL = 24 * 60 * 60 * 1000L;// 60 * 60 *1000  一个小时
    //
    public static final long LONG_JWT_TTL = 7 * JWT_TTL;
    //设置秘钥明文
    public static final String JWT_KEY = "kiritoasuna";
    public static final String REFRESH_TOKEN_KEY = "refreshToken";
    //设置签发者
    public static final String IS_SUER = "yan";
    public static final String HEADER_AS_TOKEN = "Authorization";
    private long expire = JWT_TTL;
    private long expireLong = LONG_JWT_TTL;
    private String secret = JWT_KEY;
    private String header = HEADER_AS_TOKEN;
    private String isSuer = IS_SUER;


    /**
     * demo
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //加密
        String jwt = createJWT("8");
        System.out.println(jwt);
        System.out.println("=================");
        //jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2N2EwNDJlM2Q5MTE0MWVmOWEyOTAwZTRkMzQwYjBkYSIsInN1YiI6IjgiLCJpc3MiOiJ5YW4iLCJpYXQiOjE3MjE5Nzc2MDMsImV4cCI6MTcyMjA2NDAwM30.Cnj7fp-G21jj3mBcLMeG6Iftj-wTy2TJK2_CBkHZ2JU";//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhOGRiNDdiZjJhZjA0NGJhODExZjgxNmNhNzA5NWEzMyIsInN1YiI6IjIxMjMiLCJpc3MiOiJ5YW4iLCJpYXQiOjE2ODM4ODU5MTQsImV4cCI6MTY4Mzg4OTUxNH0.qsi14zM8LA02M57VyK-qDay7QJq5lKcDoaktplUZNKs";
//        token="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxYzUxZjRiMTNmMGY0ZGJkYjZhM2M1YjdmMWNhMmI0NSIsInN1YiI6IjIxMjMiLCJpc3MiOiJ5YW4iLCJpYXQiOjE2OTExMjU3NDAsImV4cCI6MTY5MTEyOTM0MH0.M0fnMqKfqTnChwTxUAnikedzzKMehn2tgqeeeD3F4f8";
        Claims claims = parseJWT(jwt);
        System.out.println(claims);

    }

    public static long getJWT_TTL() {
        return SpringUtil.getBean(JwtUtils.class).getExpire();
    }

    public static long getLONG_JWT_TTL() {
        return SpringUtil.getBean(JwtUtils.class).getExpireLong();
    }

    public static String getJWT_KEY() {
        return SpringUtil.getBean(JwtUtils.class).getSecret();
    }

    public static String getHEADER_AS_TOKEN() {
        return SpringUtil.getBean(JwtUtils.class).getHeader();
    }

    public static String getIS_SUER() {
        return SpringUtil.getBean(JwtUtils.class).getIsSuer();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @param subject
     * @return
     */
    public static String createJWT(String subject) {
        long JWT_TTL = getJWT_TTL();
        return createJWT(subject, JWT_TTL);
    }

    public static String createJWT(String subject, Long ttlMillis) {
        return createJWT(subject, ttlMillis, getJWT_KEY(), getIS_SUER());
    }

    /**
     * 生成jtw
     *
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject, String secret, String issuer) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID(), secret, issuer);// 设置过期时间
        return builder.compact();
    }


    /**
     * 生成jtw
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis, String secret, String issuer) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID(), secret, issuer);// 设置过期时间
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid, String secret, String issuer) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey(secret);
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = getJWT_TTL();
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer(ObjectUtil.isEmpty(issuer) ? IS_SUER : issuer)     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);
    }

    /**
     * 创建token
     *
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis, String secret, String issuer) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id, secret, issuer);// 设置过期时间
        return builder.compact();
    }


    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return
     */
    public static SecretKey generalKey(String secret) {
        byte[] encodedKey = Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt, String secret) throws Exception {
        SecretKey secretKey = generalKey(secret);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static Claims parseJWT(String jwt) throws Exception {
        return parseJWT(jwt, getJWT_KEY());
    }

    public static String getSubjectByParseJWT(String jwt) throws Exception {
        try {
            String subject = parseJWT(jwt).getSubject();
            return subject;
        }catch (Exception e){
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
    }

    // 判断JWT是否过期
    public static boolean isTokenExpired(Claims claims) {
        return isTokenExpired(claims, new Date());
    }

    /**
     * 判断JWT是否过期
     *
     * @param claims
     * @param date
     * @return
     */
    public static boolean isTokenExpired(Claims claims, Date date) {
        return claims.getExpiration().before(date);
    }


}