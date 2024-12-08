package pdes.unq.com.APC.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pdes.unq.com.APC.entities.ProductFavorite;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, UUID> {
    List<ProductFavorite> findByUserId(UUID userId); 
    Optional<ProductFavorite> findByProduct_ExternalItemIDAndUser_Id(String externalItemID, UUID userId);
    
    @Query(value = """
        SELECT p.id, p.name, p.description, p.category, p.price, p.external_item_id, p.url, 
           p.created_at, p.updated_at, p.deleted_at
        FROM product_favorite pf
        INNER JOIN product p ON p.id = pf.product_id
        GROUP BY p.id
        ORDER BY COUNT(pf.user_id) DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findTopFavoriteProducts(@Param("limit") int limit);
}