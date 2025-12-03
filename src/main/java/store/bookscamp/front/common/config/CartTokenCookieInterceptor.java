package store.bookscamp.front.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CartTokenCookieInterceptor implements RequestInterceptor {

    private static final String CART_TOKEN_COOKIE = "cartToken";

    @Override
    public void apply(RequestTemplate requestTemplate) {

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;

        for (Cookie cookie : cookies) {
            if (CART_TOKEN_COOKIE.equals(cookie.getName())) {
                requestTemplate.header("Cookie", CART_TOKEN_COOKIE + "=" + cookie.getValue());
                break;
            }
        }
    }
}
