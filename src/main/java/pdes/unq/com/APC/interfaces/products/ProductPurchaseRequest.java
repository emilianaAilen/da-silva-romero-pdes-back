package pdes.unq.com.APC.interfaces.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductPurchaseRequest {
    public String productID;
    public String userID;
    public Integer cantStockBuyed;
    public Integer priceBuyed;
    public Integer puntage;

    public ProductPurchaseRequest() {}
}
