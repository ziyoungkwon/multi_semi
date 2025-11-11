package com.multi.multi_semi.common.jwt.dao;


import com.multi.multi_semi.common.jwt.dto.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {
    Optional<RefreshToken> findByEmail(@Param("email") String email);

    void deleteRefreshTokenByEmail(@Param("email") String email);

    void insertRefreshToken(RefreshToken newToken);
}
