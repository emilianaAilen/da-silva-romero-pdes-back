package pdes.unq.com.APC.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    String status ;
    String message;
}
