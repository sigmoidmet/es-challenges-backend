package net.burndmg.eschallenges.infrastructure.config.security;

import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.Customizer;
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        // permit /api here, but we will reject everything in customAuthorize
        return http.authorizeExchange(customizer -> customizer.pathMatchers("/login/**").permitAll()
                                                              .pathMatchers("/api/**").permitAll()
                                                              .anyExchange().denyAll())
                   .csrf(Customizer.withDefaults())
                   .oauth2Login(Customizer.withDefaults())
                   .build();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
