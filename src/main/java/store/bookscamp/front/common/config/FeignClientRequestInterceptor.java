package store.bookscamp.front.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import store.bookscamp.front.auth.user.CustomMemberDetails; // CustomMemberDetails import 필요

@Component
public class FeignClientRequestInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomMemberDetails customMemberDetails) {

            String rawJwtToken = customMemberDetails.getRawJwtToken();

            if (rawJwtToken != null && !rawJwtToken.isEmpty()) {

                String finalToken;

                if (rawJwtToken.startsWith("Bearer ")) {
                    finalToken = rawJwtToken;
                } else {
                    finalToken = "Bearer " + rawJwtToken;
                }

                template.header(AUTHORIZATION_HEADER, finalToken);
            }
        }
    }
}