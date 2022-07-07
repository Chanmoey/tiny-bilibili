package com.moon.tinybilibili.domain;

import com.moon.tinybilibili.domain.constant.UserConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Getter
@Setter
public class UserInfo {

    private Long id;

    private Long userId;

    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    private Date createTime;

    private Date updateTime;

    private Boolean followed;

    public void setDefaultData() {
        this.nick = UserConstant.DEFAULT_NICK;
        this.birth = UserConstant.DEFAULT_BIRTH;
        this.gender = UserConstant.GENDER_UNKNOWN;
        this.createTime = new Date();
    }
}
