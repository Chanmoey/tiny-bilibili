package com.moon.tinybilibili.service;

import com.moon.tinybilibili.dao.repository.UserInfoRepository;
import com.moon.tinybilibili.dao.repository.VideoRepository;
import com.moon.tinybilibili.domain.UserInfo;
import com.moon.tinybilibili.domain.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Chanmoey
 * @date 2022年07月19日
 */
@Service
public class ElasticsearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public void addUseInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    public Video getVideo(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }
}
