package com.multi.multi_semi.place.controller;

import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.paging.Pagenation;
import com.multi.multi_semi.common.paging.ResponseDtoWithPaging;
import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import com.multi.multi_semi.place.service.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/places")
    public ResponseEntity<ResponseDto> findAllPlacesByPaging(@RequestParam(name = "offset", defaultValue = "1") String offset){
        SelectCriteria selectCriteria = getSelectCriteria(Integer.parseInt(offset), placeService.selectReviewTotal());

        ResponseDtoWithPaging responseDtoWithPaging = new ResponseDtoWithPaging(placeService.findAllPlaces(selectCriteria), selectCriteria);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "장소 리스트 조회 성공", responseDtoWithPaging));
    }

    private SelectCriteria getSelectCriteria(int offset, int totalCount) {
        int limit = 10;
        int buttonAmount = 10;
        return Pagenation.getSelectCriteria(offset, totalCount, limit, buttonAmount);
    }

    @GetMapping("/search/places")
    public ResponseEntity<ResponseDto> selectSearchPlaceList(@RequestParam(name = "query", defaultValue = "") String query) {

        List<PlaceDto> places = placeService.selectSearchPlaceList(query);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "장소 검색 성공", places));

    }

    @GetMapping("/places/rate")
    public ResponseEntity<ResponseDto> findPlacesByRate(@RequestParam(name = "rating") int rating){
        List<PlaceDto> places = placeService.findPlacesByRate(rating);

        return ResponseEntity.ok(
                new ResponseDto(HttpStatus.OK, rating + "점대 장소 조회 성공", places)
        );
    }

    @GetMapping("/places/district")
    public ResponseEntity<ResponseDto> findPlacesByAreaCode(@RequestParam("dist") int code) {
        List<PlaceDto> places = placeService.findPlacesByAreaCode(code);
        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "권역별 관광지 조회 성공", places));
    }
}
