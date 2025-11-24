package store.bookscamp.front.common.config;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.auth.filter.JwtAuthenticationFilter;
import store.bookscamp.front.auth.handler.CustomAuthenticationFailureHandler;
import store.bookscamp.front.auth.handler.CustomAuthenticationSuccessHandler;
import store.bookscamp.front.auth.handler.CustomOAuthSuccessHandler;
import store.bookscamp.front.auth.provider.AdminAuthenticationProvider;
import store.bookscamp.front.auth.provider.CustomAuthenticationProvider;
import store.bookscamp.front.auth.service.CustomOAuth2UserService;
import store.bookscamp.front.common.exception.CustomAccessDeniedHandler;

@Slf4j
@Configuration
public class SecurityConfig {

    private final AuthFeignClient authFeignClient;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    public SecurityConfig(
                          @Lazy AuthFeignClient authFeignClient,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.authFeignClient = authFeignClient;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(authFeignClient);
    }

    @Bean
    public AdminAuthenticationProvider adminAuthenticationProvider() {
        return new AdminAuthenticationProvider(authFeignClient);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public LogoutHandler customLogoutHandler() {
        return (request, response, authentication) -> {
            Cookie rtCookie = Arrays.stream(request.getCookies())
                    .filter(c -> "refresh_token".equals(c.getName()))
                    .findFirst()
                    .orElse(null);

            if (rtCookie != null) {
                try {
                    authFeignClient.doLogout(rtCookie.getValue());
                } catch (Exception e) {
                    log.error("Failed to logout from Auth server", e);
                }
            }
        };
    }

    @Bean
    @Order(0)
    public SecurityFilterChain staticResourceFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatchers(matchers -> matchers
                        .requestMatchers("/js/**", "/css/**", "/img/**", "/favicon.ico") // 이 경로들은
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 모두 허용
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(SecurityContextConfigurer::disable)
                .sessionManagement(SessionManagementConfigurer::disable);

        return http.build();
    }


    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**");

        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
        );

        http.authenticationProvider(adminAuthenticationProvider());

        http.formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin")
                        .failureUrl("/admin/login?error")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(new CustomAuthenticationSuccessHandler("/admin/dashboard"))
                        .failureHandler(new CustomAuthenticationFailureHandler())
        );

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.exceptionHandling(exception -> exception
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        return http.build();
    }
    @Profile("!test")
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/mypage/**").hasRole("USER")
                        .anyRequest().permitAll()
        );

        http.authenticationProvider(customAuthenticationProvider());

        http.formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/")
            .failureUrl("/login?error")
            .usernameParameter("username")
            .passwordParameter("password")
                .successHandler(new CustomAuthenticationSuccessHandler("/"))
                .failureHandler(new CustomAuthenticationFailureHandler())
        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .addLogoutHandler(customLogoutHandler())
                .deleteCookies("Authorization","refresh_token")
                .invalidateHttpSession(true)
        );
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(new CustomOAuthSuccessHandler("/"))
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                )
        );

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.exceptionHandling(exception -> exception
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        return http.build();
    }
}