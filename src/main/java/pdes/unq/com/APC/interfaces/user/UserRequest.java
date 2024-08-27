package pdes.unq.com.APC.interfaces.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {
    private String name;
    private String password;
    private String email;

}
