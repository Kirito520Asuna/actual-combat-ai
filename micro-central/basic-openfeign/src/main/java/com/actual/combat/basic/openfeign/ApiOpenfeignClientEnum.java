package com.actual.combat.basic.openfeign;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2025/2/10 3:12:36
 * @Description
 */
@Getter
@AllArgsConstructor
public enum ApiOpenfeignClientEnum {
    DEFAULT;
    String salt;
    String serverName;
    String signAsName;
    String timestampAsName;

    private void saltForServer() {
        this.salt = "API_SALT";
        this.serverName = "DEFAULT";
    }

    ApiOpenfeignClientEnum() {
        saltForServer();
        this.signAsName = "sign";
        this.timestampAsName = "timestamp";
    }

    ApiOpenfeignClientEnum(String signAsName, String timestampAsName) {
        saltForServer();
        this.signAsName = signAsName;
        this.timestampAsName = timestampAsName;
    }
}
