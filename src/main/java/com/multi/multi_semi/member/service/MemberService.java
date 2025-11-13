package com.multi.multi_semi.member.service;


import com.multi.multi_semi.member.dao.MemberMapper;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.req.UpdateMemberReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public Optional<MemberDto> findMemberByNo(Long no) {
        Optional<MemberDto> memberDto = memberMapper.findMemberByNo(no);
        return memberDto;
    }

    public Optional<MemberDto> findMemberByEmail(String email) {
        Optional<MemberDto> memberDto = memberMapper.findMemberByEmail(email);
        return memberDto;
    }

    public int updateMemberInfo(String email, UpdateMemberReqDto updateMemberReqDto) {
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(email);

        memberDto.setId(updateMemberReqDto.getId());
        memberDto.setName(updateMemberReqDto.getName());
        memberDto.setAddr(updateMemberReqDto.getAddr());
        memberDto.setPhone(updateMemberReqDto.getPhone());
        memberDto.setIntro(updateMemberReqDto.getIntro());

        String newPassword = updateMemberReqDto.getPwd();
        if (newPassword != null && !newPassword.isEmpty()) {
            memberDto.setPwd(passwordEncoder.encode(newPassword));
        }

        int result = memberMapper.updateMemberInfo(memberDto);
        return result;
    }

    public int updateMemberPwd(String email, UpdateMemberReqDto updateMemberReqDto) {
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(email);
        memberDto.setPwd(passwordEncoder.encode(updateMemberReqDto.getPwd()));
        int result = memberMapper.updateMemberPwd(memberDto);
        return result;
    }

    public int deleteMemberByEmail(String email) {
        int result = memberMapper.deleteMemberByEmail(email);
        return result;
    }
}
