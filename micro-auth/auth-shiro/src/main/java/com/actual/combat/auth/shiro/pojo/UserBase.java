package com.actual.combat.auth.shiro.pojo;

import com.actual.combat.basic.core.pojo.auth.User;
import com.actual.combat.basic.core.pojo.auth.UserInfo;
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
