package store.bookscamp.front.packaging.controller.request;

import java.util.List;
import lombok.Data;

@Data
public class PackagingUpdateRequest {
    private String name;
    private Integer price;
    private List<String> imageUrl;
}
