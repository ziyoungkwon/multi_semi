package com.multi.multi_semi.auth.service;


import com.multi.multi_semi.common.exception.*;
import com.multi.multi_semi.common.jwt.dto.TokenDto;
import com.multi.multi_semi.common.jwt.service.TokenService;
import com.multi.multi_semi.member.dao.MemberMapper;
import com.multi.multi_semi.member.dto.MemberReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;
    private final TokenService tokenService;


    @Transactional
    public int signup(MemberReqDto memberReqDto) {
        if(memberReqDto.getPwd() == null || memberReqDto.getPwd().isEmpty() || memberReqDto.getPwd().length() < 8){
            throw new InvalidPasswordException("비밀번호는 8자리 이상이어야 합니다.");
        }
        if(memberMapper.findMemberByEmail(memberReqDto.getEmail()).isPresent()){
            throw new DuplicateUserEmailException("중복된 이메일입니다. 다른 이메일로 시도해주세요.");
        }
        if(memberMapper.findMemberById(memberReqDto.getId()).isPresent()){
            throw new DuplicateUserIdException("중복된 아이디입니다. 다른 아이디로 시도해주세요.");
        }

        memberReqDto.setPwd(passwordEncoder.encode(memberReqDto.getPwd()));
        int result = memberMapper.insertMember(memberReqDto);
        if (result <= 0) {
            throw new MemberRegistrationException("회원가입 실패. 다시 시도해주세요.");
        }

        return result;
    }

    // 이메일, 비밀번호 직접 입력하여 로그인
    @Transactional
    public TokenDto login(MemberReqDto memberReqDto) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(memberReqDto.getEmail());

        if(!passwordEncoder.matches(memberReqDto.getPwd(), userDetails.getPassword()))
        {
            throw new BadCredentialsException("이메일 또는 비밀번호를 확인해주세요.");
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String , Object> loginData = new HashMap<>();
        loginData.put("email", memberReqDto.getEmail());
        loginData.put("roles", roles);

        TokenDto tokenDto = tokenService.createTokenForLogin(loginData);
        return tokenDto;

    }
}
