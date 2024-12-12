package pdes.unq.com.APC.interfaces.auth;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionResponse {
    String id;
    String roleType;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String username;
    String email;

    public SessionResponse(){}  
}
