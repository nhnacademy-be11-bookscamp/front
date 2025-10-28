package store.bookscamp.front.member.controller.response;

import java.time.LocalDate;

public record MemberGetResponse(
        String username,
        String name,
        String email,
        String phone,
        Integer point,
        LocalDate birthDate
) {
}
