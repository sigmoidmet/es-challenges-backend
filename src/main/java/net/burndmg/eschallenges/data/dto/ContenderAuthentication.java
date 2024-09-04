package net.burndmg.eschallenges.data.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

@Getter
public class ContenderAuthentication implements OAuth2User {

    private final Map<String, Object> attributes;
    private final List<SimpleGrantedAuthority> authorities;

    @Builder
    public ContenderAuthentication(String id,
                                   String username,
                                   List<String> authorities) {
       this.attributes = Map.of("id", id,
                                "username", username);

       this.authorities = authorities == null ?
                          List.of() :
                          authorities.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getName() {
        return attributes.get("username").toString();
    }
}
