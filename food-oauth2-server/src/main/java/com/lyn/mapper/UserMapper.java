package com.lyn.mapper;

import com.lyn.model.pojo.Diners;
import org.apache.ibatis.annotations.Select;

/**
 * @Author Liu
 * @Date 2023/4/26 15:14
 * @PackageName:com.lyn.mapper
 * @ClassName: UserMapper
 * @Version 1.0
 */
public interface UserMapper {
    @Select("select id, username, nickname, phone, email, " +
            "password, avatar_url, roles, is_valid from t_diners where " +
            "(username = #{account} or phone = #{account} or email = #{account})")
    Diners findByUsername(String account);
}
