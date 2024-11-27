package pdes.unq.com.APC.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import pdes.unq.com.APC.entities.ProductFavorite;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, UUID> {
    List<ProductFavorite> findByUserId(UUID userId); 
    Optional<ProductFavorite> findByProduct_ExternalItemIDAndUser_Id(String externalItemID, UUID userId);
}