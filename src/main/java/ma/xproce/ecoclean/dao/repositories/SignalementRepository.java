package ma.xproce.ecoclean.dao.repositories;

import ma.xproce.ecoclean.dao.entities.Signalement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignalementRepository extends JpaRepository<Signalement, Long> {
    // NOUVEAU : Méthode pour la pagination par utilisateur
    Page<Signalement> findByUserId(Long userId, Pageable pageable);
}