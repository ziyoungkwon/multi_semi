package com.multi.multi_semi.place.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.place.dao.PlaceMapper;
import com.multi.multi_semi.place.dto.PlaceDto;
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

    @Value("${image.image-url}")
    private String IMAGE_URL;

    public Optional<PlaceDto> findByPlaceId(int placeId) {
        Optional<PlaceDto> placeDto = placeMapper.findByPlaceId(placeId);
        return placeDto;
    }

    public List<PlaceDto> findAllPlaces(SelectCriteria selectCriteria) {
        List<PlaceDto> placeList = placeMapper.findAllPlaces(selectCriteria);
        for(int i = 0 ; i < placeList.size() ; i++) {
            placeList.get(i).setImageUrl(IMAGE_URL + placeList.get(i).getImageUrl());
        }

        return placeList;
    }

    public int selectReviewTotal() {
        int result = placeMapper.selectPlaceTotal();

        return result;
    }

    public List<PlaceDto> selectSearchPlaceList(String query) {
        List<PlaceDto> placeList = placeMapper.selectSearchProductList(query);
        for(int i = 0 ; i < placeList.size() ; i++) {
            placeList.get(i).setImageUrl(IMAGE_URL + placeList.get(i).getImageUrl());
        }
        return placeList;
    }

    public List<PlaceDto> findPlacesByRate(int rating) {

        List<PlaceDto> placeList = placeMapper.findPlacesByRate(rating);
        for(int i = 0 ; i < placeList.size() ; i++) {
            placeList.get(i).setImageUrl(IMAGE_URL + placeList.get(i).getImageUrl());
        }

        return placeList;
    }

    public List<PlaceDto> findPlacesByAreaCode(int code) {

        List<PlaceDto> placeList = placeMapper.findPlacesByAreaCode(code);
        for(int i = 0 ; i < placeList.size() ; i++) {
            placeList.get(i).setImageUrl(IMAGE_URL + placeList.get(i).getImageUrl());
        }

        return placeList;
    }
}

