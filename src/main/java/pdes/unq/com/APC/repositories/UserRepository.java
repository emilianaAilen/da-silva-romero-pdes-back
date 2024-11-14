package pdes.unq.com.APC.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pdes.unq.com.APC.entities.User;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Métodos personalizados de consulta pueden ser añadidos aquí
    User findByUsername(String username);
    User findByEmail(String email);

}