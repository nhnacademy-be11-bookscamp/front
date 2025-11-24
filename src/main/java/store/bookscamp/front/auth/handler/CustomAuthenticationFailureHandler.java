package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import store.bookscamp.front.common.exception.ConcurrentLoginException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private boolean isDisabledException(Throwable exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof DisabledException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");

        String defaultUrl = getRedirectUrl(request, "/login");

        boolean isDormant = isDisabledException(exception);

        if (exception instanceof ConcurrentLoginException) {
            response.sendRedirect(defaultUrl + "?concurrent");

        } else if (isDormant) {

            String encodedUsername = "";
            if (username != null) {
                encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            }

            log.info("휴면 계정 로그인 시도 감지: {}, 해제 페이지로 리다이렉트", username);
            response.sendRedirect("/dormant?username=" + encodedUsername);

        } else {
            response.sendRedirect(defaultUrl + "?error=true");
        }
    }

    private String getRedirectUrl(HttpServletRequest request, String defaultUrl) {
        String loginPage = "/login";
        String adminLoginPage = "/admin/login";
        String referer = request.getRequestURI();
        if (referer != null && (referer.contains("/admin/login") || referer.contains("/admin"))) {
            return adminLoginPage + defaultUrl.substring(loginPage.length());
        }
        return defaultUrl;
    }
}