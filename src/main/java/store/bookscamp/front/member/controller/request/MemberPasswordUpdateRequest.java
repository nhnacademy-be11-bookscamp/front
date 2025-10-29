package store.bookscamp.front.member.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberPasswordUpdateRequest(
        @NotNull
        @Size(min = 8, max = 20)
        String password
){
}
