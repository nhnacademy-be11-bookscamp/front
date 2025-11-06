package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String rawJwtToken = (String) authentication.getDetails();

        if (rawJwtToken != null && rawJwtToken.startsWith("Bearer ")) {
            String jwtToken = rawJwtToken.substring(7);

            Cookie cookie = new Cookie("Authorization", jwtToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
//            cookie.setSecure(true);
//            cookie.setAttribute("SameSite", "None");
            response.addCookie(cookie);
        }

        new SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);
    }
}