package pdes.unq.com.APC.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import pdes.unq.com.APC.entities.ProductFavorite;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, UUID> {
}