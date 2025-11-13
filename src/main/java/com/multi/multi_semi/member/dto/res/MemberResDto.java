package com.multi.multi_semi.member.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResDto {
    private Long no;
    private String id;
    private String email;
    private String name;
    private String addr;
    private String phone;
    private String intro;
    private String role;
}