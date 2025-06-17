package com.actual.combat.user.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.mp.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.actual.combat.dept.domain.SysDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */

/**
 * 用户信息表
 */
@Schema(description = "用户信息表")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.user)
public class SysUser extends BaseEntity implements Serializable {
    /**
     * 用户ID
     */
    //@TableId(value = "`user_id`", type = IdType.AUTO)
    @TableId(value = COL_USER_ID, type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户账号
     */
    @TableField(value = "`user_name`")
    @Schema(description = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @TableField(value = "`nick_name`")
    @Schema(description = "用户昵称")
    private String nickName;

    /**
     * 用户类型（00系统用户）
     */
    @TableField(value = "`user_type`")
    @Schema(description = "用户类型（00系统用户）")
    private String userType;

    /**
     * 用户邮箱
     */
    @TableField(value = "`email`")
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @TableField(value = "`phone_number`")
    @Schema(description = "手机号码")
    private String phoneNumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @TableField(value = "`sex`")
    @Schema(description = "用户性别（0男 1女 2未知）")
    private String sex;

    /**
     * 头像地址
     */
    @TableField(value = "`avatar`")
    @Schema(description = "头像地址")
    private String avatar;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    @Schema(description = "密码")
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description = "帐号状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "`del_flag`")
    @Schema(description = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    /**
     * 最后登录IP
     */
    @TableField(value = "`login_ip`")
    @Schema(description = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @TableField(value = "`login_date`")
    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    /**
     * 部门对象
     */
    @Schema(description = "部门对象")
    @TableField(exist = false)
    private SysDept dept;

    /**
     * 角色对象
     */
    @Schema(description = "角色对象")
    @TableField(exist = false)
    private List<SysRole> roles;

    private static final long serialVersionUID = 1L;

    public static final String COL_USER_ID = TableConstants.USER_COL_USER_ID;

    public static final String COL_USER_NAME = "user_name";

    public static final String COL_NICK_NAME = "nick_name";

    public static final String COL_USER_TYPE = "user_type";

    public static final String COL_EMAIL = "email";

    public static final String COL_PHONE_NUMBER = "phone_number";

    public static final String COL_SEX = "sex";

    public static final String COL_AVATAR = "avatar";

    public static final String COL_PASSWORD = "password";

    public static final String COL_STATUS = "status";

    public static final String COL_DEL_FLAG = "del_flag";

    public static final String COL_LOGIN_IP = "login_ip";

    public static final String COL_LOGIN_DATE = "login_date";

}