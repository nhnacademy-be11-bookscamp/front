package store.bookscamp.front.pointhistory.controller.response;

import store.bookscamp.front.pointhistory.PointType;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long id,
        Long orderId,
        PointType pointType,
        Integer pointAmount,
        LocalDateTime createdAt
) {}
