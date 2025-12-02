package store.bookscamp.front.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.common.service.MinioService;
import store.bookscamp.front.review.controller.request.ReviewCreateRequest;
import store.bookscamp.front.review.controller.request.ReviewUpdateRequest;
import store.bookscamp.front.review.controller.response.MyReviewResponse;
import store.bookscamp.front.review.controller.response.ReviewableItemResponse;
import store.bookscamp.front.review.feign.ReviewFeignClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewFeignClient reviewFeignClient;
    private final MinioService minioService;

    @GetMapping("/mypage/reviews")
    public String getAllReviewPages(Model model) {

        List<ReviewableItemResponse> reviewable = reviewFeignClient.getReviewableItems().getBody();
        List<MyReviewResponse> written = reviewFeignClient.getMyReviews().getBody();

        model.addAttribute("reviewable", reviewable);
        model.addAttribute("written", written);

        return "review/my-reviews";
    }

    // 리뷰 등록 페이지
    @GetMapping("/mypage/reviews/new/{orderItemId}")
    public String getCreatePage(@PathVariable Long orderItemId, Model model) {

        model.addAttribute("orderItemId", orderItemId);
        return "review/create";
    }

    // 리뷰 수정 페이지
    @GetMapping("/mypage/reviews/{reviewId}")
    public String getUpdatePage(@PathVariable Long reviewId, Model model) {

        MyReviewResponse review = reviewFeignClient.getUpdateReview(reviewId).getBody();
        model.addAttribute("review", review);
        return "review/update";
    }

    // 리뷰 등록
    @PostMapping("/mypage/reviews")
    public String createReview(
            @ModelAttribute ReviewCreateRequest req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        if (files != null) {
            files.removeIf(MultipartFile::isEmpty);
        }

        if (files != null && !files.isEmpty()) {
            req.setImageUrls(minioService.uploadFiles(files, "review"));
        }

        reviewFeignClient.createReview(req);
        return "redirect:/mypage/reviews";
    }

    // 리뷰 수정
    @PutMapping("/mypage/reviews")
    public String updateReview(
            @ModelAttribute ReviewUpdateRequest req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        if (files != null) {
            files.removeIf(MultipartFile::isEmpty);
        }

        if (files != null && !files.isEmpty()) {
            req.setImageUrls(minioService.uploadFiles(files, "review"));
        }

        List<String> removedUrls = req.getRemovedImageUrls();
        if (removedUrls != null && !removedUrls.isEmpty()) {
            for (String url : removedUrls) {
                minioService.deleteFile(url, "review");
            }
        }

        reviewFeignClient.updateReview(req);
        return "redirect:/mypage/reviews";
    }
}
