package finalproject.ss3v2.security;

import finalproject.ss3v2.dao.request.SignInRequest;
import finalproject.ss3v2.dao.request.SignUpRequest;
import finalproject.ss3v2.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);
    JwtAuthenticationResponse signin(SignInRequest request);
}
