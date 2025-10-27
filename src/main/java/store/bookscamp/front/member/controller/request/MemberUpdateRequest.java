package store.bookscamp.front.member.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(
        @NotBlank
        String name,
        @NotBlank @Email
        String email,
        @NotBlank
        String phone
) {
}
