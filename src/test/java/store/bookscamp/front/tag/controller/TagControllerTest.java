package store.bookscamp.front.tag.controller;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.tag.TagFeignClient;
import store.bookscamp.front.tag.controller.request.TagCreateRequest;
import store.bookscamp.front.tag.controller.request.TagUpdateRequest;
import store.bookscamp.front.tag.controller.response.TagGetResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    private MockMvc mvc;

    @Mock
    private TagFeignClient tagFeignClient;

    @InjectMocks
    private TagController tagController;

    private final String BASE_URL = "/admin/tags";
    private final int PAGE_SIZE = 5;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(tagController)
                .build();
    }

    private TagGetResponse createTagResponse(Long id, String name) {
        return new TagGetResponse(id, name);
    }

    @Nested
    @DisplayName("GET /admin/tags")
    class ShowTagPageTest {

        @Test
        @DisplayName("태그 목록 조회 성공 시 페이징 정보와 태그 리스트를 ModelAndView에 담아 반환한다")
        void showTagPage_Success() throws Exception {
            int page = 1;
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            List<TagGetResponse> tags = List.of(
                    createTagResponse(1L, "소설"),
                    createTagResponse(2L, "IT")
            );
            Page<TagGetResponse> mockPage = new PageImpl<>(tags, pageable, 10);

            given(tagFeignClient.getAll(eq(page), eq(PAGE_SIZE)))
                    .willReturn(mockPage);

            mvc.perform(get(BASE_URL)
                            .param("page", String.valueOf(page)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("tags/tag"))
                    .andExpect(model().attributeExists("tags", "currentPage", "totalPages"))
                    .andExpect(model().attribute("tags", tags))
                    .andExpect(model().attribute("currentPage", page))
                    .andExpect(model().attribute("totalPages", 2));

            verify(tagFeignClient).getAll(eq(page), eq(PAGE_SIZE));
        }

        @Test
        @DisplayName("태그 목록이 없을 경우 빈 리스트를 ModelAndView에 담아 반환한다")
        void showTagPage_EmptyList() throws Exception {
            // given
            int page = 0;
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            Page<TagGetResponse> mockPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            given(tagFeignClient.getAll(eq(page), eq(PAGE_SIZE)))
                    .willReturn(mockPage);

            mvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("tags", Collections.emptyList()));
        }
    }

    @Nested
    @DisplayName("POST /admin/tags")
    class CreateTagTest {

        private final String TAG_NAME = "신규 태그";

        @Test
        @DisplayName("태그 생성 요청 성공 시 목록 페이지로 리다이렉트한다")
        void createTag_Success() throws Exception {
            TagGetResponse mockResponse = createTagResponse(10L, TAG_NAME);

            given(tagFeignClient.createTag(any(TagCreateRequest.class)))
                    .willReturn(mockResponse);

            mvc.perform(post(BASE_URL)
                            .param("name", TAG_NAME))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/tags"));

            verify(tagFeignClient).createTag(any(TagCreateRequest.class));        }


        @Test
        @DisplayName("이미 존재하는 태그명으로 생성 요청 시 에러 메시지를 Model에 담고 tags/tag 뷰를 반환한다")
        void createTag_FeignException_Duplicate() throws Exception {
            String errorMessage = "이미 존재하는 태그입니다.";

            feign.Response feignResponse = feign.Response.builder() // <--- 여기를 수정했습니다.
                    .status(HttpStatus.CONFLICT.value()) // 409
                    .reason("Conflict")
                    .request(feign.Request.create(feign.Request.HttpMethod.POST, "/api/tags",
                            Collections.emptyMap(), null, StandardCharsets.UTF_8))
                    .body("Tag already exists", StandardCharsets.UTF_8)
                    .build();

            FeignException mockException = FeignException.errorStatus("POST /api-server/admin/tags", feignResponse);

            given(tagFeignClient.createTag(any(TagCreateRequest.class)))
                    .willThrow(mockException);

            mvc.perform(post(BASE_URL)
                            .param("name", TAG_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("tags/tag"))
                    .andExpect(model().attribute("error", errorMessage));

            verify(tagFeignClient).createTag(any(TagCreateRequest.class));        }


    }

    @Nested
    @DisplayName("POST /admin/tags/{id}")
    class UpdateTagTest {

        private final Long TAG_ID = 1L;
        private final String NEW_NAME = "새로운 이름";

        @Test
        @DisplayName("태그 수정 요청 성공 시 목록 페이지로 리다이렉트한다")
        void updateTag_Success() throws Exception {
            given(tagFeignClient.updateTag(eq(TAG_ID), any(TagUpdateRequest.class)))
                    .willReturn(ResponseEntity.ok().build());

            mvc.perform(post(BASE_URL + "/{id}", TAG_ID)
                            .param("name", NEW_NAME))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/tags"));

            verify(tagFeignClient).updateTag(eq(TAG_ID), any(TagUpdateRequest.class));        }
    }

    @Nested
    @DisplayName("POST /admin/tags/{id}/delete")
    class DeleteTagTest {

        private final Long TAG_ID = 1L;

        @Test
        @DisplayName("태그 삭제 요청 성공 시 목록 페이지로 리다이렉트한다")
        void deleteTag_Success() throws Exception {
            mvc.perform(post(BASE_URL + "/{id}/delete", TAG_ID))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/tags"));

            verify(tagFeignClient).deleteTag(TAG_ID);
        }
    }
}