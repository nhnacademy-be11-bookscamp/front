package store.bookscamp.front.book.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateRequest {
    private String title;
    private String contributors;
    private String publisher;
    private String isbn;
    private LocalDate publishDate;
    private Integer regularPrice;
    private Integer salePrice;
    private String content;
    private String explanation;
    /*private List<String> imageUrls;
    private List<Long> categoryIds;
    private List<String> tagNames;*/
}

