package ma.xproce.ecoclean.web;

import ma.xproce.ecoclean.dao.entities.Signalement;
import ma.xproce.ecoclean.dao.entities.User;
import ma.xproce.ecoclean.dao.repositories.UserRepository;
import ma.xproce.ecoclean.service.SignalementService;
import ma.xproce.ecoclean.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@Controller
public class SignalementController {

    @Autowired
    private SignalementService signalementService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // PAGE USER - Liste des signalements AVEC PAGINATION
    @GetMapping("/user/dashboard")
    public String userDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Récupérer l'utilisateur connecté via Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null || !"USER".equals(currentUser.getRole())) {
            return "error";
        }

        // Configuration de la pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Signalement> userSignalements = signalementService.getUserSignalements(currentUser.getId(), pageable);

        // CORRECTION ICI : Changez "signalements" en "listsignalements"
        model.addAttribute("listsignalements", userSignalements.getContent()); // <-- CHANGÉ
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userSignalements.getTotalPages());
        model.addAttribute("currentUser", currentUser);
        return "userDashboard";
    }

    // PAGE ADMIN - Liste de TOUS les signalements AVEC PAGINATION
    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentAdmin = userRepository.findByEmail(email).orElse(null);

        if (currentAdmin == null || !"ADMIN".equals(currentAdmin.getRole())) {
            return "error";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Signalement> signalements = signalementService.getAllSignalements(pageable);

        // CORRECTION ICI : Changez "signalements" en "listsignalements"
        model.addAttribute("listsignalements", signalements.getContent()); // <-- CHANGÉ
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", signalements.getTotalPages());
        model.addAttribute("currentAdmin", currentAdmin);
        return "adminDashboard";
    }

    // CÔTÉ USER Formulaire d'ajout de signalement
    @GetMapping("/user/addSignalement")
    public String showUserAddForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null || !"USER".equals(currentUser.getRole())) {
            return "error";
        }

        model.addAttribute("signalement", new Signalement());
        model.addAttribute("currentUser", currentUser);
        return "userAddSignalement";
    }

    // CÔTÉ USER Ajouter un signalement AVEC VALIDATION
    @PostMapping("/user/addSignalement")
    public String addUserSignalement(
            @Valid @ModelAttribute("signalement") Signalement signalement,
            @RequestParam(name = "photo", required = false) String photo,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null || !"USER".equals(currentUser.getRole())) {
            return "error";
        }

        // Traitement de la photo
        String processedPhoto = photo;
        if (photo != null && photo.startsWith("data:image") && photo.length() > 500000) {
            processedPhoto = null;
        }

        signalement.setPhoto(processedPhoto);
        signalement.setStatut("SIGNALE");
        signalement.setUser(currentUser);

        signalementService.saveSignalement(signalement);
        return "redirect:/user/dashboard";
    }

    // CÔTÉ USER - Supprimer un signalement
    @GetMapping("/user/deleteSignalement")
    public String userDeleteSignalement(@RequestParam Long signalementId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null || !"USER".equals(currentUser.getRole())) {
            return "error";
        }

        Signalement signalement = signalementService.getSignalementById(signalementId);
        if (signalement != null && signalement.getUser() != null &&
                signalement.getUser().getId().equals(currentUser.getId())) {
            signalementService.deleteSignalement(signalementId);
        }

        return "redirect:/user/dashboard";
    }

    // CÔTÉ ADMIN - Modifier le statut
    @GetMapping("/admin/editSignalement")
    public String adminEditSignalement(@RequestParam(name = "signalementId") Long signalementId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentAdmin = userRepository.findByEmail(email).orElse(null);

        if (currentAdmin == null || !"ADMIN".equals(currentAdmin.getRole())) {
            return "error";
        }

        Signalement signalement = signalementService.getSignalementById(signalementId);
        if (signalement != null) {
            model.addAttribute("signalementToBeUpdated", signalement);
            model.addAttribute("currentAdmin", currentAdmin);
            return "adminUpdateSignalement";
        } else {
            return "error";
        }
    }

    // CÔTÉ ADMIN - Mettre à jour le statut
    @PostMapping("/admin/updateSignalement")
    public String adminUpdateSignalement(
            @RequestParam(name = "signalementId") Long signalementId,
            @RequestParam(name = "statut") String statut) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentAdmin = userRepository.findByEmail(email).orElse(null);

        if (currentAdmin == null || !"ADMIN".equals(currentAdmin.getRole())) {
            return "error";
        }

        Signalement signalement = signalementService.getSignalementById(signalementId);
        if (signalement != null) {
            signalement.setStatut(statut);
            signalementService.updateSignalement(signalement);
            return "redirect:/admin/dashboard";
        } else {
            return "error";
        }
    }

    // CÔTÉ ADMIN - Supprimer un signalement
    @GetMapping("/admin/deleteSignalement")
    public String adminDeleteSignalement(@RequestParam Long signalementId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentAdmin = userRepository.findByEmail(email).orElse(null);

        if (currentAdmin == null || !"ADMIN".equals(currentAdmin.getRole())) {
            return "error";
        }

        signalementService.deleteSignalement(signalementId);
        return "redirect:/admin/dashboard";
    }
}