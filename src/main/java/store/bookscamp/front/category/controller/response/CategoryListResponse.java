package store.bookscamp.front.category.controller.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CategoryListResponse(

        Long id,
        String name,
        List<CategoryListResponse> children
) {

    @JsonCreator
    public CategoryListResponse(@JsonProperty("id") Long id,
                                @JsonProperty("name") String name,
                                @JsonProperty("children") List<CategoryListResponse> children) {
        this.id = id;
        this.name = name;
        this.children = (children != null) ? children : List.of();
    }
}