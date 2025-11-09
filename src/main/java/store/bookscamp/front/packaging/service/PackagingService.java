package store.bookscamp.front.packaging.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.common.service.MinioService;
import store.bookscamp.front.packaging.controller.request.PackagingCreateRequest;
import store.bookscamp.front.packaging.controller.request.PackagingUpdateRequest;
import store.bookscamp.front.packaging.controller.response.PackagingGetResponse;
import store.bookscamp.front.packaging.feign.PackagingFeignClient;

@Service
@RequiredArgsConstructor
public class PackagingService {

    private final MinioService minioService;
    private final PackagingFeignClient packagingFeignClient;

    private List<String> uploadOne(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {return null;}
        files.removeIf(MultipartFile::isEmpty);
        if (files.isEmpty()) {return null;}

        List<String> urls = minioService.uploadFiles(files, "packaging");
        // 리스트로 받지만 실사용은 1장만 하게 됨
        // 넘어온 값이 여러 개면 1장만 사용하도록 함
        return (urls.size() > 1) ? urls.subList(0, 1) : urls;
    }

    public void create(String name, Integer price, List<MultipartFile> files) {
        PackagingCreateRequest request = new PackagingCreateRequest();
        request.setName(name);
        request.setPrice(price);

        List<String> imageUrl = uploadOne(files);
        request.setImageUrl(imageUrl);

        packagingFeignClient.createPackaging(request);
    }

    public PackagingGetResponse get(Long id) {
        return packagingFeignClient.getPackaging(id).getBody();
    }

    public java.util.List<PackagingGetResponse> getAll() {
        return packagingFeignClient.getAll().getBody();
    }

    public void update(Long id, String name, Integer price, List<MultipartFile> files) {
        PackagingUpdateRequest request = new PackagingUpdateRequest();
        request.setName(name);
        request.setPrice(price);

        List<String> imageUrl = uploadOne(files);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            request.setImageUrl(imageUrl);
        }

        packagingFeignClient.updatePackaging(id, request);
    }

    public void delete(Long id) {
        packagingFeignClient.deletePackaging(id);
    }

}
