package store.bookscamp.front.tag.controller.request;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateRequest {
    @NotNull
    @Size(max = 255)
    String name;
}
