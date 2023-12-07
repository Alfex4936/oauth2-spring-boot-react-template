package csw.lms.namsan.nodes.controller.auth;

import csw.lms.namsan.nodes.dto.auth.SignUpRequest;
import csw.lms.namsan.nodes.dto.auth.SignUpResponse;
import csw.lms.namsan.nodes.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        var user = userService.signUp(request);

        var response = new SignUpResponse(user);

        return ResponseEntity.ok(response);
    }
}
