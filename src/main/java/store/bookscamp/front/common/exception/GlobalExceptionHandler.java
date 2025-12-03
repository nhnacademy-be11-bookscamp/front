package store.bookscamp.front.common.exception;

import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public String handleFeignException(FeignException ex, Model model) {
        int statusCode = ex.status();
        String errorMessage = ex.contentUTF8();

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);

        return "error/error";
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public String handleTokenExpired(RefreshTokenExpiredException ex) {
        log.warn("리프레시 토큰 만료 -> 스프링 시큐리티 로그아웃 체인으로 리다이렉트");

        return "redirect:/logout";
    }
}