package finalproject.ss3v2.util;

import jakarta.servlet.http.Cookie;


public class CookieUtils {//This class is used to create cookies

    public static Cookie createAccessTokenCookie(String value) {
        Cookie accessTokenCookie = new Cookie("accessToken", value);

        return accessTokenCookie;
    }

    public static Cookie createRefreshTokenCookie(String value) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", value);

        return refreshTokenCookie;
    }
}
