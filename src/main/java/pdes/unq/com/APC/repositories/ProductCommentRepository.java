package pdes.unq.com.APC.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pdes.unq.com.APC.entities.ProductComment;

public interface ProductCommentRepository extends JpaRepository<ProductComment, UUID> {
    @Query("SELECT pc FROM ProductComment pc WHERE pc.productPurchase.product.id = :productId")
    List<ProductComment> findByProductId(@Param("productId") UUID productId);
}