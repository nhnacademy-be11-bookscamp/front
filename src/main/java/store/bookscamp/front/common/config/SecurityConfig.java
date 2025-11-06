package store.bookscamp.front.common.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import store.bookscamp.front.auth.handler.CustomAuthenticationSuccessHandler;
import store.bookscamp.front.auth.provider.CustomAuthenticationProvider;
import store.bookscamp.front.member.controller.MemberLoginFeignClient;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberLoginFeignClient memberLoginFeignClient;

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(memberLoginFeignClient);
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/mypage/**").hasRole("USER")
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
                .successHandler(customAuthenticationSuccessHandler())
        );

        http.httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}