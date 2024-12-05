package pdes.unq.com.APC.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pdes.unq.com.APC.entities.ProductPurchase;
import java.util.List;
import java.util.UUID;

public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, UUID> {
    List<ProductPurchase> findByUserId(UUID userId); 
    @Query(value = """
            SELECT  p.id, p.name, p.description, p.category, p.price, p.external_item_id, p.url
            FROM product_purchase pp 
            inner join  product p on p.id = pp.product_id
            GROUP BY p.id
            ORDER BY COUNT(pp.product_id) DESC
            LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> finTopPurchasesProducts(@Param("limit") int limit);
}


