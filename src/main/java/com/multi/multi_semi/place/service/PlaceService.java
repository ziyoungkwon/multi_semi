package com.multi.multi_semi.place.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.place.dao.PlaceMapper;
import com.multi.multi_semi.place.dto.PlaceDto;
import com.multi.multi_semi.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceMapper placeMapper;
    private final ReviewService reviewService;


    public PlaceDto findByPlaceId(int placeId) {
        PlaceDto placeDto = placeMapper.findByPlaceId(placeId);

        placeDto.setAvgRate(reviewService.getPlaceRate(placeDto.getNo()));
        return placeDto;
    }

    public List<PlaceDto> findAllPlaces() {
        List<PlaceDto> placeList = placeMapper.findAllPlaces();

        for(PlaceDto placeDto : placeList) {
            placeDto.setAvgRate(reviewService.getPlaceRate(placeDto.getNo()));
        }

        return placeList;
    }

    public int selectPlaceTotal() {
        int result = placeMapper.selectPlaceTotal();

        return result;
    }

    public List<PlaceDto> selectSearchPlaceList(String query) {
        List<PlaceDto> placeList = placeMapper.selectSearchPlaceList(query);
        for(PlaceDto placeDto : placeList) {
            placeDto.setAvgRate(reviewService.getPlaceRate(placeDto.getNo()));
        }

        return placeList;

    }

    public List<PlaceDto> findPlacesByRate(int rating) {

        List<PlaceDto> placeList = placeMapper.findPlacesByRate(rating);
        for(PlaceDto placeDto : placeList) {
            placeDto.setAvgRate(reviewService.getPlaceRate(placeDto.getNo()));
        }


        return placeList;
    }

    public List<PlaceDto> findPlacesByAreaCode(int code) {

        List<PlaceDto> placeList = placeMapper.findPlacesByAreaCode(code);
        for(PlaceDto placeDto : placeList) {
            placeDto.setAvgRate(reviewService.getPlaceRate(placeDto.getNo()));
        }
        for(PlaceDto placeDto : placeList){
            System.out.println(">>>>>>>>>>>>>>>>>>>>IMGURL : " + placeDto.getImageUrl());
        }

        return placeList;
    }

    public Object findAllPlacesByPaging(SelectCriteria selectCriteria) {
        List<PlaceDto> placeList = placeMapper.findAllPlacesByPaging(selectCriteria);
        for(PlaceDto placeDto : placeList) {
            placeDto.setAvgRate(reviewService.getPlaceRate(placeDto.getNo()));
        }

        return placeList;
    }


}

