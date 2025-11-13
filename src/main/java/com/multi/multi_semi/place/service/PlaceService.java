package com.multi.multi_semi.place.service;

import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.place.dao.PlaceMapper;
import com.multi.multi_semi.place.dto.PlaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceMapper placeMapper;

    public Optional<PlaceDto> findByPlaceId(int placeId) {
        Optional<PlaceDto> placeDto = placeMapper.findByPlaceId(placeId);
        return placeDto;
    }

    public List<PlaceDto> findAllPlaces() {

        List<PlaceDto> places = placeMapper.findAllPlaces();

        return places;
    }
}

