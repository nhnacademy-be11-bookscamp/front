package store.bookscamp.front.book.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import store.bookscamp.front.book.controller.response.BookWishListResponse;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.common.pagination.RestPageImpl;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WishListAdvice {

    private final BookFeignClient bookFeignClient;

    @ModelAttribute("likedCount")
    public long addLikedCountToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return 0L;
        }

        try {
            ResponseEntity<RestPageImpl<BookWishListResponse>> response = bookFeignClient.getWishListBooks();

            if (response != null && response.getBody() != null) {
                return response.getBody().getTotalElements();
            }
        } catch (Exception e) {
            log.error("헤더 위시리스트 카운트 조회 실패", e);
            return 0L;
        }

        return 0L;
    }
}