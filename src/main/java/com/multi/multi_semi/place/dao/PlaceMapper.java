package com.multi.multi_semi.place.dao;


import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.MemberReqDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PlaceMapper {

    Optional<PlaceDto> findByPlaceId(int placeId);


    List<PlaceDto> findAllPlaces();
}
