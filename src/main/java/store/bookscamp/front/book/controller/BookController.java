package store.bookscamp.front.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.book.feign.BookFeignClient;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookFeignClient bookFeignClient;

    @PostMapping("/image")
    public ResponseEntity<String> uploadBookImage(@RequestParam("file") MultipartFile file) throws IOException {

//        // 멀티파트로 받은 이미지 파일을 바이너리로 받음
//        byte[] bytes = file.getBytes();
//
//        // 리소스로 감싸기
//        MultipartInputStreamFileResource resource =
//                new MultipartInputStreamFileResource(
//                        new ByteArrayInputStream(bytes),
//                        file.getOriginalFilename(),
//                        bytes.length
//                );
//
        return bookFeignClient.uploadBookImage(file);
    }
}
