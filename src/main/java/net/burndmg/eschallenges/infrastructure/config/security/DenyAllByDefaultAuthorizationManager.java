package net.burndmg.eschallenges.infrastructure.config.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.function.Supplier;


@Component
public class DenyAllByDefaultAuthorizationManager implements AuthorizationManager<MethodInvocation> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (containsSecurityAnnotations(method) || containsSecurityAnnotations(method.getDeclaringClass())) {
            return null;
        }

        return new AuthorizationDecision(false);
    }

    private static boolean containsSecurityAnnotations(AnnotatedElement source) {
        MergedAnnotations annotations = MergedAnnotations.from(source, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

        return annotations.get(PreAuthorize.class).isPresent() || annotations.get(PostAuthorize.class).isPresent();
    }
}
