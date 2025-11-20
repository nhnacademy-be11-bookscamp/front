package store.bookscamp.front.order.dto;

import java.time.LocalDateTime;

public record OrderListResponse(
        Long orderId,                 // 주문 번호
        LocalDateTime orderDate,  // 주문 일시
        String representativeBookTitle, // 첫 번째 책 제목
        int totalQuantity,            // 총 권수
        int finalPaymentAmount
) {
}
