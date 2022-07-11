package com.moon.tinybilibili.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Chanmoey
 * @date 2022年07月10日
 */
@Getter
@Setter
public class UserMoment {

    private Long id;

    private Long userId;

    private String type;

    private Long contentId;

    private Date createTime;

    private Date updateTime;
}
