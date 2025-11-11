package com.multi.multi_semi.member.dao;


import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.MemberReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<MemberDto> findMemberByNo(@Param("no") Long no);

    Optional<MemberDto> findMemberById(@Param("id") String id);

    Optional<MemberDto> findMemberByEmail(@Param("email") String email);

    int insertMember(MemberReqDto memberReqDto);

    int insertOAuthMember(MemberDto newMember);

    int updateUuidByNo(@Param("no") Long no, @Param("uuid") String uuid);
}
