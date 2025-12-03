package store.bookscamp.front.packaging.serivce;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.common.service.MinioService;
import store.bookscamp.front.packaging.controller.request.PackagingCreateRequest;
import store.bookscamp.front.packaging.controller.request.PackagingUpdateRequest;
import store.bookscamp.front.packaging.controller.response.PackagingGetResponse;
import store.bookscamp.front.packaging.feign.PackagingFeignClient;

import java.util.List;
import store.bookscamp.front.packaging.service.PackagingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PackagingServiceTest {

    @InjectMocks
    private PackagingService packagingService;

    @Mock
    private MinioService minioService;

    @Mock
    private PackagingFeignClient packagingFeignClient;

    private final Long testId = 1L; // testUrl
    private final String testName = "테스트 포장지";
    private final int testPrice = 1000;
    private final String testUrl = "http://minio.url/test.jpg";

    private MockMultipartFile createMockFile(String filename, String content) {
        return new MockMultipartFile("file", filename, "image/jpeg", content.getBytes());
    }

    @Nested
    @DisplayName("포장재 생성 (create)")
    class CreateTest {

        @Test
        @DisplayName("파일 1개와 함께 생성 요청 시, MinioService 호출 후 Feign Client를 호출한다")
        void create_WithOneFile_Success() {
            MockMultipartFile file = createMockFile("p1.jpg", "content");
            List<MultipartFile> files = new java.util.ArrayList<>(List.of(file));
            given(minioService.uploadFiles(anyList(), eq("package"))).willReturn(List.of(testUrl));

            given(packagingFeignClient.createPackaging(any(PackagingCreateRequest.class)))
                    .willReturn(ResponseEntity.ok("OK"));

            packagingService.create(testName, testPrice, files);

            verify(minioService).uploadFiles(files, "package");

            verify(packagingFeignClient).createPackaging(
                    argThat(req -> req.getName().equals(testName) &&
                            req.getPrice().equals(testPrice) &&
                            req.getImageUrl().get(0).equals(testUrl)));
        }

        @Test
        void create_NoFile_Success() {
            // given
            String name = "테스트 포장지";
            Integer price = 1000;
            List<MultipartFile> files = null;

            // when
            packagingService.create(name, price, files);

            // then
            ArgumentCaptor<PackagingCreateRequest> requestCaptor = ArgumentCaptor.forClass(PackagingCreateRequest.class);

            verify(packagingFeignClient).createPackaging(requestCaptor.capture());

            PackagingCreateRequest actualRequest = requestCaptor.getValue();

            assertThat(actualRequest.getName()).isEqualTo(name);
            assertThat(actualRequest.getPrice()).isEqualTo(price);

            assertThat(actualRequest.getImageUrl()).isNotNull().isEmpty();
        }
    }

    @Nested
    @DisplayName("포장재 조회 (get/getAll)")
    class GetTest {

        private final PackagingGetResponse mockResponse = new PackagingGetResponse();

        @Test
        @DisplayName("단건 조회 성공 시 응답 DTO를 반환한다")
        void get_Success() {
            given(packagingFeignClient.getPackaging(testId))
                    .willReturn(ResponseEntity.ok(mockResponse));

            PackagingGetResponse result = packagingService.get(testId);

            assertThat(result).isEqualTo(mockResponse);
            verify(packagingFeignClient).getPackaging(testId);
        }

        @Test
        @DisplayName("전체 목록 조회 성공 시 리스트를 반환한다")
        void getAll_Success() {
            List<PackagingGetResponse> mockList = List.of(mockResponse);
            given(packagingFeignClient.getAll())
                    .willReturn(ResponseEntity.ok(mockList));

            List<PackagingGetResponse> result = packagingService.getAll();

            assertThat(result).isEqualTo(mockList);
            verify(packagingFeignClient).getAll();
        }
    }

    @Nested
    @DisplayName("포장재 수정 (update)")
    class UpdateTest {

        @Test
        @DisplayName("이미지 파일 교체와 함께 수정 요청 시, MinioService 호출 후 Feign Client를 호출한다")
        void update_WithNewFile_Success() {
            MockMultipartFile file = createMockFile("p_new.jpg", "new_content");
            List<MultipartFile> files = new java.util.ArrayList<>(List.of(file));
            given(minioService.uploadFiles(anyList(), eq("package"))).willReturn(List.of(testUrl));

            given(packagingFeignClient.updatePackaging(eq(testId), any(PackagingUpdateRequest.class)))
                    .willReturn(ResponseEntity.ok("OK"));

            packagingService.update(testId, testName, testPrice, files);

            verify(minioService).uploadFiles(files, "package");

            verify(packagingFeignClient).updatePackaging(eq(testId),
                    argThat(req -> req.getName().equals(testName) && req.getImageUrl().get(0).equals(testUrl)));
        }

        @Test
        @DisplayName("이미지 교체 없이 수정 요청 시, MinioService 호출 없이 Feign Client를 호출한다")
        void update_NoFile_Success() {
            List<MultipartFile> files = null;

            given(packagingFeignClient.updatePackaging(eq(testId), any(PackagingUpdateRequest.class)))
                    .willReturn(ResponseEntity.ok("OK"));

            packagingService.update(testId, testName, testPrice, files);

            verify(minioService, org.mockito.Mockito.never()).uploadFiles(any(), any());

            verify(packagingFeignClient).updatePackaging(eq(testId),
                    argThat(req -> req.getName().equals(testName) && req.getImageUrl() == null));
        }
    }

    @Test
    @DisplayName("포장재 삭제 (delete): Feign Client를 호출하여 삭제를 요청한다")
    void delete_Success() {
        given(packagingFeignClient.deletePackaging(testId))
                .willReturn(ResponseEntity.ok("OK"));

        packagingService.delete(testId);

        verify(packagingFeignClient).deletePackaging(testId);
    }

    @Test
    @DisplayName("uploadOne 로직: 여러 파일 전달 시 첫 번째 URL만 반환한다 (Branch)")
    void uploadOne_MultipleFiles_ReturnsOne() {
        MockMultipartFile file1 = createMockFile("p1.jpg", "c1");
        MockMultipartFile file2 = createMockFile("p2.jpg", "c2");
        List<MultipartFile> files = new java.util.ArrayList<>(List.of(file1, file2));

        given(minioService.uploadFiles(anyList(), anyString())).willReturn(List.of(testUrl, "http://other.url/p2.jpg"));
        packagingService.update(testId, testName, testPrice, files);

        verify(packagingFeignClient).updatePackaging(eq(testId),
                argThat(req -> req.getImageUrl() != null && req.getImageUrl().size() == 1 && req.getImageUrl().get(0).equals(testUrl)));
    }

    @Test
    @DisplayName("uploadOne 로직: 빈 파일 제거 후 리스트가 비면 null을 반환한다 (Branch)")
    void uploadOne_OnlyEmptyFiles_ReturnsNull() {
        MultipartFile emptyFile = org.mockito.Mockito.mock(MultipartFile.class);
        given(emptyFile.isEmpty()).willReturn(true);

        List<MultipartFile> files = new java.util.ArrayList<>(List.of(emptyFile));

        packagingService.update(testId, testName, testPrice, files);

        verify(minioService, org.mockito.Mockito.never()).uploadFiles(any(), any());

        verify(packagingFeignClient).updatePackaging(eq(testId),
                argThat(req -> req.getImageUrl() == null));
    }
}
