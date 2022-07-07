package com.moon.tinybilibili.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Getter
@Setter
public class User {
    
    private Long id;

    private String phone;

    private String email;

    private String password;

    private String salt;

    private Date createTime;

    private Date updateTime;

    private UserInfo userInfo;
}
