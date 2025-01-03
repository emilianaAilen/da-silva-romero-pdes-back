package pdes.unq.com.APC.services;

import pdes.unq.com.APC.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private static final long EXPIRATION_TIME = 43200000;

  public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getEmail())
        .addClaims(Map.of(
            "name", user.getUsername(),
            "role", user.getRoleType()))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SECRET_KEY)
        .compact();
  }

  public Map<String, Object> validateToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(SECRET_KEY)
        .build()
        .parseClaimsJws(token)
        .getBody();

    // Retornamos los claims como un mapa
    return Map.of(
        "email", claims.getSubject(),
        "name", claims.get("name"),
        "role", claims.get("role"));
  }
}
