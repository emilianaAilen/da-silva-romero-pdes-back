package pdes.unq.com.APC.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.exceptions.UserNotFoundException;
import pdes.unq.com.APC.repositories.UserRepository;

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

    throw new RuntimeException("Credenciales inválidas");
  }
}
