package csw.lms.namsan.nodes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import csw.lms.namsan.nodes.config.jwt.TokenProvider;
import csw.lms.namsan.nodes.config.security.CustomAuthenticationEntryPoint;
import csw.lms.namsan.nodes.domain.CustomOAuth2User;
import csw.lms.namsan.nodes.domain.User;
import csw.lms.namsan.nodes.dto.oauth.LoginResponse;
import csw.lms.namsan.nodes.service.oauth.CustomOAuth2UserService;
import csw.lms.namsan.nodes.service.oauth.UserDetailService;
import csw.lms.namsan.nodes.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static csw.lms.namsan.nodes.config.JwtTokenFilter.*;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@Slf4j
public class AppSecurityConfiguration {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final UserDetailService userDetailsService;

    @Value("${server.frontend}")
    private String clientUrl;

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(antMatcher("/img/**"))
                .requestMatchers(antMatcher("/css/**"))
                .requestMatchers(antMatcher("/js/**"))
                .requestMatchers(antMatcher("/static/**"))
                // swagger
                .requestMatchers(antMatcher("/v3/api-docs/**"))
                .requestMatchers(antMatcher("/proxy/**"))
                .requestMatchers(antMatcher("/swagger-ui/**"));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://*:8123", "http://*:8080")); // Explicitly set the allowed origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector).servletPath("/path");
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));
        http.anonymous(AbstractHttpConfigurer::disable);
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(c -> c.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
//        http.formLogin(AbstractHttpConfigurer::disable);
//        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers(antMatcher("/")).permitAll()
                .requestMatchers(antMatcher("/login")).permitAll()
                .requestMatchers(antMatcher("/auth/signup")).permitAll()
                .requestMatchers(antMatcher("/oauth/**"), antMatcher("/oauth2/authorization/**")).permitAll()
                .anyRequest().authenticated());

        http.formLogin(f -> f.loginPage("/auth/login").usernameParameter("email").passwordParameter("password").permitAll()
                .successHandler(((request, response, authentication) -> {
                    log.info("Login successful for user: {}", authentication.getName());

                    var user = (User) authentication.getPrincipal();

                    // auth login will make long-lived token at once.
                    var accessToken = tokenProvider.generateToken(user, false);
                    addAccessTokenToCookie(request, response, accessToken);
                    log.info("ACCESS TOKEN: {}", accessToken);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    LoginResponse login = new LoginResponse(accessToken, user);

                    var result = objectMapper.writeValueAsString(login);

                    response.addHeader("Authorization", "Bearer " + accessToken);
                    response.getWriter().write(result);
                })));

        http.oauth2Login(o -> o
//                .loginPage("/oauth/login")
                .successHandler(((request, response, authentication) -> {
                    log.info("Login successful for user: {}", authentication);

                    var user = ((CustomOAuth2User) authentication.getPrincipal()).user();

                    // Generate access token
                    var accessToken = tokenProvider.generateToken(user, true);

                    response.sendRedirect(clientUrl + "/token?token=" + accessToken);
                }))
                .failureHandler((request, response, exception) -> {
                    log.error("Handling failure, redirecting to login page.");

                    // Set the content type of the response to JSON
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpStatus.FORBIDDEN.value());

                    // Create a JSON object with the error message
                    String jsonMessage = "{\"msg\":\"Unauthorized. Please provide correct user info.\"}";

                    // Write the JSON message to the response
                    try {
                        response.getOutputStream().write(jsonMessage.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .userInfoEndpoint(c -> c.userService(customOAuth2UserService)));

        http.logout(l -> l
                .logoutRequestMatcher(antMatcher("/logout"))
                .logoutSuccessHandler(((request, response, authentication) -> {
                    try {
                        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                })));

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtTokenFilter tokenAuthenticationFilter() {
        return new JwtTokenFilter(tokenProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());

        return authProvider;
    }


    private void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        log.debug("Adding access token to cookie.");

        int cookieMaxAge = (int) SHORT_ACCESS_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE);
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE, accessToken, cookieMaxAge);
    }
}
