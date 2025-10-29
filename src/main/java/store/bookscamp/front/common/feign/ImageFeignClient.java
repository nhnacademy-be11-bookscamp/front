package store.bookscamp.front.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.common.config.FeignMultipartConfig;

import java.util.List;

@FeignClient(
        name = "image",
        url = "${gateway.base-url}/api-server",
        configuration = FeignMultipartConfig.class
)
public interface ImageFeignClient {

    @PostMapping(
            value = "/admin/book/image", // api서버 컨트롤러 api랑 맞추기
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
        // api서버 컨트롤러 response랑 맞추기 -> List<String> 바꾸기
    ResponseEntity<List<String>> creatBookImage(@RequestPart("files") List<MultipartFile> files);

    @DeleteMapping(
            value = "/admin/book/image", // api서버 컨트롤러 쪽 api랑 맞추기
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
        // api서버 컨트롤러 request랑 맞추기
    ResponseEntity<Void> deleteBookImage();
}
