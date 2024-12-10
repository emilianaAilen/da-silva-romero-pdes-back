package pdes.unq.com.APC.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pdes.unq.com.APC.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Métodos personalizados de consulta pueden ser añadidos aquí
    User findByUsername(String username);
    User findByEmail(String email);
        @Query(value = """   
                SELECT um.id,um.username, um.email, um.created_at,  count(pp.id) as cant_purchase_products
                FROM user_manager um 
                INNER JOIN product_purchase pp ON pp.user_id = um.id 
                GROUP BY um.id 
                ORDER BY count(pp.id) DESC 
                LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findTopUsersWithMostPurchasesProducts(@Param("limit") int limit);

}
