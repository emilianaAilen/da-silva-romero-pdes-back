package pdes.unq.com.APC.exceptions;

public class ProductPurchaseNotFoundException extends RuntimeException {
    public ProductPurchaseNotFoundException(String message) {
        super(message);
    }
}