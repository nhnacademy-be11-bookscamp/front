package store.bookscamp.front.common.config;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.util.Collection;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.bookscamp.front.auth.service.TokenRefreshService;

@Slf4j
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final TokenRefreshService tokenRefreshService;
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 401) {
            log.warn("Received 401 Unauthorized for methodKey: {}", methodKey);

            String requestUrl = response.request().url();
            if (requestUrl.contains("/auth-server/admin/login") ||
                    requestUrl.contains("/auth-server/login") ||
                    requestUrl.contains("/auth-server/reissue")) {

                log.error("401 on authentication request. Won't retry. MethodKey: {}", methodKey);
                return defaultDecoder.decode(methodKey, response);
            }

            String failedAccessToken = null;
            Collection<String> authHeaders = response.request().headers().get("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.iterator().next();
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    failedAccessToken = authHeader;
                }
            }

            boolean refreshSuccess = tokenRefreshService.refreshTokens(failedAccessToken);

            if (refreshSuccess) {
                log.info("Token refreshed successfully. Retrying request for methodKey: {}", methodKey);
                return new RetryableException(
                        response.status(),
                        "Token refreshed, retrying request.",
                        response.request().httpMethod(),
                        (Date) null,
                        response.request()
                );
            } else {
                log.error("Token refresh failed. Returning default error. MethodKey: {}", methodKey);
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}