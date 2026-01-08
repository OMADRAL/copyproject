package ma.xproce.ecoclean.service;
import ma.xproce.ecoclean.dao.entities.Signalement;
import ma.xproce.ecoclean.dao.repositories.SignalementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SignalementManager implements SignalementService {

    private final SignalementRepository signalementRepository;

    public SignalementManager(SignalementRepository signalementRepository) {
        this.signalementRepository = signalementRepository;
    }

    @Override
    public List<Signalement> getAllSignalements() {
        return signalementRepository.findAll();
    }

    // NOUVEAU : Pagination pour tous les signalements
    @Override
    public Page<Signalement> getAllSignalements(Pageable pageable) {
        return signalementRepository.findAll(pageable);
    }

    // NOUVEAU : Pagination pour les signalements d'un utilisateur
    @Override
    public Page<Signalement> getUserSignalements(Long userId, Pageable pageable) {
        return signalementRepository.findByUserId(userId, pageable);
    }

    @Override
    public Signalement updateSignalement(Signalement signalement) {
        return signalementRepository.save(signalement);
    }

    @Override
    public Signalement getSignalementById(Long id) {
        return signalementRepository.findById(id).orElse(null);
    }

    @Override
    public Signalement saveSignalement(Signalement signalement) {
        return signalementRepository.save(signalement);
    }

    @Override
    public boolean deleteSignalement(Long id) {
        try {
            signalementRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}