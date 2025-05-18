package com.actual_combat.base.page;

import com.actual_combat.base.view.BaseJsonView;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页封装类
 *
 * @author yan
 */
@Schema(description = "通用分页")
@Data
@NoArgsConstructor
@AllArgsConstructor @Accessors(chain = true)
@JsonView(BaseJsonView.BaseView.class)
public class ResultPage<T> implements Serializable, AtrPage {
    private static final long serialVersionUID = 5716478706296860374L;
    /**
     * 返回列表
     */

    @Schema(description = "返回列表")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("list")
    private List<T> list;
    /**
     * 每页记录数
     */

    @Schema(description = "每页记录数", example = "10")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("pageSize")
    private long pageSize;
    /**
     * 当前页码数
     */

    @Schema(description = "当前页码数", example = "1")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("pageNumber")
    private long pageNumber;
    /**
     * 总页码数
     */

    @Schema(description = "总页码数", example = "2")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("pages")
    private long pages;
    /**
     * 总记录数
     */

    @Schema(description = "总记录数", example = "13")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("total")
    private long total;

}
