package pdes.unq.com.APC.dtos.mercadoLibre;

import lombok.Data;

@Data
public class Item {
    private String id;
    private String siteId;
    private String title;
    private long seller_Id;
    private String category_id;
    private Integer officialStoreId;
    private double price;
    private double basePrice;
    private double originalPrice;
    private String currencyId;
    private String thumbnail;
}
