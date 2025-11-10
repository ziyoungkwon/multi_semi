package com.multi.multi_semi.place.controller;

import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import com.multi.multi_semi.place.service.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/places/{placeId}")
    public ResponseEntity<ResponseDto> findPlaceId(@PathVariable("placeId") int placeId){
        Optional<PlaceDto> place = placeService.findByPlaceId(placeId);

        if(place.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "상세조회실패", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "상세조회성공", place));
    }
}
