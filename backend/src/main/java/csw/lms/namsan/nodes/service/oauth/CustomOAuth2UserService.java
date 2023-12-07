package csw.lms.namsan.nodes.service.oauth;

import csw.lms.namsan.nodes.domain.CustomOAuth2User;
import csw.lms.namsan.nodes.domain.User;
import csw.lms.namsan.nodes.dto.oauth.OAuthAttributes;
import csw.lms.namsan.nodes.dto.oauth.SessionUser;
import csw.lms.namsan.nodes.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final Environment env; // Injected environment

    @Override
    public CustomOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        var userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        var attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes(), env);
        var user = saveOrUpdate(attributes);
//
//        httpSession.setAttribute("user", new SessionUser(user));

//        return new DefaultOAuth2User(user.getAuthorities(), attributes.getAttributes(), attributes.getNameAttributeKey());
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        var user = userRepository.findByProviderAndEmail(attributes.getProvider(), attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}