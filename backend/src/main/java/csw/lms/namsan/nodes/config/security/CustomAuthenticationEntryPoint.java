package csw.lms.namsan.nodes.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a JSON object with the error message
        String jsonMessage = "{\"msg\":\"Unauthorized. Please provide correct user info.\"}";
        try {
            response.getOutputStream().write(jsonMessage.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
