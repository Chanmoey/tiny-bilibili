package com.moon.tinybilibili.dao;

import com.moon.tinybilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Chanmoey
 * @date 2022年07月12日
 */
@Mapper
public interface AuthRoleDao {
    AuthRole getRoleByCode(String code);
}
