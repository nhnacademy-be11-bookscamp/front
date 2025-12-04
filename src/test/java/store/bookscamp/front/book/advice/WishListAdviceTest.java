package store.bookscamp.front.book.advice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import store.bookscamp.front.book.controller.response.BookWishListResponse;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.common.pagination.RestPageImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishListAdviceTest {

    @Mock
    private BookFeignClient bookFeignClient;

    @InjectMocks
    private WishListAdvice wishListAdvice;

    @Test
    @DisplayName("비로그인 사용자(UserDetails가 null)일 경우 0을 반환하고 Feign을 호출하지 않는다")
    void addLikedCountToModel_WhenUserIsNull_ReturnsZero() {
        // when
        long result = wishListAdvice.addLikedCountToModel(null);

        // then
        assertThat(result).isZero();
        verifyNoInteractions(bookFeignClient);
    }

    @Test
    @DisplayName("로그인 사용자: Feign 호출 성공 시 위시리스트의 총 개수(TotalElements)를 반환한다")
    void addLikedCountToModel_WhenSuccess_ReturnsTotalElements() {
        // given
        UserDetails mockUser = mock(UserDetails.class);
        RestPageImpl<BookWishListResponse> mockPage = mock(RestPageImpl.class);
        long expectedCount = 5L;

        given(bookFeignClient.getWishListBooks()).willReturn(ResponseEntity.ok(mockPage));
        given(mockPage.getTotalElements()).willReturn(expectedCount);

        // when
        long result = wishListAdvice.addLikedCountToModel(mockUser);

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(bookFeignClient).getWishListBooks();
    }

    @Test
    @DisplayName("로그인 사용자: Feign 응답 Body가 null일 경우 0을 반환한다")
    void addLikedCountToModel_WhenBodyIsNull_ReturnsZero() {
        // given
        UserDetails mockUser = mock(UserDetails.class);

        given(bookFeignClient.getWishListBooks()).willReturn(ResponseEntity.ok(null));

        // when
        long result = wishListAdvice.addLikedCountToModel(mockUser);

        // then
        assertThat(result).isZero();
        verify(bookFeignClient).getWishListBooks();
    }

    @Test
    @DisplayName("로그인 사용자: Feign 호출 중 예외 발생 시 로그를 남기고 0을 반환한다")
    void addLikedCountToModel_WhenExceptionThrown_ReturnsZero() {
        // given
        UserDetails mockUser = mock(UserDetails.class);

        given(bookFeignClient.getWishListBooks()).willThrow(new RuntimeException("Feign Connection Error"));

        // when
        long result = wishListAdvice.addLikedCountToModel(mockUser);

        // then
        assertThat(result).isZero();
        verify(bookFeignClient).getWishListBooks();
    }
}