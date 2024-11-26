package pdes.unq.com.APC.interfaces.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductFavoriteRequest {
    public String productExternalId;
    public String userId;

    public ProductFavoriteRequest(){}
}
