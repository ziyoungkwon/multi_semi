package com.multi.multi_semi.auth.service;


import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.member.dao.MemberMapper;
import com.multi.multi_semi.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberDto memberDto = memberMapper.findMemberByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("이메일과 비밀번호를 확인해주세요."));

        return CustomUser.builder()
                .no(memberDto.getNo())
                .id(memberDto.getId())
                .email(memberDto.getEmail())
                .pwd(memberDto.getPwd())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(memberDto.getRole())))
                .build();
    }
}
