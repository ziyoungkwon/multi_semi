package com.multi.multi_semi.ai_image.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@RestController
@RequestMapping("/api/v1")
public class ImageDownloadController {

    @GetMapping("/download-image")
    public ResponseEntity<Resource> downloadImageProxy(@RequestParam("url") String imageUrl) {

        try {
            // 1. JavaScript가 보낸 Azure URL로 URL 객체 생성
            URL url = new URL(imageUrl);

            // 2. Spring 서버가 Azure 서버에 연결 (서버 <-> 서버 통신. CORS 없음!)
            URLConnection connection = url.openConnection();

            // 3. 이미지 데이터를 InputStream으로 가져옴
            InputStream inputStream = connection.getInputStream();

            // 4. Spring의 Resource 객체로 래핑
            Resource resource = new InputStreamResource(inputStream);

            // 5. (★중요★) 브라우저에게 "이건 화면에 띄우지 말고 다운로드해라"고 명령
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "generated_image.png");
            headers.setContentType(MediaType.IMAGE_PNG); // PNG 이미지라고 명시

            // 6. 200 OK 상태와, 헤더, 이미지 데이터를 함께 반환
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            // (실제로는 로깅을 해야 함)
            e.printStackTrace();
            // 에러 발생 시, 500 Internal Server Error 반환
            return ResponseEntity.status(500).build();
        }
    }
}
