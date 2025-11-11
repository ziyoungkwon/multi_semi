package com.multi.multi_semi.member.service;


import com.multi.multi_semi.member.dao.MemberMapper;
import com.multi.multi_semi.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;

    public Optional<MemberDto> findMemberByNo(Long no) {
        Optional<MemberDto> memberDto = memberMapper.findMemberByNo(no);
        return memberDto;
    }

    public Optional<MemberDto> findMemberByEmail(String email) {
        Optional<MemberDto> memberDto = memberMapper.findMemberByEmail(email);
        return memberDto;
    }
}
