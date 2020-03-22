package com.cx.pojo;

import lombok.Data;

/**
 * @Author 苍晓
 * @Date 2020/3/20 13:43
 */
@Data
public class User {

    private Integer id;
    private String openId;
    private String nickName;
    private String avatar;
    private String gender;
}
