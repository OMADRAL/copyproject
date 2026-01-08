package ma.xproce.ecoclean.dao.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collection;

@Entity
@Table(name = "`user`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(nullable = false) // NOUVEAU
    private String password; // NOUVEAU - stockera le mot de passe crypté

    private String role; // "USER" ou "ADMIN"

    @OneToMany(mappedBy = "user")
    private Collection<Signalement> signalements;
}