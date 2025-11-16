package store.bookscamp.front.category.controller.request;

import store.bookscamp.front.category.entity.Category;

public record CategoryUpdateRequest(

        Category parent,
        String name
) {

}
