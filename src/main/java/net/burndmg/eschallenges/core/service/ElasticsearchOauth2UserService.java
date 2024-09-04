package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.ContenderAuthentication;
import net.burndmg.eschallenges.data.model.Contender;
import net.burndmg.eschallenges.map.ContenderMapper;
import net.burndmg.eschallenges.repository.ContenderRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ElasticsearchOauth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultReactiveOAuth2UserService delegate = new DefaultReactiveOAuth2UserService();

    private final ContenderRepository contenderRepository;
    private final ContenderMapper contenderMapper;

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return delegate.loadUser(userRequest)
                       .flatMap(this::toContenderAuthentication);
    }

    private Mono<ContenderAuthentication> toContenderAuthentication(OAuth2User oauth2User) {
        String id = String.valueOf(oauth2User.<Integer>getAttribute("id"));
        String login = oauth2User.getAttribute("login");

        return contenderRepository.findById(String.valueOf(id), Contender.class)
                                  .switchIfEmpty(createNewContender(id, login))
                                  .map(contenderMapper::toAuthentication);
    }

    private Mono<Contender> createNewContender(String id, String login) {
        Contender contender = Contender.builder()
                                       .id(id)
                                       .username(login)
                                       .build();

        return contenderRepository.saveToReadIndex(contender)
                                  .flatMap(contenderRepository::saveToUpdateIndex);
    }

}
