package store.bookscamp.front.review.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReviewUpdateRequest {

    private Long reviewId;
    private Integer score;
    private String content;
    private List<String> imageUrls;
    private List<String> removedImageUrls;
}
