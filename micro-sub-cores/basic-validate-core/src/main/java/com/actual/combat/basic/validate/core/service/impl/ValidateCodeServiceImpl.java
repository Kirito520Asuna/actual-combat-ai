package com.actual.combat.basic.validate.core.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.constant.Constants;
import com.actual.combat.basic.core.constant.cache.CacheConstants;
import com.actual.combat.basic.core.pojo.captcha.CaptchaInfo;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.basic.validate.core.config.captcha.CaptchaConfig;
import com.actual.combat.basic.validate.core.prop.CaptchaProperties;
import com.actual.combat.redis.service.RedisService;
import com.actual.combat.basic.validate.core.service.ValidateService;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author yan
 * @Date 2024/11/11 下午6:44:24
 * @Description
 */
@Service
public class ValidateCodeServiceImpl implements ValidateService {
    @Resource(name = CaptchaConfig.CAPTCHA_PRODUCER)
    private Producer captchaProducer;

    @Resource(name = CaptchaConfig.CAPTCHA_PRODUCER_MATH)
    private Producer captchaProducerMath;

    /**
     * @return
     */
    @Override
    public CaptchaInfo createCaptcha() {
        CaptchaProperties captchaProperties = SpringUtil.getBean(CaptchaProperties.class);
        boolean captchaEnabled = captchaProperties.getEnabled();
        CaptchaInfo captchaInfo = new CaptchaInfo()
                .setCaptchaEnabled(captchaEnabled);
        if (captchaEnabled) {
            // 保存验证码信息
            String uuid = IdUtil.simpleUUID();
            String captchaKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

            String capStr = null, code = null;
            BufferedImage image = null;

            String captchaType = captchaProperties.getType();
            // 生成验证码
            if ("math".equals(captchaType)) {
                String capText = captchaProducerMath.createText();
                capStr = capText.substring(0, capText.lastIndexOf("@"));
                code = capText.substring(capText.lastIndexOf("@") + 1);
                image = captchaProducerMath.createImage(capStr);
            } else if ("char".equals(captchaType)) {
                capStr = code = captchaProducer.createText();
                image = captchaProducer.createImage(capStr);
            }
            //SpringUtil.getBean(RedisTemplate.class).opsForValue()
            //        .set(captchaKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
            SpringUtil.getBean(RedisService.class)
                    .save(captchaKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
            // 转换流信息写出
            FastByteArrayOutputStream os = new FastByteArrayOutputStream();

            try {

                ImageIO.write(image, "jpg", os);
            } catch (IOException e) {
                throw new GlobalCustomException("生成验证码失败");
            }
            String img = Base64.encode(os.toByteArray());
            //img = ObjectUtils.isEmpty(img) ? null : img;
            captchaInfo.setUuid(uuid)
                    .setImg(img);
        }

        return captchaInfo;
    }


    /**
     * 校验验证码
     */
    @Override
    public void checkCaptcha(String code, String uuid) {
        if (ObjectUtils.isEmpty(code)) {
            throw new GlobalCustomException("验证码不能为空");
        }
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + ObjectUtils.defaultIfEmpty(uuid, "");
        //String captcha = (String) redisTemplate.opsForValue().get(verifyKey);
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String captcha = (String) redisService.get(verifyKey);

        if (captcha == null) {
            throw new GlobalCustomException("验证码已失效");
        }
        redisTemplate.delete(verifyKey);
        redisService.del("",false,verifyKey);
        if (!captcha.equalsIgnoreCase(code)) {
            throw new GlobalCustomException("验证码错误");
        }
    }
}
