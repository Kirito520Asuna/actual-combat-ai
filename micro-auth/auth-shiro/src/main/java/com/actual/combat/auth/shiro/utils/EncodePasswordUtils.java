package com.actual.combat.auth.shiro.utils;

import com.actual.combat.basic.exceptions.ErrorInfo;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author yan
 * @Date 2023/9/6 0006 14:24
 * @Description
 */
public class EncodePasswordUtils {
    public static String encodePassword(String password,String password1) throws Exception {
        if (password.equals(password1)) {
            //密码加密
            String encode = encodePassword(password);
            return encode;
        } else {
            String errorJson = ErrorInfo.builder()
                    .errorMessage("密码不一致")
                    .build()
                    .addErrorMap(ErrorInfo.ErrorLanguage.US_EN,"Passwords do not match")
                    .toJson();
            throw new GlobalCustomException(errorJson);
        }

    }
    // 加密密码
    public static String encodePassword(String plainPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(plainPassword);
    }

    // 比对密码
    public static boolean matchPassword(String plainPassword, String encodedPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(plainPassword, encodedPassword);
    }
}
