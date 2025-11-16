package store.bookscamp.front.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import store.bookscamp.front.auth.user.TokenDetails;

public class FeignClientRequestInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {

        String requestPath = template.path();

        if (requestPath.contains("/auth-server/admin/login") ||
                requestPath.contains("/auth-server/login") ||
                requestPath.contains("/auth-server/reissue")) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof TokenDetails tokenDetails) {
            String rawJwtToken = tokenDetails.getRawJwtToken();
            if (rawJwtToken != null && !rawJwtToken.isEmpty()) {
                template.header(AUTHORIZATION_HEADER);
                template.header(AUTHORIZATION_HEADER, rawJwtToken);

            }
        }
    }
}