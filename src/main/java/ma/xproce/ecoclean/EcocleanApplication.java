package ma.xproce.ecoclean;
import ma.xproce.ecoclean.dao.entities.Signalement;
import ma.xproce.ecoclean.dao.entities.User;
import ma.xproce.ecoclean.service.SignalementService;
import ma.xproce.ecoclean.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EcocleanApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcocleanApplication.class, args);
    }

    @Bean
    CommandLineRunner start(UserService userService, SignalementService signalementService, PasswordEncoder passwordEncoder) {
        return args -> {
            User user1 = new User(null, "Ahmed", "ahmed@gmail.com",
                    passwordEncoder.encode("motdepasse"), "USER", null);
            User user2 = new User(null, "Fatima", "fatima@gmail.com",
                    passwordEncoder.encode("motdepasse"), "USER", null);
            User admin1 = new User(null, "Admin EcoClean", "admin@ecoclean.ma",
                    passwordEncoder.encode("motdepasse"), "ADMIN", null);

            user1 = userService.saveUser(user1);
            user2 = userService.saveUser(user2);
            admin1 = userService.saveUser(admin1);

            System.out.println("=== COMPTES DE TEST ===");
            System.out.println("Citoyen: ahmed@gmail.com / motdepasse");
            System.out.println("Citoyen: fatima@gmail.com / motdepasse");
            System.out.println("Admin: admin@ecoclean.ma / motdepasse");
            System.out.println("=======================");

            Signalement s1 = new Signalement(null, "Déchets plastiques dans la rue principale",
                    "Rue Mohammed V, Casablanca", null, "SIGNALE", user1);
            Signalement s2 = new Signalement(null, "Poubelle publique pleine",
                    "Place Jamaa El Fna, Marrakech", null, "EN_COURS", user2);
            Signalement s3 = new Signalement(null, "Déchargement sauvage",
                    "Route de Rabat, Salé", null, "RESOLU", user1);

            signalementService.saveSignalement(s1);
            signalementService.saveSignalement(s2);
            signalementService.saveSignalement(s3);

            System.out.println("Signalements créés avec succès !");
            System.out.println("Accédez à l'application: http://localhost:8090/");
        };
    }
}