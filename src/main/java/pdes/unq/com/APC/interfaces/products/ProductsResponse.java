package pdes.unq.com.APC.interfaces.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductsResponse {

    String id;
    String tittle;
    String meliLink;
    String imageLink;
    Integer price;
    String currency;
    String condition;
}
