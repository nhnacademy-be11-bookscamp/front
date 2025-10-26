package store.bookscamp.front.book.controller.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookDetailResponse {
    private String title;
    private String author;
    private String publisher;
    private String publishDate;
    private String isbn13;
    private Integer regularPrice;
    private Integer salePrice;
    private String cover;
    private String categoryName;
    private String explanation;
    private String content;
}
