package store.bookscamp.front.auth.provider;


import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import store.bookscamp.front.admin.controller.request.AdminLoginRequest;
import store.bookscamp.front.admin.repository.AdminLoginFeignClient;
import store.bookscamp.front.auth.user.CustomAdminDetails;
import store.bookscamp.front.auth.user.CustomMemberDetails;
import store.bookscamp.front.member.controller.MemberLoginFeignClient;
import store.bookscamp.front.member.controller.request.MemberLoginRequest;

@RequiredArgsConstructor
public class AdminAuthenticationProvider implements AuthenticationProvider {

    private final AdminLoginFeignClient adminLoginFeignClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            AdminLoginRequest request = new AdminLoginRequest(username, password);
            var authResponse = adminLoginFeignClient.doLogin(request);

            String rawJwtToken = authResponse.getHeaders().getFirst("Authorization");

            if (rawJwtToken == null || !rawJwtToken.startsWith("Bearer ")) {
                throw new BadCredentialsException("토큰을 찾을 수 없거나 형식이 잘못되었습니다.");
            }

            String jwtToken = rawJwtToken.substring(7);
            var decodedJWT = JWT.decode(jwtToken);

            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();

            CustomAdminDetails customAdminDetails = new CustomAdminDetails(id, username, role,rawJwtToken);

            UsernamePasswordAuthenticationToken result =
                    new UsernamePasswordAuthenticationToken(customAdminDetails, null, customAdminDetails.getAuthorities());

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