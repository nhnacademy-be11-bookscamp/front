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

    private final String BASE_URL = "/admin/packagings";
    private final Long TEST_ID = 1L;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(packagingController).build();
    }

    private PackagingGetResponse createResponse(Long id, String name) {
        return new PackagingGetResponse();
    }
    @Nested
    @DisplayName("GET " + BASE_URL)
    class ShowListTest {

        @Test
        @DisplayName("포장재 목록 조회 성공 시 리스트를 Model에 담아 반환한다")
        void showList_Success() throws Exception {
            // given
            List<PackagingGetResponse> mockList = List.of(
                    createResponse(1L, "일반 포장"),
                    createResponse(2L, "고급 포장")
            );
            given(packagingService.getAll()).willReturn(mockList);

            mvc.perform(get(BASE_URL))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/packagings/packagings"))
                    .andExpect(model().attribute("packagings", mockList));

            verify(packagingService).getAll();
        }
    }

    @Nested
    @DisplayName("GET " + BASE_URL + "/{id}")
    class ShowDetailTest {

        @Test
        @DisplayName("포장재 상세 조회 성공 시 객체를 Model에 담아 반환한다")
        void showDetail_Success() throws Exception {
            // given
            PackagingGetResponse mockResponse = createResponse(TEST_ID, "일반 포장");
            given(packagingService.get(eq(TEST_ID))).willReturn(mockResponse);

            mvc.perform(get(BASE_URL + "/{id}", TEST_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/packagings/detail"))
                    .andExpect(model().attribute("packaging", mockResponse));

            verify(packagingService).get(eq(TEST_ID));
        }
    }

    @Nested
    @DisplayName("POST " + BASE_URL)
    class CreatePackagingTest {

        private final String NAME = "새 포장재";
        private final int PRICE = 1000;

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

            mvc.perform(multipart(BASE_URL)
                            .file(file)
                            .param("name", NAME)
                            .param("price", String.valueOf(PRICE))
                            .with(request -> { // POST 요청임을 명시적으로 지정
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(BASE_URL));

            verify(packagingService).create(eq(NAME), eq(PRICE), anyList());
        }

        @Test
        @DisplayName("파일 없이 포장재 생성 성공 시 목록 페이지로 리다이렉트한다")
        void createPackaging_NoFile_Success() throws Exception {
            mvc.perform(multipart(BASE_URL)
                            .param("name", NAME)
                            .param("price", String.valueOf(PRICE))
                            .with(request -> { // POST 요청임을 명시적으로 지정
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(BASE_URL));

            verify(packagingService).create(eq(NAME), eq(PRICE), any());
        }
    }

    @Test
    @DisplayName("GET " + BASE_URL + "/create: 포장재 생성 폼 페이지를 반환한다")
    void showCreatePackaging_Success() throws Exception {
        mvc.perform(get(BASE_URL + "/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/packagings/form"));
    }

    @Nested
    @DisplayName("GET " + BASE_URL + "/{id}/update")
    class ShowUpdateTest {

        @Test
        @DisplayName("포장재 수정 폼 조회 성공 시 기존 데이터를 Model에 담아 반환한다")
        void showUpdate_Success() throws Exception {
            // given
            PackagingGetResponse mockResponse = createResponse(TEST_ID, "수정 대상");
            given(packagingService.get(eq(TEST_ID))).willReturn(mockResponse);

            // when & then
            mvc.perform(get(BASE_URL + "/{id}/update", TEST_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/packagings/form"))
                    .andExpect(model().attribute("packaging", mockResponse));

            verify(packagingService).get(eq(TEST_ID));
        }
    }

    @Nested
    @DisplayName("PUT " + BASE_URL + "/{id}/update")
    class UpdatePackagingTest {

        private final String NAME = "수정된 이름";
        private final int PRICE = 2000;

        @Test
        @DisplayName("이미지 교체 없이 포장재 수정 성공 시 상세 페이지로 리다이렉트한다")
        void updatePackaging_NoFile_Success() throws Exception {
            mvc.perform(multipart(BASE_URL + "/{id}/update", TEST_ID)
                            .param("name", NAME)
                            .param("price", String.valueOf(PRICE))
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(BASE_URL + "/" + TEST_ID));

            verify(packagingService).update(eq(TEST_ID), eq(NAME), eq(PRICE), any());
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

            mvc.perform(multipart(BASE_URL + "/{id}/update", TEST_ID)
                            .file(file)
                            .param("name", NAME)
                            .param("price", String.valueOf(PRICE))
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(BASE_URL + "/" + TEST_ID));

            // service.update 메서드가 정확히 호출되었는지 검증
            verify(packagingService).update(eq(TEST_ID), eq(NAME), eq(PRICE), anyList());
        }
    }

    @Nested
    @DisplayName("POST " + BASE_URL + "/{id}/delete")
    class DeletePackagingTest {

        @Test
        @DisplayName("포장재 삭제 성공 시 204 No Content를 반환한다")
        void deletePackaging_Success() throws Exception {
            mvc.perform(post(BASE_URL + "/{id}/delete", TEST_ID)
                            .contentType(MediaType.APPLICATION_JSON)) // @ResponseBody로 ResponseEntity를 반환하므로 JSON/NoContent 기대
                    .andDo(print())
                    .andExpect(status().isNoContent()); // 204 No Content

            verify(packagingService).delete(eq(TEST_ID));
        }
    }
}
