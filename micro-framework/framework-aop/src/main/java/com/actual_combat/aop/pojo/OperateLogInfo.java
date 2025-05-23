package com.actual_combat.aop.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2024/5/15 0015 10:32
 * @Description
 */
@Data @NoArgsConstructor @AllArgsConstructor @Accessors(chain = true)
public class OperateLogInfo {
    private static final long serialVersionUID = -3962640568516032970L;
    /**
     * 链路追踪编号
     */
    @Schema(description = "链路追踪编号")
    private String traceId;
    /**
     * 用户编号
     */
    @Schema(description = "用户编号")
    private String userId;
    /**
     * 用户类型
     */
    @Schema(description = "用户类型")
    private String userType = "NO_USERS";
    /**
     * 操作模块
     */
    @Schema(description = "操作模块")
    private String module;
    /**
     * 操作名
     */
    @Schema(description = "操作名")
    private String name;
    /**
     * 操作分类
     */
    @Schema(description = "操作分类")
    private String type;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String businessType;
    /**
     * 操作明细
     */
    @Schema(description = "操作明细")
    private String content;
    /**
     * 请求方法名
     */
    @Schema(description = "请求方法名")
    private String requestMethod;
    /**
     * 请求地址
     */
    @Schema(description = "请求地址")
    private String requestUrl;
    /**
     * 请求服务名
     */
    @Schema(description = "请求服务名")
    private String requestApplication;
    /**
     * 用户 IP
     */
    @Schema(description = "用户 IP")
    private String userIp;
    /**
     * 浏览器 UserAgent
     */
    @Schema(description = "浏览器 UserAgent")
    private String userAgent;
    /**
     * Java 方法名
     */
    @Schema(description = "Java 方法名")
    private String javaMethod;
    /**
     * Java 方法的参数
     */
    @Schema(description = "Java 方法的参数")
    private String javaMethodArgsParams;
    /**
     * javaMethodArgsBody
     */
    @Schema(description = "javaMethodArgsBody")
    private String javaMethodArgsBody;
    /**
     * Body json化 * 是否成功(存在参数有HttpServletResponse时解析失败,可能有传输文件时失败)
     */
    @Schema(description = "Body json化 * 是否成功(存在参数有HttpServletResponse时解析失败,可能有传输文件时失败)")
    private Boolean javaMethodArgsBodyToJson;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Long startTime;
    /**
     * 执行时长，单位：毫秒
     */
    @Schema(description = "执行时长，单位：毫秒")
    private Long duration;
    /**
     * 结果码
     */
    @Schema(description = "结果码")
    private String resultCode;
    /**
     * 结果提示
     */
    @Schema(description = "结果提示")
    private String resultMsg;
    /**
     * 结果数据
     */
    @Schema(description = "结果数据")
    private String resultData;
    /**
     * 响应时间
     */
    @Schema(description = "响应时间")
    private Long resultTime;

}
