package store.bookscamp.front.book.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.common.config.FeignMultipartConfig;

@FeignClient(
        name = "bookFeignClient",
        url = "http://localhost:8080/api-server",
        configuration = FeignMultipartConfig.class
)

public interface BookFeignClient {

    @PostMapping(
            value = "/api/test/minio/upload/book",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )

    ResponseEntity<String> uploadBookImage(@RequestParam("file") MultipartFile file);

}
