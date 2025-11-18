package store.bookscamp.front.category.entity;


import java.util.ArrayList;
import java.util.List;

public class Category {

    Long id;

    Category parent;

    String name;

    List<Category> children = new ArrayList<>();

}
