package store.bookscamp.front.member.controller.request;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginRequest (
        @NotBlank
        String username,
        @NotBlank
        String password
){
}
