package com.actual_combat.auth.shiro.pojo;

import com.actual_combat.base.core.pojo.auth.User;
import com.actual_combat.base.core.pojo.auth.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author yan
 * @Date 2023/5/12 0012 16:18
 * @Description
 */
@Data
//@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserBase extends User {
    public UserBase(UserInfo userInfo, List<String> roles) {
        super(userInfo, roles);
    }
}
