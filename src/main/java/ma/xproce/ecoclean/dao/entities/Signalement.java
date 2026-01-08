package ma.xproce.ecoclean.dao.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "signalement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Signalement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "La description est obligatoire")
    @Size(min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
    private String description;

    @NotEmpty(message = "La localisation est obligatoire")
    private String localisation;

    @Column(columnDefinition = "TEXT")
    private String photo; // Stocke soit l'URL soit les données base64

    private String statut = "SIGNALE"; // SIGNALE, EN_COURS, RESOLU

    @ManyToOne
    private User user;
}