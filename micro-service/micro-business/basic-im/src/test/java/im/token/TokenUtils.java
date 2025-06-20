package im.token;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.GlobalBouncyCastleProvider;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.AlgorithmUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;


import java.util.*;

/**
 * @Author yan
 * @Date 2023/8/29 0029 13:09
 * @Description 基于hutool 5.8.16
 */
public class TokenUtils {
    //token存储mapKey实体键
    public static final String Object_Map = "objectMap";
    //设置秘钥明文
    public static final String JWT_KEY = "kiritoasuna";
    //签发人
    public static final String ISSUER = "yan";
    //验签id
    public static final String algorithmId = "HMD5";
    //有效期为
    public static final Long JWT_TTL = 24 * 60 * 60 * 1000L;// 60 * 60 *1000  一个小时

    /**
     * 基础
     *
     * @param payload
     * @param key
     * @return
     */
    public static String createToken(Map<String, Object> payload, byte[] key) {
        //十分重要，不禁用发布到生产环境无法验证
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        String token = JWTUtil.createToken(payload, key);
        return token;
    }

    /**
     * @param headers ---头
     * @param payload --荷载
     * @param signer  --签名
     * @return
     */
    public static String createToken(Map<String, Object> headers, Map<String, Object> payload, JWTSigner signer) {
        //十分重要，不禁用发布到生产环境无法验证
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        String token = JWTUtil.createToken(headers, payload, signer);
        return token;
    }


    /**
     * 创建JWT Token
     *
     * @param payload 荷载信息
     * @param signer  签名算法
     * @return JWT Token
     */
    public static String createToken(Map<String, Object> payload, JWTSigner signer) {
        return createToken(null, payload, signer);
    }
    /*================================================================*/

    /**
     * 生成加密后的秘钥
     *
     * @return
     */
    public static byte[] generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(TokenUtils.JWT_KEY);
        return encodedKey;
    }

    /**
     * 生成签名
     *
     * @param algorithmId --见 AlgorithmUtil
     * @return
     */
    public static JWTSigner generalSigner(String algorithmId) {
        return JWTSignerUtil.createSigner(AlgorithmUtil.getId(algorithmId), generalKey());
    }
    /*================================================================*/

    /**
     * 设置签发信息
     */
    public static void setSinger(Map<String, Object> map) {
        Date now = new Date();
        long expMillis = now.getTime() + JWT_TTL;
        Date expDate = new Date(expMillis);
        if (ObjectUtil.isNotEmpty(ISSUER)) {
            //签发人
            map.put(JWTPayload.ISSUER, ISSUER);
        }
        //唯一id
        map.put(JWTPayload.JWT_ID, UUID.randomUUID().toString());
        //签发时间
        map.put(JWTPayload.ISSUED_AT, now.getTime());
        //过期时间
        map.put(JWTPayload.EXPIRES_AT, expDate.getTime());
        //生效时间
        map.put(JWTPayload.NOT_BEFORE, now.getTime());

    }

    /*================================================================*/

    /**
     * 生成token
     *
     * @param o
     * @return
     */

    public static String createToken(Object o) {
        JSON parse = JSONUtil.parse(o);
        System.out.println(parse);
        Map<String, Object> objectMap = BeanUtil.beanToMap(o, false, false);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(Object_Map, objectMap);
        setSinger(map);
        String token = createToken(map, generalKey());
        return token;
    }

    /**
     * 带签发信息
     *
     * @param o
     * @return
     */
    public static String generalToken(Object o) {
//        JSON parse = JSONUtil.parse(o);
//        System.out.println(parse);
        Map<String, Object> objectMap = BeanUtil.beanToMap(o, false, false);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(Object_Map, objectMap);
        setSinger(map);
        String token = createToken(map, generalSigner(algorithmId));
        return token;
    }

    /*================================================================*/

    /**
     * 解析token
     */
    public static JWT parseToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt;
    }

    public static JSONObject parseTokenJson(String token) {
        JWT jwt = parseToken(token);
        JSONObject payloads = jwt.getPayloads();
        return payloads;
    }

    /**
     * @param token
     * @param verifyExp 是否验证过期时间
     * @return
     */
    public static Map<String, Object> getObjectMap(String token, boolean verifyExp) throws Exception {
        JSONObject entries = parseTokenJson(token);

        long exp = entries.getLong("exp");
        long now = new Date().getTime();
        long time = exp - now;

        if (time < 0 && verifyExp) {
            throw new Exception("差值已过:" + (now - exp) + " 毫秒");
        }

        Map<String, Object> objectMap = entries.get(Object_Map, LinkedHashMap.class);
        return objectMap;
    }


    /*================================================================*/

    /**
     * @param token
     * @param key
     * @return
     */
    public static boolean verify(String token, byte[] key) {
        //十分重要，不禁用发布到生产环境无法验证
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        boolean verify = JWTUtil.verify(token, key);

        if (!verify) {
            return verify;
        }

        try {
            getObjectMap(token, true);
        } catch (Exception e) {
            e.getStackTrace();
            verify = false;
        }
        return verify;
    }

    public static boolean verifyKey(String token) {
        return verify(token, generalKey());
    }

    /**
     * @param token
     * @param signer
     * @return
     */
    public static boolean verify(String token, JWTSigner signer) {
        //十分重要，不禁用发布到生产环境无法验证
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        boolean verify = JWTUtil.verify(token, signer);

        if (!verify) {
            return verify;
        }

        try {
            getObjectMap(token, true);
        } catch (Exception e) {
            e.getStackTrace();
            verify = false;
        }
        return verify;
    }

    public static boolean verifySigner(String token) {
        return verify(token, generalSigner(algorithmId));
    }
    /*================================================================*/
}
