package store.bookscamp.front.review.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import store.bookscamp.front.common.service.MinioService;
import store.bookscamp.front.review.controller.request.ReviewCreateRequest;
import store.bookscamp.front.review.controller.request.ReviewUpdateRequest;
import store.bookscamp.front.review.controller.response.MyReviewResponse;
import store.bookscamp.front.review.controller.response.ReviewableItemResponse;
import store.bookscamp.front.review.feign.ReviewFeignClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    MockMvc mvc;

    @Mock
    ReviewFeignClient reviewFeignClient;

    @Mock
    MinioService minioService;

    @InjectMocks
    ReviewController reviewController;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(reviewController)
                .build();
    }

    @Test
    @DisplayName("[GET] 리뷰 목록 페이지 조회 성공")
    void getAllReviews_success() throws Exception {

        when(reviewFeignClient.getReviewableItems())
                .thenReturn(ResponseEntity.ok(List.of(
                        new ReviewableItemResponse(1L, 2L, "책", "thumb")
                )));

        when(reviewFeignClient.getMyReviews())
                .thenReturn(ResponseEntity.ok(List.of()));

        mvc.perform(get("/mypage/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("review/my-reviews"));
    }

    @Test
    @DisplayName("[GET] 리뷰 작성 페이지 조회 성공")
    void getCreatePage_success() throws Exception {
        mvc.perform(get("/mypage/reviews/new/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(view().name("review/create"))
                .andExpect(model().attributeExists("orderItemId"));
    }

    @Test
    @DisplayName("[GET] 리뷰 수정 페이지 조회 성공")
    void getUpdatePage_success() throws Exception {

        MyReviewResponse mockReview = new MyReviewResponse(
                1L, 3L, "책제목", "thumb",
                5, "내용",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(reviewFeignClient.getUpdateReview(1L))
                .thenReturn(ResponseEntity.ok(mockReview));

        mvc.perform(get("/mypage/reviews/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("review"))
                .andExpect(view().name("review/update"));
    }

    @Test
    @DisplayName("[POST] 리뷰 등록 성공 - 이미지 포함")
    void createReview_with_files_success() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "files", "test.png", "image/png", "fakeimg".getBytes()
        );

        when(minioService.uploadFiles(anyList(), eq("review")))
                .thenReturn(List.of("uploaded-url"));

        mvc.perform(multipart("/mypage/reviews")
                        .file(file)
                        .param("orderItemId", "10")
                        .param("score", "5")
                        .param("content", "좋아요"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/reviews"));

        verify(reviewFeignClient).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("[PUT] 리뷰 수정 성공 - 이미지 추가 + 삭제")
    void updateReview_success() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "files", "new.png", "image/png", "img".getBytes()
        );

        when(minioService.uploadFiles(anyList(), eq("review")))
                .thenReturn(List.of("uploaded2"));

        mvc.perform(multipart("/mypage/reviews")
                        .file(file)
                        .with(req -> { req.setMethod("PUT"); return req; })
                        .param("reviewId", "1")
                        .param("score", "3")
                        .param("content", "수정함")
                        .param("removedImageUrls", "remove1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/reviews"));

        verify(minioService).deleteFile("remove1", "review");
        verify(reviewFeignClient).updateReview(any(ReviewUpdateRequest.class));
    }
}
