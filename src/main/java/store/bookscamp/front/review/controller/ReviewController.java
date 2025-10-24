//package store.bookscamp.front.review.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import store.bookscamp.front.review.feign.ReviewFeignClient;
//import store.bookscamp.front.common.utils.MultipartInputStreamFileResource;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/reviews")
//public class ReviewController {
//
//    private final ReviewFeignClient reviewFeignClient;
//
//    @PostMapping("/image")
//    public ResponseEntity<String> uploadReviewImage(@RequestParam("file") MultipartFile file) throws IOException {
//
//        byte[] bytes = file.getBytes();
//
//        MultipartInputStreamFileResource resource =
//                new MultipartInputStreamFileResource(
//                        new ByteArrayInputStream(bytes),
//                        file.getOriginalFilename(),
//                        bytes.length
//                );
//
//        return reviewFeignClient.uploadReviewImage(resource);
//    }
//}
