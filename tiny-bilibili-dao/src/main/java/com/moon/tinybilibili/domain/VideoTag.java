package com.moon.tinybilibili.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VideoTag {

    private Long id;

    private Long videoId;

    private Long tagId;

    private Date createTime;
}
