package pdes.unq.com.APC.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class ProductPurchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "puntage",nullable = false)
    private int puntage;

    @Column(name = "price_buyed",nullable = false)
    private double priceBuyed;

    @Column(name = "total_buyed",nullable = false)
    private int totalBuyed;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at",nullable = true)
    private LocalDateTime deletedAt;

    public ProductPurchase() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
