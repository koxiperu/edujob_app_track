package cnfpc.project.edujob_app_track.controller;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("title", "My Profile");
        model.addAttribute("containerClass", "user"); // optional, use different background

        return "user/profile";
    }

    @GetMapping("/edit")
    public String editForm(Model model, Principal principal, HttpServletRequest request) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow();

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);
        model.addAttribute("user", user);
        model.addAttribute("title", "Edit Profile");
        model.addAttribute("containerClass", "forms");
        return "user/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Principal principal,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Edit Profile");
            model.addAttribute("user", user);
            return "user/edit";
        }

        User existingUser = userRepository.findById(user.getId())
        .orElseThrow(); // use ID from form

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setBirthDate(user.getBirthDate());

        userRepository.save(existingUser);

        return "redirect:/user/profile?updated";
    }
    @PostMapping("/delete")
    public String deleteAccount(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow();

        userRepository.delete(user);
        SecurityContextHolder.clearContext();

        return "redirect:/login?deleted";
    }
}
