package store.bookscamp.front.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.user.CustomAdminDetails;
import store.bookscamp.front.auth.user.CustomMemberDetails;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import store.bookscamp.front.auth.user.TokenDetails;


@Slf4j
@Service
public class TokenRefreshService {

    private final AuthFeignClient authFeignClient;

    public TokenRefreshService(@Lazy AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    private final Object refreshLock = new Object();

    public boolean refreshTokens(String failedAccessToken) {

        synchronized (refreshLock) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentAccessTokenInContext = null;
            if (authentication != null && authentication.getPrincipal() instanceof TokenDetails) {
                currentAccessTokenInContext = ((TokenDetails) authentication.getPrincipal()).getRawJwtToken();
            }

            if (failedAccessToken != null && !failedAccessToken.equals(currentAccessTokenInContext)) {
                log.info("Token already refreshed by another thread. Skipping refresh.");
                return true;
            }

            var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                log.warn("Request attributes not found. Cannot refresh token.");
                return false;
            }
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();

            Cookie rtCookie = findCookie(request, "refresh_token");
            if (rtCookie == null) {
                log.warn("Refresh token cookie not found.");
                return false;
            }

            try {
                log.info("Attempting to refresh token...");
                ResponseEntity<AccessTokenResponse> reissueResponse =
                        authFeignClient.reissue(rtCookie.getValue());

                String newAccessToken = reissueResponse.getBody().getAccessToken();
                String newRawAccessToken = "Bearer " + newAccessToken;
                log.info("New Access Token received.");

                boolean isSecure = request.isSecure();
                if (request.getHeader("x-forwarded-proto") != null) {
                    isSecure = request.getHeader("x-forwarded-proto").equals("https");
                }

                ResponseCookie atCookie = ResponseCookie.from("Authorization", newAccessToken)
                        .path("/")
                        .httpOnly(true)
                        .secure(isSecure)
                        .sameSite(isSecure ? "None" : "Lax")
                        .build();

                response.addHeader("Set-Cookie", atCookie.toString());

                String newRtCookieString = reissueResponse.getHeaders().getFirst("Set-Cookie");
                if (newRtCookieString != null) {
                    response.addHeader("Set-Cookie", newRtCookieString);
                }

                updateSecurityContext(newRawAccessToken, newAccessToken);
                log.info("SecurityContext updated with new token.");

                return true;

            } catch (Exception e) {
                log.error("Token refresh failed: " + e.getMessage());
                return false;
            }
        }
    }


    private void updateSecurityContext(String rawJwtToken, String jwtToken) {
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        String role = decodedJWT.getClaim("role").asString();

        UserDetails userDetails;
        if ("ADMIN".equals(role)) {
            userDetails = new CustomAdminDetails(role, rawJwtToken);
        } else {
            userDetails = new CustomMemberDetails(role, rawJwtToken);
        }

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    private Cookie findCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .orElse(null);
    }
}