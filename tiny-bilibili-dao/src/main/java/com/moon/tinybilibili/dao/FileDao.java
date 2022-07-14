package com.moon.tinybilibili.dao;

import com.moon.tinybilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Chanmoey
 * @date 2022年07月13日
 */
@Mapper
public interface FileDao {

    Integer addFile(File file);

    File getFileByMD5(String md5);
}
