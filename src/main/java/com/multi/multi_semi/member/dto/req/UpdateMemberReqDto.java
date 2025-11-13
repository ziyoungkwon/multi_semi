package com.multi.multi_semi.member.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberReqDto {
    private String id;
    private String pwd;
    private String name;
    private String addr;
    private String phone;
    private String intro;
}