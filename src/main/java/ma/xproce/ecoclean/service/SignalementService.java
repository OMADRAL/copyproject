package ma.xproce.ecoclean.service;
import ma.xproce.ecoclean.dao.entities.Signalement;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SignalementService {
    Signalement saveSignalement(Signalement signalement);
    Signalement updateSignalement(Signalement signalement);
    boolean deleteSignalement(Long id);
    Signalement getSignalementById(Long id);
    List<Signalement> getAllSignalements();

    // NOUVEAUX : Méthodes de pagination
    Page<Signalement> getAllSignalements(Pageable pageable);
    Page<Signalement> getUserSignalements(Long userId, Pageable pageable);
}