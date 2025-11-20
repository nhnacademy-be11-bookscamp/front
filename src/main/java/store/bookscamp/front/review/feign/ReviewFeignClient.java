package store.bookscamp.front.review.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.pointhistory.controller.response.PageResponse;
import store.bookscamp.front.review.controller.request.ReviewCreateRequest;
import store.bookscamp.front.review.controller.request.ReviewUpdateRequest;
import store.bookscamp.front.review.controller.response.BookReviewResponse;
import store.bookscamp.front.review.controller.response.MyReviewResponse;
import store.bookscamp.front.review.controller.response.ReviewableItemResponse;


import java.util.List;

@FeignClient(
        name = "review",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface ReviewFeignClient {

    // 리뷰 작성 가능한 상품 조회
    @GetMapping("/api-server/member/review/reviewable")
    ResponseEntity<List<ReviewableItemResponse>> getReviewableItems();

    // 내가 작성한 리뷰 조회
    @GetMapping("/api-server/member/review/my")
    ResponseEntity<List<MyReviewResponse>> getMyReviews();

    // 리뷰 수정 페이지
    @GetMapping("/api-server/member/review/{reviewId}")
    ResponseEntity<MyReviewResponse> getUpdateReview(@PathVariable Long reviewId);

    @GetMapping("/api-server/review/book/{bookId}")
    ResponseEntity<PageResponse<BookReviewResponse>> getBookReviews(
            @PathVariable Long bookId,
            @RequestParam int page,
            @RequestParam int size
    );

    @GetMapping("/api-server/review/book/{bookId}/avg")
    ResponseEntity<Double> getBookAverageScore(@PathVariable Long bookId);

    // 리뷰 등록
    @PostMapping("/api-server/member/review")
    ResponseEntity<Void> createReview(@RequestBody ReviewCreateRequest request);

    // 리뷰 수정
    @PutMapping("/api-server/member/review")
    ResponseEntity<Void> updateReview(@RequestBody ReviewUpdateRequest request);
}
