package pdes.unq.com.APC.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pdes.unq.com.APC.entities.ProductPurchase;
import java.util.UUID;

public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, UUID> {
}
