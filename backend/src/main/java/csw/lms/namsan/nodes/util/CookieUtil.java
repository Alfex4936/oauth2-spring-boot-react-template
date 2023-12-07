package csw.lms.namsan.nodes.util;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        return cookies == null ? Optional.empty()
                : Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                addCookie(response, name, "/", 0);
            }
        }
    }
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }

    // Production
    public static void addSecureCookie(HttpServletResponse response, String name, String value, int maxAge, boolean isSecure, String sameSite) {
        String cookieValue = String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly%s%s",
                name, value, maxAge,
                isSecure ? "; Secure" : "",
                sameSite != null ? "; SameSite=" + sameSite : "");
        response.addHeader("Set-Cookie", cookieValue);
    }
}

