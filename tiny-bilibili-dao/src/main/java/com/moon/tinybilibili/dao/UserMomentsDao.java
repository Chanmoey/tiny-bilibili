package com.moon.tinybilibili.dao;

import com.moon.tinybilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Chanmoey
 * @date 2022年07月10日
 */
@Mapper
public interface UserMomentsDao {
    Integer addUserMoments(UserMoment userMoment);
}
