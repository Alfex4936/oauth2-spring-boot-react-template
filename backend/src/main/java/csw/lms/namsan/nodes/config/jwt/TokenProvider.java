package csw.lms.namsan.nodes.config.jwt;


import csw.lms.namsan.nodes.domain.User;
import csw.lms.namsan.nodes.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Date;

import static csw.lms.namsan.nodes.config.JwtTokenFilter.LONG_ACCESS_TOKEN_DURATION;
import static csw.lms.namsan.nodes.config.JwtTokenFilter.SHORT_ACCESS_TOKEN_DURATION;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final String SHORT_LIVED_ISSUER = "shortLivedIssuer";
    private final Long ACCEPTABLE_TOKEN_EXPIRATION = (long) (24 * 60 * 60 * 1000); // 1 day in milliseconds

    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    public String generateToken(User user, boolean isShortLived) {
        var now = new Date();
        var duration = isShortLived ? SHORT_ACCESS_TOKEN_DURATION : LONG_ACCESS_TOKEN_DURATION;
        return makeToken(new Date(now.getTime() + duration.toMillis()), user, isShortLived);
    }

    private String makeToken(Date expiry, User user, boolean isShortLived) {
        var now = new Date();
        String issuer = isShortLived ? SHORT_LIVED_ISSUER : jwtProperties.getIssuer();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("provider", user.getProvider())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validTokenIgnoringExpiration(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);

            // Check if the token is expired more than the acceptable duration
            Date expiration = claims.getBody().getExpiration();
            long expiredDuration = new Date().getTime() - expiration.getTime();
            return expiredDuration <= ACCEPTABLE_TOKEN_EXPIRATION; // Token is expired beyond acceptable duration
        } catch (ExpiredJwtException e) {
            // Token is expired but within the acceptable duration
            return true;
        } catch (Exception e) {
            // Any other exceptions mean the token is invalid
            return false;
        }
    }

    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        Claims claims = getClaims(token);
        var email = claims.getSubject();
        var provider = claims.get("provider", String.class);

        var user = userRepository.findByProviderAndEmail(provider, email).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email + " and provider: " + provider)
        );

        return new OAuth2AuthenticationToken(user, user.getAuthorities(), provider);
//        return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
    }

    public String getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", String.class);
    }

    public String getProvider(String token) {
        Claims claims = getClaims(token);
        return claims.get("provider", String.class);
    }

    public boolean isShortLivedToken(String token) {
        return getClaims(token).getIssuer().equals(SHORT_LIVED_ISSUER);
    }

    public String getUserEmail(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private static class FixedClock implements Clock {
        private final Date date;

        public FixedClock(Date date) {
            this.date = date;
        }

        @Override
        public Date now() {
            return date; // Always return the fixed past time
        }
    }
}
