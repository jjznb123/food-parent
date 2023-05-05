package com.lyn.model.pojo;

import com.lyn.model.common.BaseModel;
import lombok.Data;

@Data
public class Diners extends BaseModel {
    private Integer id;
    private String username;
    private String nickname;
    private String password;
    private String phone;
    private String email;
    private String roles;
    private String avatarUrl;
}
