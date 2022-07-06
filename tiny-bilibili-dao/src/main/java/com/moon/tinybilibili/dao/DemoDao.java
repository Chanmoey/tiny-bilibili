package com.moon.tinybilibili.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Mapper
public interface DemoDao {

    Long query(Long id);
}
