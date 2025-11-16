package store.bookscamp.front.common.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.bookscamp.front.auth.service.TokenRefreshService;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor feignClientRequestInterceptor() {
        return new FeignClientRequestInterceptor();
    }

    @Bean
    public RequestInterceptor cartTokenCookieInterceptor() {
        return new CartTokenCookieInterceptor();
    }

    @Bean
    public ErrorDecoder feignErrorDecoder(TokenRefreshService tokenRefreshService) {
        return new FeignErrorDecoder(tokenRefreshService);
    }
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default();
    }
}