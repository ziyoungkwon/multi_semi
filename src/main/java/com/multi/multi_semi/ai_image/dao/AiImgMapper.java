package com.multi.multi_semi.ai_image.dao;

import com.multi.multi_semi.ai_image.dto.AiImgDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiImgMapper {
    /**
     * AI 이미지 생성 정보를 DB에 저장
     */
    void insertAiImg(AiImgDto dto);

    List<AiImgDto> findByEmail(String memEmail);
}
