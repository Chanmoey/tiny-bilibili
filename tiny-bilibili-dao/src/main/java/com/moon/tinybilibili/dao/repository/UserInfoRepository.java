package com.moon.tinybilibili.dao.repository;

import com.moon.tinybilibili.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Chanmoey
 * @date 2022年07月19日
 */
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {
}
