package com.moon.tinybilibili.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Chanmoey
 * @date 2022年07月07日
 */
@Getter
@Setter
public class UserFollowing {

    private Long id;

    private Long userId;

    private Long followingId;

    private Long groupId;

    private Date createTime;

    private UserInfo userInfo;
}
