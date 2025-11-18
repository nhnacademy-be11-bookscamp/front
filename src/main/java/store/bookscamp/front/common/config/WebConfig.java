package store.bookscamp.front.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilterRegistration() {
        FilterRegistrationBean<HiddenHttpMethodFilter> registration =
                new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
        registration.setOrder(Integer.MIN_VALUE + 1);

        return registration;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
