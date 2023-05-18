package com.lyn.model.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SignInDinerInfo implements Serializable {
    private Integer id;
    private String username;
    private String nickname;
    private String password;
    private String phone;
    private String email;
    private String roles;
    private String avatarUrl;
    private int isValid;
}
