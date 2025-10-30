package store.bookscamp.front.tag;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.tag.controller.request.TagCreateRequest;
import store.bookscamp.front.tag.controller.request.TagUpdateRequest;
import store.bookscamp.front.tag.controller.response.TagGetResponse;

@FeignClient(name = "tagApiClient", url = "${gateway.base-url}")
public interface TagFeignClient {

    @PostMapping("/api-server/admin/tags")
    TagGetResponse createTag(@RequestBody TagCreateRequest request);

    @GetMapping("/api-server/admin/tags")
    List<TagGetResponse> getAll();

    @PutMapping("/api-server/admin/tags/{id}")
    ResponseEntity<Void> updateTag(@PathVariable("id") Long id,
                                @RequestBody TagUpdateRequest request);

    @DeleteMapping("/api-server/admin/tags/{id}")
    void deleteTag(@PathVariable("id") Long id);
}
