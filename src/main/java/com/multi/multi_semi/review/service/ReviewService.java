package com.multi.multi_semi.review.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.common.util.FileUploadUtils;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import com.multi.multi_semi.review.dao.ReviewMapper;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {

    private final ReviewMapper reviewMapper;

    @Value("${image.image-dir}")
    private String IMAGE_DIR;


    @Value("${image.image-url}")
    private String IMAGE_URL;

    public List<ReviewResDto> findReviewList() {

        List<ReviewResDto> reviewList = reviewMapper.findReviewList();
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }
        return reviewList;
    }

    public ReviewResDto findReviewByNo(String reviewNo) {

        ReviewResDto review = reviewMapper.findReviewByNo(Long.parseLong(reviewNo));
        if (review.getImgUrl() != null && !review.getImgUrl().isEmpty()) {
            review.setImgUrl(IMAGE_URL + review.getImgUrl());
        }

        return review;
    }


    public int insertReview(ReviewReqDto reviewReqDto) {

        String imageName = UUID.randomUUID().toString().replace("-", "");
        String replaceFileName = null;
        int result = 0;

        try {
            replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName, reviewReqDto.getImgFile());
            log.info("[ProductService] replaceFileName : " + replaceFileName);

            reviewReqDto.setImgUrl(replaceFileName);

            log.info("[ProductService] insert Image Name : "+ replaceFileName);

            result = reviewMapper.insertReview(reviewReqDto);

        } catch (IOException e) {
            log.info("[ProductService] IOException IMAGE_DIR : "+ IMAGE_DIR);

            log.info("[ProductService] IOException deleteFile : "+ replaceFileName);

            // FileUploadUtils.deleteFile(IMAGE_DIR, replaceFileName);
            throw new RuntimeException(e);
        }

        return result;
    }

    public int updateReview(ReviewReqDto reviewReqDto) {

        String replaceFileName = null;
        int result = 0;

        try {
            String oriImage = reviewMapper.findReviewByNo(reviewReqDto.getNo()).getImgUrl();
            log.info("[updateProduct] oriImage : " + oriImage);

            if(reviewReqDto.getImgFile() != null && !reviewReqDto.getImgFile().isEmpty()) {
                // 이미지 변경 진행
                String imageName = UUID.randomUUID().toString().replace("-", "");
                replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName, reviewReqDto.getImgFile());

                log.info("[updateProduct] IMAGE_DIR!!"+ IMAGE_DIR);
                log.info("[updateProduct] imageName!!"+ imageName);

                log.info("[updateProduct] InsertFileName : " + replaceFileName);
                reviewReqDto.setImgUrl(replaceFileName);

                log.info("[updateProduct] deleteImage : " + oriImage);
                boolean isDelete = FileUploadUtils.deleteFile(IMAGE_DIR, oriImage);
                log.info("[update] isDelete : " + isDelete);
            } else {
                reviewReqDto.setImgUrl(oriImage);
            }

            result = reviewMapper.updateReview(reviewReqDto);

        } catch (IOException e) {
            log.info("[updateProduct] Exception!!");
            // FileUploadUtils.deleteFile(IMAGE_DIR, replaceFileName);
            throw new RuntimeException(e);
        }

        return result;
    }

    public int deleteReview(String reviewId) {

        int result = reviewMapper.deleteReview(Integer.parseInt(reviewId));

        return result;
    }

    public List<ReviewResDto> findReviewByMemberId(String memberId) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByMemberId(memberId);
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }

        return reviewList;
    }

    public Double getPlaceRate(Long placeId) {
        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceId(placeId);

        if (reviewList == null || reviewList.isEmpty()) {
            return 0.0; // 리뷰 없을 때 0점
        }

        int sum = 0;
        for (ReviewResDto reviewResDto : reviewList) {
            sum += reviewResDto.getRate();
        }

        double avg = (double) sum / reviewList.size();
        return Math.round(avg * 10.0) / 10.0; // 소수점 1자리 반올림
    }

    public List<ReviewResDto> findReviewByPlaceId(String placeId) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceIdPaging(Integer.parseInt(placeId));
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }

        return reviewList;
    }

    public Object selectReviewListWithPaging(SelectCriteria selectCriteria, int placeNo) {

        List<ReviewResDto> list = reviewMapper.selectReviewListWithPaging(selectCriteria,placeNo);
        return list;
    }

    public int getReviewCount(int placeNo) { return reviewMapper.countReview(placeNo);}

}
