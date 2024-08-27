package pdes.unq.com.APC.interfaces.user;

import lombok.Data;
import java.util.Date;

@Data
public class UserResponse {
    private String id ;
    private Date created ;
    private Date modified ;
    private Date last_login ;
    private String token ;
    private Boolean isactive ;

    
}

