package net.burndmg.eschallenges.infrastructure.config.security;

import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {


    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        // permit here but we will reject everything in customAuthorize
        return http.authorizeExchange(registry -> registry.anyExchange().permitAll())
                   .csrf(ServerHttpSecurity.CsrfSpec::disable)
                   .build();
    }

    @Bean
    CorsConfigurationSource corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedOrigin("*");
        corsConfig.setAllowedMethods(List.of("PUT", "POST", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor denyAllByDefaultAuthorizationManagerAdvisor(DenyAllByDefaultAuthorizationManager authorizationManager) {
        Pointcut pointcut = new AnnotationMatchingPointcut(RestController.class, true);
        return new AuthorizationManagerBeforeMethodInterceptor(pointcut, authorizationManager);
    }
}
