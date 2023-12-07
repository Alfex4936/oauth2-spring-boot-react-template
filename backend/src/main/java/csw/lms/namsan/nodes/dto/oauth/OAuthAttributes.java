package csw.lms.namsan.nodes.dto.oauth;

import csw.lms.namsan.nodes.domain.User;
import csw.lms.namsan.nodes.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Builder
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String provider;
    private final String email;
    private final String picture;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes, Environment env) {
        log.info("OF: {}", attributes);

        String providerPrefix = registrationId.toLowerCase() + ".";
        String name = getValue(attributes, env.getProperty(providerPrefix + "name"));
        String email = getValue(attributes, env.getProperty(providerPrefix + "email"));
        String picture = getValue(attributes, env.getProperty(providerPrefix + "picture"));
        return OAuthAttributes.builder()
                .provider(registrationId)
                .name(name)
                .email(email)
                .picture(picture)
                .attributes(new HashMap<>(attributes))
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static String getValue(Map<String, Object> attributes, String path) {
        if (path == null || path.isEmpty()) return null;
        String[] pathComponents = path.split("\\.");
        Map<String, Object> currentMap = attributes;
        for (int i = 0; i < pathComponents.length - 1; i++) {
            currentMap = (Map<String, Object>) currentMap.get(pathComponents[i]);
        }
        return (String) currentMap.get(pathComponents[pathComponents.length - 1]);
    }

    public User toEntity() {
        return User.builder().attributes(attributes).name(name).provider(provider).email(email).picture(picture).userRole(UserRole.USER.getKey()).build();
    }
}