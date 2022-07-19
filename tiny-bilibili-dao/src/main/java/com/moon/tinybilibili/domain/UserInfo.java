package com.moon.tinybilibili.domain;

import com.moon.tinybilibili.domain.constant.UserConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Getter
@Setter
@Document(indexName = "user-infos")
public class UserInfo {

    @Id
    private Long id;

    private Long userId;

    @Field(type = FieldType.Text)
    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Date)
    private Date updateTime;

    private Boolean followed;

    public void setDefaultData() {
        this.nick = UserConstant.DEFAULT_NICK;
        this.birth = UserConstant.DEFAULT_BIRTH;
        this.gender = UserConstant.GENDER_UNKNOWN;
        this.createTime = new Date();
    }
}
