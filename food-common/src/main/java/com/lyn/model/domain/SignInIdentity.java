package com.lyn.model.domain;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用于返回结果的 dto(数据传输对象)对象
 * 该对象最终在userDetailsService中返回
 */
@Data
public class SignInIdentity implements UserDetails {
    private Integer id;
    private String username;
    private String nickname;
    private String password;
    private String phone;
    private String email;
    private String roles;
    private String avatarUrl;
    private int isValid;
    private List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(StrUtil.isNotBlank(this.roles)){
            this.authorities =
                    Stream.of(this.roles.split(",")).
                            map(role->new SimpleGrantedAuthority(role)).
                            collect(Collectors.toList());
        }else{
            // 默认用户最低角色就是 ROLE_USER
//            this.authorities = Collections.singletonList(
//                    new SimpleGrantedAuthority("ROLE_USER")
//            );
            this.authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList("ROLE_USER");
        }
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
