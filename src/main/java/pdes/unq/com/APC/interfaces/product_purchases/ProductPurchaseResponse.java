package pdes.unq.com.APC.interfaces.product_purchases;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductPurchaseResponse {
    String id;
    String user_id;
    Integer puntage;
    double price_buyed;
    double total_buyed;
    ProductResponse product;

    public ProductPurchaseResponse(){}    
}

