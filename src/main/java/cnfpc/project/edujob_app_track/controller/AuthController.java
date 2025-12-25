package cnfpc.project.edujob_app_track.controller;

import java.security.Principal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import cnfpc.project.edujob_app_track.model.Institution;
import cnfpc.project.edujob_app_track.model.Role;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.model.Enums.InstitutionType;
import cnfpc.project.edujob_app_track.repository.InstitutionRepository;
import cnfpc.project.edujob_app_track.repository.RoleRepository;
import cnfpc.project.edujob_app_track.repository.UserRepository;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final InstitutionRepository institutionRepository;
    
    public AuthController(UserRepository userRepository,PasswordEncoder passwordEncoder,RoleRepository roleRepository, InstitutionRepository institutionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.institutionRepository = institutionRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model, Principal principal) {
        if (principal != null) {
            // Already logged in
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Encode password
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register"; // Show form again with errors
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
            // Assign default role
            // Assign default role
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role not found"));
        user.setRole(defaultRole);

        // Save user
        userRepository.save(user);

        // Add default companies for the new user
        String[] companies = {
            "CFL - Société Nationale des Chemins de Fer Luxembourgeois",
            "Dussmann Luxembourg",
            "POST Luxembourg",
            "Amazon",
            "Cactus",
            "BNP PARIBAS Luxembourg",
            "PwC Luxembourg",
            "ArcelorMittal",
            "Goodyear",
            "Cargolux Airlines International SA"
        };

        for (String companyName : companies) {
            Institution institution = new Institution();
            institution.setName(companyName);
            institution.setType(InstitutionType.EMPLOYER);
            institution.setCountry("Luxembourg");
            institution.setAddress("123, " + companyName + " Street, L-1234 Luxembourg");
            institution.setWebsite("www." + companyName.toLowerCase().replaceAll("\\s+", "") + ".lu");
            institution.setEmail("contact@" + companyName.toLowerCase().replaceAll("\\s+", "") + ".lu");
            institution.setPhone("+352 123 456 789");
            institution.setUser(user); // Associate with the newly registered user
            institutionRepository.save(institution);
        }

        return "redirect:/login?registered";
    }
}
