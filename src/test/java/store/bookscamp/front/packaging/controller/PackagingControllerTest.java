package store.bookscamp.front.packaging.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.packaging.controller.response.PackagingGetResponse;
import store.bookscamp.front.packaging.service.PackagingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class PackagingControllerTest {

    private MockMvc mvc;

    @Mock
    private PackagingService packagingService;

    @InjectMocks
    private PackagingController packagingController;

    private final String baseUrl = "/admin/packagings";
    private final Long testId = 1L;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(packagingController).build();
    }

    // 변경됨: 파라미터 제거
    private PackagingGetResponse createResponse() {
        return new PackagingGetResponse();
    }

    @Nested
    @DisplayName("GET " + baseUrl)
    class ShowListTest {

        @Test
        @DisplayName("포장재 목록 조회 성공 시 리스트를 Model에 담아 반환한다")
        void showList_Success() throws Exception {
            // given
            List<PackagingGetResponse> mockList = List.of(
                    createResponse(), // 변경됨: 인자 제거
                    createResponse()  // 변경됨: 인자 제거
            );
            given(packagingService.getAll()).willReturn(mockList);

            mvc.perform(get(baseUrl))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/packagings/packagings"))
                    .andExpect(model().attribute("packagings", mockList));

            verify(packagingService).getAll();
        }
    }

    @Nested
    @DisplayName("GET " + baseUrl + "/{id}")
    class ShowDetailTest {

        @Test
        @DisplayName("포장재 상세 조회 성공 시 객체를 Model에 담아 반환한다")
        void showDetail_Success() throws Exception {
            // given
            PackagingGetResponse mockResponse = createResponse(); // 변경됨: 인자 제거
            given(packagingService.get(testId)).willReturn(mockResponse);

            mvc.perform(get(baseUrl + "/{id}", testId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/packagings/detail"))
                    .andExpect(model().attribute("packaging", mockResponse));

            verify(packagingService).get(testId);
        }
    }

    @Nested
    @DisplayName("POST " + baseUrl)
    class CreatePackagingTest {

        private final String name = "새 포장재";
        private final int price = 1000;

        @Test
        @DisplayName("이미지 파일과 함께 포장재 생성 성공 시 목록 페이지로 리다이렉트한다")
        void createPackaging_WithFile_Success() throws Exception {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "files",
                    "test.jpg",
                    "image/jpeg",
                    "file content".getBytes()
            );

            mvc.perform(multipart(baseUrl)
                            .file(file)
                            .param("name", name)
                            .param("price", String.valueOf(price))
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(baseUrl));

            verify(packagingService).create(eq(name), eq(price), anyList());
        }

        @Test
        @DisplayName("파일 없이 포장재 생성 성공 시 목록 페이지로 리다이렉트한다")
        void createPackaging_NoFile_Success() throws Exception {
            mvc.perform(multipart(baseUrl)
                            .param("name", name)
                            .param("price", String.valueOf(price))
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(baseUrl));

            verify(packagingService).create(eq(name), eq(price), any());
        }
    }

    @Test
    @DisplayName("GET " + baseUrl + "/create: 포장재 생성 폼 페이지를 반환한다")
    void showCreatePackaging_Success() throws Exception {
        mvc.perform(get(baseUrl + "/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/packagings/form"));
    }

    @Nested
    @DisplayName("GET " + baseUrl + "/{id}/update")
    class ShowUpdateTest {

        @Test
        @DisplayName("포장재 수정 폼 조회 성공 시 기존 데이터를 Model에 담아 반환한다")
        void showUpdate_Success() throws Exception {
            // given
            PackagingGetResponse mockResponse = createResponse(); // 변경됨: 인자 제거
            given(packagingService.get(testId)).willReturn(mockResponse);

            // when & then
            mvc.perform(get(baseUrl + "/{id}/update", testId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/packagings/form"))
                    .andExpect(model().attribute("packaging", mockResponse));

            verify(packagingService).get(testId);
        }
    }

    @Nested
    @DisplayName("PUT " + baseUrl + "/{id}/update")
    class UpdatePackagingTest {

        private final String name = "수정된 이름";
        private final int price = 2000;

        @Test
        @DisplayName("이미지 교체 없이 포장재 수정 성공 시 상세 페이지로 리다이렉트한다")
        void updatePackaging_NoFile_Success() throws Exception {
            mvc.perform(multipart(baseUrl + "/{id}/update", testId)
                            .param("name", name)
                            .param("price", String.valueOf(price))
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(baseUrl + "/" + testId));

            verify(packagingService).update(eq(testId), eq(name), eq(price), any());
        }

        @Test
        @DisplayName("이미지 파일 교체와 함께 포장재 수정 성공 시 상세 페이지로 리다이렉트한다")
        void updatePackaging_WithFile_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "files",
                    "new_image.png",
                    "image/png",
                    "new file content".getBytes()
            );

            mvc.perform(multipart(baseUrl + "/{id}/update", testId)
                            .file(file)
                            .param("name", name)
                            .param("price", String.valueOf(price))
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(baseUrl + "/" + testId));

            verify(packagingService).update(eq(testId), eq(name), eq(price), anyList());
        }
    }

    @Nested
    @DisplayName("POST " + baseUrl + "/{id}/delete")
    class DeletePackagingTest {

        @Test
        @DisplayName("포장재 삭제 성공 시 204 No Content를 반환한다")
        void deletePackaging_Success() throws Exception {
            mvc.perform(post(baseUrl + "/{id}/delete", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(packagingService).delete(testId);
        }
    }
}