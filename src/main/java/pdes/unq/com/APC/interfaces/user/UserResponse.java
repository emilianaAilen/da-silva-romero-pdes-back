package pdes.unq.com.APC.interfaces.user;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserResponse {
    private String id ;
    private String username;
    private String email;
    private LocalDateTime created_at;
}

