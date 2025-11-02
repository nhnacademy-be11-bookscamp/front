package store.bookscamp.front.book.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AladinCreateRequest {
    private String title;
    private String contributors;
    private String publisher;
    private String isbn;
    private LocalDate publishDate;
    private Integer regularPrice;
    private Integer salePrice;
    private Integer stock;
    private boolean packable;
    private String content;
    private String explanation;
    private List<String> imgUrls;
    private List<Long> tagIds;
    private List<Long> categoryIds;
}

