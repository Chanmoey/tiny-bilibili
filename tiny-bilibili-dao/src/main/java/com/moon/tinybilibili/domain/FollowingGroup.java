package com.moon.tinybilibili.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2022年07月07日
 */
@Getter
@Setter
public class FollowingGroup {

    private Long id;

    private Long userId;

    private String name;

    private String type;

    private Date createTime;

    private Date updateTime;

    private List<UserInfo> followingUserInfoList;
}
