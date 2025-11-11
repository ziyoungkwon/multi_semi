package com.multi.multi_semi.place.dao;


import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.MemberReqDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PlaceMapper {

    Optional<PlaceDto> findByPlaceId(int placeId);


    List<PlaceDto> findAllPlaces(SelectCriteria selectCriteria);

    int selectPlaceTotal();

    List<PlaceDto> selectSearchProductList(String query);

    List<PlaceDto> findPlacesByRate(int rating);

    List<PlaceDto> findPlacesByAreaCode(int code);
}
