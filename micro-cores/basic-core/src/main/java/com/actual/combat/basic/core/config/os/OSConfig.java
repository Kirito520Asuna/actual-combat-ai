package com.actual.combat.basic.core.config.os;

import cn.hutool.core.util.StrUtil;
import com.actual.combat.basic.enums.OSType;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;


/**
 * @Author yan
 * @Date 2025/3/11 0:06:31
 * @Description
 */
@Configuration
public class OSConfig {
    public static String os = getOS();
    public static String separator = getSeparator(os);

    @PostConstruct
    void init() {
        os = getOS();
        separator = getSeparator(os);
    }

    public static String getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains(OSType.win.name())) {
            os = OSType.win.name();
        } else if (os.contains(OSType.mac.name())) {
            os = OSType.mac.name();
        } else if (os.contains(OSType.linux.name())) {
            os = OSType.linux.name();
        }
        return os;
    }

    public static boolean isWindows(String os) {
        if (StrUtil.isBlank(os)) {
            os = OSConfig.os;
        }
        return os.contains(OSType.win.name());
    }

    public static boolean isMac(String os) {
        if (StrUtil.isBlank(os)) {
            os = OSConfig.os;
        }
        return os.contains(OSType.mac.name());
    }

    public static boolean isLinux(String os) {
        if (StrUtil.isBlank(os)) {
            os = OSConfig.os;
        }
        return os.contains(OSType.linux.name()) || os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    public static String getSeparator(String os) {
        String separator;
        if (StrUtil.isBlank(os)) {
            os = getOS();
        }
        if (isWindows(os)) {
            separator = OSType.win.getSeparator();
        } else if (isMac(os)) {
            separator = OSType.mac.getSeparator();
        } else if (isLinux(os)) {
            separator = OSType.linux.getSeparator();
        } else {
            separator = OSType.linux.getSeparator();
        }
        return separator;
    }


    public static boolean isWindows() {
        return isWindows(os);
    }

    public static boolean isMac() {
        return isMac(os);
    }

    public static boolean isLinux() {
        return isLinux(os);
    }

    public static String getSeparator() {
        return getSeparator(os);
    }
}
