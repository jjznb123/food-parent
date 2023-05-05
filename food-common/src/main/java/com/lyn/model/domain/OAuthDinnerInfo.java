package com.lyn.model.domain;

import lombok.Data;

@Data
public class OAuthDinnerInfo {
    private String nickname;
    private String email;
    private String expTime;
    private String accessToken;
    private String refreshToken;
}
