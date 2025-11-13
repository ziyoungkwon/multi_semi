package com.multi.multi_semi.member.dao;


import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.MemberReqDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<MemberDto> findByMemberId(String memberId);

    Optional<MemberDto> findByEmail(String memberEmail);

    int insertMember(MemberReqDto memberReqDto);

    int insertOAuthMember(MemberDto newMember);
}
