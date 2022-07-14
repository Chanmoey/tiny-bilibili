package com.moon.tinybilibili.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Chanmoey
 * @date 2022年07月13日
 */

@Getter
@Setter
public class File {

    private Long id;

    private String url;

    private String type;

    private String md5;

    private Date createTime;
}
