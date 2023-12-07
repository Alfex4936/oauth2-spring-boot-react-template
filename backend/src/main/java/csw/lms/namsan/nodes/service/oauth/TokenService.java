package csw.lms.namsan.nodes.service.oauth;

import csw.lms.namsan.nodes.config.jwt.TokenProvider;
import csw.lms.namsan.nodes.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;

    public String exchangeToken(User user, String token) {
        // Check if the token is a short-lived token
        if (tokenProvider.isShortLivedToken(token)) {
            // For short-lived tokens, they should not be expired
            if (!tokenProvider.validToken(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired or invalid short-lived token.");
            }
        } else {
            // For long-lived tokens, it's okay if they are expired
            if (!tokenProvider.validTokenIgnoringExpiration(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid long-lived token.");
            }
        }

        // Check user ID regardless of token type
        if (!tokenProvider.getUserId(token).equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user id doesn't match.");
        }

        // Generate a new long-lived token
        return tokenProvider.generateToken(user, false);
    }
}
