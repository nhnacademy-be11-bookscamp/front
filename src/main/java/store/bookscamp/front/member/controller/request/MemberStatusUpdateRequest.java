package store.bookscamp.front.member.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberStatusUpdateRequest(
        @NotNull
        Long memberId,

        @NotBlank
        String status
) {
}
