package csw.lms.namsan.nodes.dto.oauth;

import csw.lms.namsan.nodes.domain.User;

public record LoginResponse(String accessToken, User user) {
}