package store.bookscamp.front.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class FeignMultipartConfig {

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignMultipartConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    // LocalDate 직렬화를 위한 ObjectMapper
    @Bean
    public ObjectMapper feignObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate, LocalDateTime 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    // Encoder 등록 (기존 messageConverters + custom ObjectMapper 통합)
    @Bean
    public Encoder feignFormEncoder(ObjectMapper feignObjectMapper) {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(feignObjectMapper);

        HttpMessageConverters converters = new HttpMessageConverters(jacksonConverter);
        SpringEncoder springEncoder = new SpringEncoder(() -> converters);
        return new SpringFormEncoder(springEncoder);
    }
}