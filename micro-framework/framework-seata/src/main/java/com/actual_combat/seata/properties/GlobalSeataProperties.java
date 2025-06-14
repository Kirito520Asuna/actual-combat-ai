package com.actual_combat.seata.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/4/25 20:52:08
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "config.seata")
public class GlobalSeataProperties {
    Boolean toGlobal = false;
}
