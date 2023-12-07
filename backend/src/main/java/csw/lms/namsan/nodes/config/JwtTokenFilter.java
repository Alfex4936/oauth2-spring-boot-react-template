package csw.lms.namsan.nodes.config;

import csw.lms.namsan.nodes.config.jwt.TokenProvider;
import csw.lms.namsan.nodes.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration SHORT_ACCESS_TOKEN_DURATION = Duration.ofMinutes(5);
    public static final Duration LONG_ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    public final static String HEADER_AUTHORIZATION = "Authorization";

    public final static String NEW_HEADER_AUTHORIZATION = "New-Access-Token";
    public final static String NEW_XRT_AUTHORIZATION = "New-Refresh-Token";
    public final static String ACCESS_TOKEN_COOKIE = "Access-Token";
    public final static String TOKEN_PREFIX = "Bearer ";

    private static final Pattern UNPROTECTED_API =
            Pattern.compile("^/(login|auth/signup|auth/login)(/.*)?$");
    private static final Pattern PROTECTED_API =
            Pattern.compile("^/(logout|user)(/.*)?$");


    private final TokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isPublicRoute(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);
        boolean isShortLived = tokenProvider.isShortLivedToken(token);
        boolean isValid = isShortLived ? tokenProvider.validToken(token) : tokenProvider.validTokenIgnoringExpiration(token);

        if (isValid) {
            try {
                Authentication auth = tokenProvider.getAuthentication(token);
                log.debug("AUTH IS : {}", auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (UsernameNotFoundException e) {
                handleAuthenticationException(request, response, e);
                return;
            }

            // Check for expiration in case of long-lived token
            if (!isShortLived && !tokenProvider.validToken(token)) {
                response.setHeader("Token-Expired", "true");
                // Optionally, you can set a specific status code here if needed
            }
        } else {
            // Token is not valid - send 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return; // Stop further filter processing
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(String uri) {
        return UNPROTECTED_API.matcher(uri).matches();
    }

    private boolean isProtectedRoute(String uri) {
        return PROTECTED_API.matcher(uri).matches();
    }

    private String getAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }

        // Also check for token in cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response,
                                               UsernameNotFoundException e) throws IOException {
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: " + e.getLocalizedMessage());
    }
}
