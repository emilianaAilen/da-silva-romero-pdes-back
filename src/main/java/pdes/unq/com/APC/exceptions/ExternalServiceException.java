package pdes.unq.com.APC.exceptions;
import org.springframework.http.HttpStatusCode;
import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {
    private HttpStatusCode status;

    public ExternalServiceException(HttpStatusCode status, String message) {
        super(message);
        this.status = status;
    }

}
