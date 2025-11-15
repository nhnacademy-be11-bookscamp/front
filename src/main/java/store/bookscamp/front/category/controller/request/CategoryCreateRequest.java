package store.bookscamp.front.category.controller.request;


public record CategoryCreateRequest(

        Long parentId,
        String name
) {
}
