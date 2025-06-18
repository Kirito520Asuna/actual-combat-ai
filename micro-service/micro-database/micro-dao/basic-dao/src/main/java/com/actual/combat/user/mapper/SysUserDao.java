package com.actual.combat.user.mapper;

import com.actual.combat.basic.core.enums.im.ChatType;
import com.actual.combat.basic.core.vo.user.UserVo;
import com.actual.combat.mp.abs.mapper.MpMapper;
import com.actual.combat.user.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Mapper
public interface SysUserDao extends MpMapper<SysUser> {
    int updateBatch(List<SysUser> list);

    int updateBatchSelective(List<SysUser> list);

    int batchInsert(@Param("list") List<SysUser> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysUser> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysUser record);

    int insertOrUpdateSelective(SysUser record);

    /**
     * @param user
     * @param friendIds
     * @param commonDatasource 相同数据源
     * @return
     */
    List<SysUser> getFriends(@Param("user") SysUser user, @Param("friendIds") List<Long> friendIds, @Param("commonDatasource") boolean commonDatasource);

    List<SysUser> applyList(@Param("userId") Long userId);

    SysUser getUser(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("chatType") ChatType chatType);

    SysUser getOneUser(@Param("userId") Long userId, @Param("nowUserId") Long nowUserId);

    List<SysUser> getUsers(SysUser user);
    UserVo getUserVoById(Long id);

    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser sysUser);
}