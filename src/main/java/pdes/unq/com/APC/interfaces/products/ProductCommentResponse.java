package pdes.unq.com.APC.interfaces.products;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductCommentResponse {
  public ProductCommentResponse() {}

  String id;
  String username;
  LocalDateTime createdAt;
  String description;
  Integer likes;
}
