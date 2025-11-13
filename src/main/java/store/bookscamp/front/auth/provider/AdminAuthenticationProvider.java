package store.bookscamp.front.auth.provider;

import com.auth0.jwt.exceptions.JWTDecodeException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import store.bookscamp.front.admin.controller.request.AdminLoginRequest;
import store.bookscamp.front.admin.repository.AdminLoginFeignClient;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.dto.LoginAuthDetails;
import store.bookscamp.front.auth.user.CustomAdminDetails;

@RequiredArgsConstructor
public class AdminAuthenticationProvider implements AuthenticationProvider {

    private final AdminLoginFeignClient adminLoginFeignClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            AdminLoginRequest request = new AdminLoginRequest(username, password);

            ResponseEntity<AccessTokenResponse> authResponse = adminLoginFeignClient.doLogin(request);

            AccessTokenResponse body = authResponse.getBody();
            if (body == null || body.getAccessToken() == null) {
                throw new BadCredentialsException("Access Token을 받지 못했습니다.");
            }
            String rawJwtToken = "Bearer " + body.getAccessToken();

            String rtCookieString = authResponse.getHeaders().getFirst("Set-Cookie");
            if (rtCookieString == null) {
                throw new BadCredentialsException("Refresh Token 쿠키를 받지 못했습니다.");
            }

            CustomAdminDetails tempDetails = new CustomAdminDetails("ROLE_ADMIN", rawJwtToken);
            UsernamePasswordAuthenticationToken result =
                    new UsernamePasswordAuthenticationToken(tempDetails, null, tempDetails.getAuthorities());

            result.setDetails(new LoginAuthDetails(rawJwtToken, rtCookieString));
            return result;

        } catch (FeignException e) {
            throw new BadCredentialsException("로그인에 실패했습니다. (인증 서버 오류)", e);
        } catch (JWTDecodeException e) {
            throw new BadCredentialsException("토큰 디코딩에 실패했습니다. (토큰 위변조 또는 형식 오류)", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}