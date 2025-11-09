package store.bookscamp.front.packaging.controller.response;

import lombok.Data;

@Data
public class PackagingGetResponse {
    private Long id;
    private String name;
    private Integer price;
    private String imageUrl; // 백엔드 응답 필드명과 동일하게
}
