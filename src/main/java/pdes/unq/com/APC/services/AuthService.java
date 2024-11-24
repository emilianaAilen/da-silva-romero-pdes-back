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

  public User validateToken(String token) {
    try {
      Map<String, Object> claims = jwtService.validateToken(token);
      String email = (String) claims.get("email"); // O el campo que uses en el token
      User user = userRepository.findByEmail(email);

      if (user == null) {
          throw new UserNotFoundException("User not found for token");
      }

      return user; // Devuelve el usuario autenticado
    } catch (JwtException e) {
      throw new RuntimeException("Invalid token", e);
    }
  }
}
