package store.bookscamp.front.packaging.controller.response;

import lombok.Data;

@Data
public class PackagingGetResponse {
    private Long id;
    private String name;
    private Integer price;
    private String imageUrl;
}
