package csw.lms.namsan.nodes.service.auth;

import csw.lms.namsan.nodes.domain.User;
import csw.lms.namsan.nodes.domain.UserRole;
import csw.lms.namsan.nodes.dto.auth.SignUpRequest;
import csw.lms.namsan.nodes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${server.default.user}")
    private String defaultUserProfilePic;

    public User signUp(SignUpRequest request) {
        // 이메일 중복 체크
        checkDuplicatedUser(request.email());

        var user = User.builder()
                .email(request.email())
                .userRole(UserRole.USER.getKey())
                .provider("website")
                .picture(defaultUserProfilePic)
                .build();
        user.setPassword(bCryptPasswordEncoder.encode(request.password()));

        return userRepository.save(user);
    }

    private void checkDuplicatedUser(String email) {
        userRepository.findByProviderAndEmail("website", email).ifPresent(
                u -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is in used.");
                }
        );
    }
}
