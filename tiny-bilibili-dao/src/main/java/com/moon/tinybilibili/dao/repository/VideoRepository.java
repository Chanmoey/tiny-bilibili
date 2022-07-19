package com.moon.tinybilibili.dao.repository;

import com.moon.tinybilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Chanmoey
 * @date 2022年07月19日
 */
public interface VideoRepository extends ElasticsearchRepository<Video, Long> {

    Video findByTitleLike(String keyword);

}
