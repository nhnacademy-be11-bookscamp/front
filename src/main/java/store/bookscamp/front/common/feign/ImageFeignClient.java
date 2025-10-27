package store.bookscamp.front.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.common.config.FeignMultipartConfig;

import java.util.List;

@FeignClient(
        name = "image",
        url = "http://localhost:8080/api-server",
        configuration = FeignMultipartConfig.class
)
public interface ImageFeignClient {

    @PostMapping(
            value = "/api/{type}", // api서버 컨트롤러 쪽 api랑 맞추기
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<List<String>> uploadBookImages(@PathVariable("type") String type,
                                                  @RequestPart("files") List<MultipartFile> files);
}
