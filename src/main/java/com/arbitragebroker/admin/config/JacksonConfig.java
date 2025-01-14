package com.arbitragebroker.admin.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());

        // Handle Hibernate proxy
//        Hibernate5Module hibernateModule = new Hibernate5Module();
//        hibernateModule.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, false);
//        hibernateModule.configure(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION, true);
//        mapper.registerModule(hibernateModule);

        return mapper;
    }
}
