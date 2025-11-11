package store.bookscamp.front.packaging.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.packaging.controller.request.PackagingCreateRequest;
import store.bookscamp.front.packaging.controller.request.PackagingUpdateRequest;
import store.bookscamp.front.packaging.controller.response.PackagingGetResponse;

@FeignClient(name = "packaging", url = "${gateway.base-url}/api-server")
public interface PackagingFeignClient {

    @PostMapping("/admin/packagings/create")
    ResponseEntity<String> createPackaging(@RequestBody PackagingCreateRequest request);

    @GetMapping("/admin/packagings/{id}")
    ResponseEntity<PackagingGetResponse> getPackaging(@PathVariable("id") Long id);

    @GetMapping("/admin/packagings")
    ResponseEntity<List<PackagingGetResponse>> getAll();

    @PutMapping("/admin/packagings/{id}/update")
    ResponseEntity<String> updatePackaging(@PathVariable("id") Long id,
                                           @RequestBody PackagingUpdateRequest request);

    @DeleteMapping("/admin/packagings/{id}/delete")
    ResponseEntity<String> deletePackaging(@PathVariable("id") Long id);
}
