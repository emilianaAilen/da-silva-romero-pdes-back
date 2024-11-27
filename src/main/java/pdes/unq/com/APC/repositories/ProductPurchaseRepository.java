package pdes.unq.com.APC.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pdes.unq.com.APC.entities.ProductPurchase;
import java.util.List;
import java.util.UUID;

public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, UUID> {
    List<ProductPurchase> findByUserId(UUID userId); 
}
