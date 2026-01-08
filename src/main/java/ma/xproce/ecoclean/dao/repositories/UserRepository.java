package ma.xproce.ecoclean.dao.repositories;
import ma.xproce.ecoclean.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // NOUVEAU : Pour Spring Security
    Optional<User> findByEmail(String email);
}

