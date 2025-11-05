package store.bookscamp.front.auth.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import feign.FeignException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import store.bookscamp.front.auth.user.CustomMemberDetails;
import store.bookscamp.front.member.controller.MemberLoginFeignClient;
import store.bookscamp.front.member.controller.request.MemberLoginRequest;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final MemberLoginFeignClient memberLoginFeignClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            MemberLoginRequest request = new MemberLoginRequest(username, password);
            var authResponse = memberLoginFeignClient.doLogin(request);

            String rawJwtToken = authResponse.getHeaders().getFirst("Authorization");

            if (rawJwtToken == null || !rawJwtToken.startsWith("Bearer ")) {
                throw new BadCredentialsException("토큰을 찾을 수 없거나 형식이 잘못되었습니다.");
            }

            String jwtToken = rawJwtToken.substring(7);
            var decodedJWT = JWT.decode(jwtToken);

            Long memberId = decodedJWT.getClaim("memberId").asLong();
            String role = decodedJWT.getClaim("role").asString();

            CustomMemberDetails customUserDetails = new CustomMemberDetails(memberId, username, role,rawJwtToken);

            UsernamePasswordAuthenticationToken result =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            result.setDetails(rawJwtToken);
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