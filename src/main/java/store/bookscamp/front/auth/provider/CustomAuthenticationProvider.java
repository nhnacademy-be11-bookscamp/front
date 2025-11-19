package store.bookscamp.front.auth.provider;

import com.auth0.jwt.exceptions.JWTDecodeException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.dto.LoginAuthDetails;
import store.bookscamp.front.auth.user.CustomMemberDetails;
import store.bookscamp.front.common.exception.ConcurrentLoginException;
import store.bookscamp.front.member.controller.request.MemberLoginRequest;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthFeignClient authFeignClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            MemberLoginRequest request = new MemberLoginRequest(username, password);

            ResponseEntity<AccessTokenResponse> authResponse = authFeignClient.doLogin(request);

            AccessTokenResponse body = authResponse.getBody();
            if (body == null || body.getAccessToken() == null) {
                throw new BadCredentialsException("Access Token을 받지 못했습니다.");
            }
            String rawJwtToken = "Bearer " + body.getAccessToken();
            String name = body.getName();

            String rtCookieString = authResponse.getHeaders().getFirst("Set-Cookie");
            if (rtCookieString == null) {
                throw new BadCredentialsException("Refresh Token 쿠키를 받지 못했습니다.");
            }

            CustomMemberDetails tempDetails = new CustomMemberDetails("ROLE_USER", rawJwtToken);
            UsernamePasswordAuthenticationToken result =
                    new UsernamePasswordAuthenticationToken(tempDetails, null, tempDetails.getAuthorities());

            result.setDetails(new LoginAuthDetails(rawJwtToken, rtCookieString, name));
            return result;

        } catch (FeignException e) {
            if (e.status() == 401 && e.responseHeaders() != null) {
                boolean isDormant = e.responseHeaders().entrySet().stream()
                        .anyMatch(entry -> entry.getKey().equalsIgnoreCase("x-auth-error-code")
                                && entry.getValue().contains("DORMANT_MEMBER"));

                if (isDormant) {
                    throw new DisabledException("DORMANT_MEMBER");
                }
            }
            if (e.status() == 409) {
                throw new ConcurrentLoginException("이미 사용중인 ID입니다.");
            }
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