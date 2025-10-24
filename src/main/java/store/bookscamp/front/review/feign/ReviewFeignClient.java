//package store.bookscamp.front.review.feign;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.core.io.Resource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestPart;
//import store.bookscamp.front.common.config.FeignMultipartConfig;
//
//@FeignClient(
//        name = "gateway",
//        url = "http://localhost:8080/api-server",
//        configuration = FeignMultipartConfig.class
//)
//public interface ReviewFeignClient {
//
//    @PostMapping(value = "/api/test/minio/upload/review",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    ResponseEntity<String> uploadReviewImage(@RequestPart("file") Resource file);
//}