package store.bookscamp.front.common.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import store.bookscamp.front.admin.repository.AdminLoginFeignClient;
import store.bookscamp.front.auth.handler.CustomAuthenticationSuccessHandler;
import store.bookscamp.front.auth.provider.AdminAuthenticationProvider;
import store.bookscamp.front.auth.provider.CustomAuthenticationProvider;
import store.bookscamp.front.member.controller.MemberLoginFeignClient;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberLoginFeignClient memberLoginFeignClient;
    private final AdminLoginFeignClient adminLoginFeignClient;

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(memberLoginFeignClient);
    }

    @Bean
    public AdminAuthenticationProvider adminAuthenticationProvider() {
        return new AdminAuthenticationProvider(adminLoginFeignClient);
    }



    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**");

        http.csrf(AbstractHttpConfigurer::disable);

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
        );

        http.httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

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
        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("Authorization")
                .invalidateHttpSession(true)
        );

        http.httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}