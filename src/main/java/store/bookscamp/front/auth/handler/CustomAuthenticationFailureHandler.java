package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import store.bookscamp.front.common.exception.ConcurrentLoginException; //

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof ConcurrentLoginException) {
            response.sendRedirect(getRedirectUrl(request, "/login?concurrent"));
        } else {
            response.sendRedirect(getRedirectUrl(request, "/login?error"));
        }
    }

    private String getRedirectUrl(HttpServletRequest request, String defaultUrl) {
        String loginPage = "/login";
        String adminLoginPage = "/admin/login";
        String referer = request.getRequestURI();
        if (referer != null && (referer.contains("/admin/login") || referer.contains("/admin"))) {
            return adminLoginPage + defaultUrl.substring(loginPage.length()); // "/admin/login?concurrent"
        }
        return defaultUrl;
    }
}