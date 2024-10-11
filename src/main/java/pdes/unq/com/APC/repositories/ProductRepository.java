package pdes.unq.com.APC.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pdes.unq.com.APC.entities.Product;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
