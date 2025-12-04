package store.bookscamp.front.packaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.packaging.service.PackagingService;

@RestController
@RequiredArgsConstructor
public class PackagingRestController {

    private final PackagingService packagingService;

    @PostMapping("/admin/packagings/{id}/delete")
    public ResponseEntity<Void> deletePackaging(@PathVariable Long id) {
        packagingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
