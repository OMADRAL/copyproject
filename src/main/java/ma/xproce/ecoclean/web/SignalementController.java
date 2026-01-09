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

    @GetMapping("/user/dashboard")
    public String userDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null || !"USER".equals(currentUser.getRole())) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Signalement> userSignalements = signalementService.getUserSignalements(currentUser.getId(), pageable);


        model.addAttribute("listsignalements", userSignalements.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userSignalements.getTotalPages());
        model.addAttribute("currentUser", currentUser);
        return "userDashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentAdmin = userRepository.findByEmail(email).orElse(null);

        if (currentAdmin == null || !"ADMIN".equals(currentAdmin.getRole())) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Signalement> signalements = signalementService.getAllSignalements(pageable);

        model.addAttribute("listsignalements", signalements.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", signalements.getTotalPages());
        model.addAttribute("currentAdmin", currentAdmin);
        return "adminDashboard";
    }

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
            return "redirect:/admin/dashboard";
        }
    }

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
            return "redirect:/admin/dashboard";
        }
    }

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