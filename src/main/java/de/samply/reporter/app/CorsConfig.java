package de.samply.reporter.app;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] crossOrigins;
    private final int corsMaxAgeInSeconds;

    public CorsConfig(
            @Value(ReporterConst.CROSS_ORIGINS_SV) String[] crossOrigins,
            @Value(ReporterConst.CORS_MAX_AGE_IN_SECONDS_SV) Integer corsMaxAgeInSeconds) {
        this.crossOrigins = crossOrigins;
        this.corsMaxAgeInSeconds = corsMaxAgeInSeconds;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(crossOrigins) // Replace this with the allowed origin(s)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type", "Origin")
                .allowCredentials(true)
                .maxAge(corsMaxAgeInSeconds);
    }
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/reporter/.*")
                .build();
    }

}
