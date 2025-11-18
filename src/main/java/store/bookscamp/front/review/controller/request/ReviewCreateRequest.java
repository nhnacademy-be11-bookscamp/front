package store.bookscamp.front.review.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReviewCreateRequest {

    private Long orderItemId;
    private Integer score;
    private String content;
    private List<String> imageUrls;
}
