package com.multi.multi_semi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResDto {
    private Long memberCode;
    private String memberId;
    private String memberName;
    private String memberEmail;
    private String memberRole;
}