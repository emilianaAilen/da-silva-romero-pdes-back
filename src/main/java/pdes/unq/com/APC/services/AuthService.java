package pdes.unq.com.APC.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.exceptions.UserNotFoundException;
import pdes.unq.com.APC.repositories.UserRepository;
import java.util.Map;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  private final JwtService jwtService;

  public AuthService(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  public String login(String email, String password) {
    User user = userRepository.findByEmail(email);

    if (user == null) {
      throw new UserNotFoundException("User with email " + email + " not found.");
    }

    // To do: implementar encriptacion de password
    if (user.getPassword().equals(password)) {
      return jwtService.generateToken(user);
    }

    throw new RuntimeException("Invalid credentials");
  }

  public Map<String, Object> validateToken(String token) {
    try {
      return jwtService.validateToken(token);
    } catch (JwtException e) {
      throw new RuntimeException("Invalid token", e);
    }
  }
}
