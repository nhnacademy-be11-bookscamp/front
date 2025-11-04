package store.bookscamp.front.book.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AladinBookResponse {
    private int total;
    private int start;
    private int count;
    private List<Item> items; // ✅ 필드명 통일

    /*@Data
    public static class Item {
        private String title;
        private String author;
        private String publisher;
        private String cover;
    }*/
    @Data
    public static class Item {
        private String title;
        private String author;
        private String publisher;
        @JsonProperty("publishDate")
        private String pubDate;       // yyyy-MM-dd 또는 yyyy-MM
        private String isbn13;        // 상세/식별용 (ItemLookUp 시 핵심)
        private Integer priceStandard;
        @JsonProperty("salePrice")
        private Integer priceSales;
        private Integer regularPrice;
        private String cover;         // 표지 URL
        private String description;   // 설명(=explanation로 매핑)
        private String toc;           // 목차(=content로 매핑)
        private String categoryName;
    }
}
