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

        // 🚨 1. Principal에서 CustomMemberDetails 객체를 가져옵니다.
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomMemberDetails customMemberDetails) {

            // 2. CustomMemberDetails에 저장된 rawJwtToken을 추출합니다.
            // (CustomMemberDetails에 rawJwtToken 필드가 추가되어야 함)
            String rawJwtToken = customMemberDetails.getRawJwtToken();

            if (rawJwtToken != null && !rawJwtToken.isEmpty()) {

                String finalToken;

                // 3. 토큰이 'Bearer '로 시작하는지 확인하고, 없으면 추가합니다.
                if (rawJwtToken.startsWith("Bearer ")) {
                    finalToken = rawJwtToken;
                } else {
                    finalToken = "Bearer " + rawJwtToken;
                }

                // 4. Authorization 헤더에 최종 토큰을 추가합니다.
                template.header(AUTHORIZATION_HEADER, finalToken);
            }
        }

        // CustomMemberDetails 인스턴스가 아닌 경우 (예: 인증 실패, AnonymousUser)는 무시됩니다.
    }
}