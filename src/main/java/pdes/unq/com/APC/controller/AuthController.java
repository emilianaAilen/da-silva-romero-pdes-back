package pdes.unq.com.APC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import pdes.unq.com.APC.interfaces.auth.LoginRequest;
import pdes.unq.com.APC.interfaces.auth.LoginResponse;
import pdes.unq.com.APC.services.AuthService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    try {
      String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

      Cookie authCookie = new Cookie("authToken", token);
      // authCookie.setSecure(true); Descomentar en prod
      authCookie.setPath("/");
      authCookie.setMaxAge(12 * 60 * 60);
      response.addCookie(authCookie);

      return ResponseEntity.ok(new LoginResponse(token));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }
  }

  @GetMapping("/session")
  public ResponseEntity<?> getCurrentSession(@CookieValue(value = "authToken", required = false) String token) {
    if (token == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization cookie missing");
    }

    try {
      Map<String, Object> userDetails = authService.validateToken(token);
      return ResponseEntity.ok(userDetails);
    } catch (JwtException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
  }
}
