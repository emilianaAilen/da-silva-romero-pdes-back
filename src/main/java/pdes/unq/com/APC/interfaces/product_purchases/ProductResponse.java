package pdes.unq.com.APC.interfaces.product_purchases;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
    String id;
    String name;
    String description;
    String category;
    String price;
    String external_item_id;
    String url_image;

   public ProductResponse() {}
}