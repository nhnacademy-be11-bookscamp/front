package store.bookscamp.front.packaging.controller.request;

import java.util.List;
import lombok.Data;

@Data
public class PackagingCreateRequest {
    private String name;
    private Integer price;
    private List<String> imageUrl;
}
