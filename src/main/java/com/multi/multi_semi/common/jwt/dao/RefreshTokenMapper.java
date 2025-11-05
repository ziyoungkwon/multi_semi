package com.multi.multi_semi.common.jwt.dao;


import com.multi.multi_semi.common.jwt.dto.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {


    Optional<RefreshToken> findByEmail(String email);

    void deleteRefreshTokenByEmail(String email);

    void insertRefreshTokenByEmail(RefreshToken newToken);
}
