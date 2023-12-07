package csw.lms.namsan.nodes.controller.auth;

import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import csw.lms.namsan.nodes.domain.User;
import csw.lms.namsan.nodes.dto.oauth.LoginResponse;
import csw.lms.namsan.nodes.repository.UserRepository;
import csw.lms.namsan.nodes.service.oauth.TokenService;
import csw.lms.namsan.nodes.util.AuthenticatedUser;
import csw.lms.namsan.nodes.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static csw.lms.namsan.nodes.config.JwtTokenFilter.ACCESS_TOKEN_COOKIE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_USER')")
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@AuthenticatedUser User user) {
        return ResponseEntity.of(userRepository.findById(user.getId()));
    }

    @GetMapping("/exchange-token")
    public ResponseEntity<String> exchangeToken(@AuthenticatedUser User user, @RequestParam String token) {
        String longLivedToken = tokenService.exchangeToken(user, token);
        return ResponseEntity.ok(longLivedToken);
    }
}
