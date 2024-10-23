package pdes.unq.com.APC.interfaces.auth;

import lombok.Data;

@Data
public class LoginRequest {
  private String email;
  private String password;
}
