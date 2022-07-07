package com.moon.tinybilibili.dao;

import com.moon.tinybilibili.domain.User;
import com.moon.tinybilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Mapper
public interface UserDao {

    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUsers(User user);

    Integer updateUserInfos(UserInfo userInfo);
}
