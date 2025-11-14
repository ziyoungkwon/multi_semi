package com.multi.multi_semi.place.dao;


import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PlaceMapper {

    PlaceDto findByPlaceId(int placeId);


    List<PlaceDto> findAllPlaces();

    int selectPlaceTotal();

    List<PlaceDto> selectSearchPlaceList(String query);

    List<PlaceDto> findPlacesByRate(int rating);

    List<PlaceDto> findPlacesByAreaCode(int code);

    List<PlaceDto> findAllPlacesByPaging(SelectCriteria selectCriteria);
}
