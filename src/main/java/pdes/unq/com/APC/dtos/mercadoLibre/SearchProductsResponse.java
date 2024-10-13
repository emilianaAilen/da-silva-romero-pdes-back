package pdes.unq.com.APC.dtos.mercadoLibre;

import java.util.List;
import lombok.Data;

@Data
public class SearchProductsResponse {
    private String site_id;
    private String country_default_time_zone;
    private Paging paging;
    private List<Result> results;

    // Clase anidada para "Paging"
    @Data
    public static class Paging {
        private int total;
        private int primary_results;
        private int offset;
        private int limit;
    }

    // Clase anidada para "Result"
    @Data
    public static class Result {
        private String id;
        private String title;
        private String condition;
        private String thumbnail_id;
        private String catalog_product_id;
        private String listing_type_id;
        private String permalink;
        private String buying_mode;
        private String site_id;
        private String category_id;
        private String domain_id;
        private String thumbnail;
        private String currency_id;
        private int price;
        private double original_price;
        private SalePrice sale_price;
        private int available_quantity;
        private long official_store_id;
        private String official_store_name;
        private boolean use_thumbnail_id;
        private boolean accepts_mercadopago;
        private Shipping shipping;
        private String stop_time;
        private Seller seller;
        private List<Attribute> attributes;
        private Installments installments;
        private String inventory_id;

        // Clase para "SalePrice"
        @Data
        public static class SalePrice {
            private String price_id;
            private double amount;
            private Conditions conditions;
            private String currency_id;
            private double regular_amount;
            private String type;
            private Metadata metadata;

            // Clase para "Conditions"
            @Data
            public static class Conditions {
                private boolean eligible;
                private List<String> context_restrictions;
                private String start_time;
                private String end_time;
            }

            // Clase para "Metadata"
            @Data
            public static class Metadata {
                private String promotion_type;
                private String promotion_id;

            }
        }

        // Clase para "Shipping"
        @Data
        public static class Shipping {
            private boolean store_pick_up;
            private boolean free_shipping;
            private String logistic_type;
            private String mode;
            private List<String> tags;
        }

        @Data
        public static class Seller {
            private long id;
            private String nickname;
        }

        @Data
        public static class Attribute {
            private String id;
            private String name;
            private String value_id;
            private String value_name;
            private ValueStruct value_struct;

            // Clase para "ValueStruct"
            @Data
            public static class ValueStruct {
                private double number;
                private String unit;
            }
        }

        // Clase para "Installments
        @Data
        public static class Installments {
            private int quantity;
            private double amount;
            private double rate;
            private String currency_id;
            private Metadata metadata;

            // Clase para "Metadata"
            @Data
            public static class Metadata {
                private boolean meliplus_installments;
                private boolean additional_bank_interest;
            }
        }
    }
}
