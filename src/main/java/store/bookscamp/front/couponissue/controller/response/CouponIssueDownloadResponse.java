package store.bookscamp.front.couponissue.controller.response;

public record CouponIssueDownloadResponse(
        Long couponId,
        String name,
        String discountInfo
) {
}