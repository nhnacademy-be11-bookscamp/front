package store.bookscamp.front.auth.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import store.bookscamp.front.auth.user.CustomAdminDetails;
import store.bookscamp.front.auth.user.CustomMemberDetails;
import store.bookscamp.front.auth.user.TokenDetails;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie authCookie = findCookie(request, "Authorization");
        if (authCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = authCookie.getValue();
        String rawJwtToken = "Bearer " + jwtToken;

        try {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            String role = decodedJWT.getClaim("role").asString();

            TokenDetails userDetails;
            if ("ADMIN".equals(role)) {
                userDetails = new CustomAdminDetails(
                        role,
                        rawJwtToken
                );
            } else {
                userDetails = new CustomMemberDetails(
                        role,
                        rawJwtToken
                );
            }

            Collection<? extends GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JWTDecodeException e) {
            logger.warn("JWT Decode failed (UI Rendering): " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error in JwtAuthenticationFilter: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private Cookie findCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .orElse(null);
    }
}