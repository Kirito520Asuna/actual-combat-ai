package com.actual.combat.mp.aop.domain;

import com.actual.combat.mp.utils.MpObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeUser {
    @Schema(description = "管理员key")
    private String adminKey;
    @Schema(description = "当前用户角色key")
    private String roleKey;
    @Schema(description = "部门id")
    private String deptId;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "用户权限")
    private List<DataScopeRole> roles;

    public boolean isAdmin() {
        boolean isAdmin = false;
        //for (DataScopeRole role : roles) {
        //    isAdmin = ObjectUtils.equal(roleKey, adminKey);
        //    if (isAdmin) {
        //        break;
        //    }
        //}
        isAdmin = MpObjectUtils.equal(roleKey, adminKey);
        return isAdmin;
    }
}
