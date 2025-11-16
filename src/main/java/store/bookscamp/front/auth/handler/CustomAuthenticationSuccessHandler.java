package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import store.bookscamp.front.auth.dto.LoginAuthDetails;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SavedRequestAwareAuthenticationSuccessHandler delegate =
            new SavedRequestAwareAuthenticationSuccessHandler();

    public CustomAuthenticationSuccessHandler(String defaultTargetUrl) {
        this.delegate.setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        LoginAuthDetails loginDetails = (LoginAuthDetails) authentication.getDetails();

        String rawJwtToken = loginDetails.getRawAccessToken();
        if (rawJwtToken != null && rawJwtToken.startsWith("Bearer ")) {
            String jwtToken = rawJwtToken.substring(7);

            Cookie cookie = new Cookie("Authorization", jwtToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        String rtCookieString = loginDetails.getRtCookieString();
        if (rtCookieString != null) {
            response.addHeader("Set-Cookie", rtCookieString);
        }

        String name = loginDetails.getName();
        if (name != null) {
            Cookie nameCookie = new Cookie("member_name", URLEncoder.encode(name, "UTF-8"));
            nameCookie.setPath("/");
            nameCookie.setHttpOnly(false);
            nameCookie.setMaxAge(60 * 30);

            response.addCookie(nameCookie);
        }

        this.delegate.onAuthenticationSuccess(request, response, authentication);
    }

}