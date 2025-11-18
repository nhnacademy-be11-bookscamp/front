package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import store.bookscamp.front.auth.dto.LoginAuthDetails;

@Slf4j
public class CustomOAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final SavedRequestAwareAuthenticationSuccessHandler delegate =
            new SavedRequestAwareAuthenticationSuccessHandler();

    public CustomOAuthSuccessHandler(String defaultTargetUrl) {
        this.delegate.setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        boolean isGuest = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GUEST"));

        if (isGuest) {
            response.sendRedirect("/signup/social");
        } else {
            try {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                Map<String, Object> attributes = oauth2User.getAttributes();

                String rawAccessToken = (String) attributes.get("rawAccessToken");
                String rtCookieString = (String) attributes.get("rtCookieString");
                String name = (String) attributes.get("name");

                if (rawAccessToken == null || rtCookieString == null) {
                    throw new IllegalStateException("OAuth2 attributes에 JWT/Cookie 정보가 없습니다.");
                }
                boolean isSecure = request.isSecure();
                if (request.getHeader("x-forwarded-proto") != null) {
                    isSecure = request.getHeader("x-forwarded-proto").equals("https");
                }
                addCookie(response, "Authorization", rawAccessToken.substring(7), isSecure, true, -1);
                response.addHeader(HttpHeaders.SET_COOKIE, rtCookieString);
                addCookie(response, "member_name", URLEncoder.encode(name, "UTF-8"), isSecure, false, 60 * 30);

            } catch (Exception e) {
                log.error("OAuth2 로그인 성공 후 쿠키 설정 실패", e);
            }

            this.delegate.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, boolean isSecure, boolean httpOnly, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(httpOnly)
                .secure(isSecure)
                .sameSite(isSecure ? "None" : "Lax")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}