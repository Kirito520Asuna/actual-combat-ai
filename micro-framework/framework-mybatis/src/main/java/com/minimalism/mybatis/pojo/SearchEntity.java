package com.minimalism.mybatis.pojo;

import com.minimalism.mybatis.abs.entity.AbsEntity;
import com.minimalism.mybatis.utils.MpObjectUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data @Slf4j
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class SearchEntity implements AbsEntity {
    /**
     * 搜索值
     */
    @JsonIgnore
    private String searchValue;
    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params = new HashMap<>();

    @PostConstruct
    public void init() {
        toEntityParams();
    }

    protected Map<String, Object> toEntityParams() {
        log.debug("execute toEntityParams method");
        Map<String, Object> toEntityParams = toParams(MpObjectUtils.defaultIfEmpty(getParams(), this));
        this.params = toEntityParams;
        return toEntityParams;
    }

    public Map<String, Object> toParams() {
        return toEntityParams();
    }
}
