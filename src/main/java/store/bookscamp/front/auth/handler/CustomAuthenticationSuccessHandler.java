package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

        boolean isSecure = request.isSecure();

        if (request.getHeader("x-forwarded-proto") != null) {
            isSecure = request.getHeader("x-forwarded-proto").equals("https");
        }

        String rawJwtToken = loginDetails.getRawAccessToken();
        if (rawJwtToken != null && rawJwtToken.startsWith("Bearer ")) {
            String jwtToken = rawJwtToken.substring(7);

            ResponseCookie atCookie = ResponseCookie.from("Authorization", jwtToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(isSecure)
                    .sameSite(isSecure ? "None" : "Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, atCookie.toString());
        }

        String rtCookieString = loginDetails.getRtCookieString();
        if (rtCookieString != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, rtCookieString);
        }

        String name = loginDetails.getName();
        if (name != null) {
            ResponseCookie nameCookie = ResponseCookie.from("member_name", URLEncoder.encode(name, "UTF-8"))
                    .path("/")
                    .httpOnly(false)
                    .secure(isSecure)
                    .sameSite(isSecure ? "None" : "Lax")
                    .maxAge(60 * 30)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, nameCookie.toString());
        }

        this.delegate.onAuthenticationSuccess(request, response, authentication);
    }
}