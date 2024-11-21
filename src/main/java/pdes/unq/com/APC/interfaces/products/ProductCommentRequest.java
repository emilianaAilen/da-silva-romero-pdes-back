package pdes.unq.com.APC.interfaces.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductCommentRequest {
    public String purchaseProductId;
    public String description;
    public Integer likes;

    public ProductCommentRequest() {}
}
