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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.interfaces.auth.LoginRequest;
import pdes.unq.com.APC.interfaces.auth.LoginResponse;
import pdes.unq.com.APC.services.AuthService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Operation(summary = "User login and get auth token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful",
                   content = @Content(mediaType = "application/json",
                                      examples = @ExampleObject(value = "{ \"authToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\" }"))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials",
                   content = @Content(mediaType = "application/json",
                                      examples = @ExampleObject(value = "\"Credenciales inválidas\""))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
                   content = @Content(mediaType = "application/json",
                                      examples = @ExampleObject(value = "{ \"status\": \"Internal_error\", \"message\": \"Error message\" }")))
  })
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

  @Operation(summary = "Get current session from auth token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Session validated successfully",
                   content = @Content(mediaType = "application/json",
                                      examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"email\": \"user@example.com\", \"role\": \"admin\" }"))),
      @ApiResponse(responseCode = "400", description = "Authorization cookie missing",
                   content = @Content(mediaType = "application/json",
                                      examples = @ExampleObject(value = "\"Authorization cookie missing\""))),
      @ApiResponse(responseCode = "401", description = "Invalid token",
                   content = @Content(mediaType = "application/json",
                                      examples = @ExampleObject(value = "\"Invalid token\""))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
                   content = @Content(mediaType = "application/json",
                                      examples = @ExampleObject(value = "{ \"status\": \"Internal_error\", \"message\": \"Error message\" }")))
  })
  @GetMapping("/session")
  public ResponseEntity<?> getCurrentSession(@CookieValue(value = "authToken", required = false) String token) {
    if (token == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization cookie missing");
    }

    try {
      User userDetails = authService.validateToken(token);
      return ResponseEntity.ok(userDetails);
    } catch (JwtException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
  }
}
