package com.multi.multi_semi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long no;
    private String id;
    private String email;
    private String pwd;
    private String name;
    private String addr;
    private String phone;
    private String intro;
    private String role;
    private String uuid;
}