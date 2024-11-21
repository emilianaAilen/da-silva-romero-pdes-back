package pdes.unq.com.APC.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import pdes.unq.com.APC.entities.ProductComment;

public interface ProductCommentRepository extends JpaRepository<ProductComment, UUID> {
}