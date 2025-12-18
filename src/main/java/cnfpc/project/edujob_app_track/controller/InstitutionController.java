package cnfpc.project.edujob_app_track.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cnfpc.project.edujob_app_track.model.Application;
import cnfpc.project.edujob_app_track.model.Enums.InstitutionType;
import cnfpc.project.edujob_app_track.model.Institution;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.ApplicationRepository;
import cnfpc.project.edujob_app_track.repository.InstitutionRepository;
import cnfpc.project.edujob_app_track.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/institutions")
public class InstitutionController {
    private final InstitutionRepository institutionRepository;
    private final UserService userService;
    private final ApplicationRepository applicationRepository;

    public InstitutionController(InstitutionRepository institutionRepository, UserService userService, ApplicationRepository applicationRepository) {
        this.institutionRepository = institutionRepository;
        this.userService = userService;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        User currentUser = userService.getLoggedInUser();
        Map<InstitutionType, List<Institution>> grouped = Arrays.stream(InstitutionType.values())
                .collect(Collectors.toMap(
                        type -> type,
                        type -> institutionRepository.findByTypeAndUser(type, currentUser)
                ));
        boolean noInstitutions = grouped.values().stream().allMatch(List::isEmpty);
        model.addAttribute("noInstitutions", noInstitutions);
        model.addAttribute("groupedInstitutions", grouped);
        model.addAttribute("title", "Companies");
        model.addAttribute("containerClass", "user");

        return "institution/list";
    }

    /* CREATE FORM */
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) String returnUrl, Model model) {
        model.addAttribute("institution", new Institution());
        model.addAttribute("types", InstitutionType.values());
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("title", "Add Company");
        model.addAttribute("containerClass", "forms");
        return "institution/form";
    }

    /* CREATE */
    @PostMapping
    public String create(@Valid @ModelAttribute Institution institution,
                        BindingResult result,
                        @RequestParam(required = false) String returnUrl,
                        Model model) {

        if (result.hasErrors()) {
            model.addAttribute("types", InstitutionType.values());
            model.addAttribute("returnUrl", returnUrl);
            model.addAttribute("title", "Add Company");
            return "institution/form";
        }

        User currentUser = userService.getLoggedInUser();
        institution.setUser(currentUser);

        institutionRepository.save(institution);

        return returnUrl != null && !returnUrl.isBlank() ? "redirect:" + returnUrl : "redirect:/institutions";

    }
    /* EDIT FORM */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Institution institution = institutionRepository.findById(id).orElseThrow();
        if (!institution.getUser().equals(userService.getLoggedInUser())) {
            throw new AccessDeniedException("You cannot edit this institution");
        }
        model.addAttribute("institution", institution);
        model.addAttribute("types", InstitutionType.values());
        model.addAttribute("title", "Edit Company");
        model.addAttribute("containerClass", "forms");
        return "institution/form";
    }

    /* UPDATE */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,@Valid @ModelAttribute Institution institution, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", InstitutionType.values());
            model.addAttribute("title", "Edit Company");
            return "institution/form";
        }
        
        if (!institution.getUser().equals(userService.getLoggedInUser())) {
            throw new AccessDeniedException("You cannot edit this company");
        }
        institution.setId(id);
        institutionRepository.save(institution);
        return "redirect:/institutions";
    }

    /* DELETE */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        Institution institution = institutionRepository.findById(id).orElseThrow();
        if (!institution.getUser().equals(userService.getLoggedInUser())) {
            throw new AccessDeniedException("You cannot edit this company");
        }
        List<Application> applications = applicationRepository.findAllByInstitutionId(id);
        if (!applications.isEmpty()) {
            // Redirect to institution-used page instead of attempting deletion
            return "redirect:/applications/institutions/" + id + "/used";
        }
        institutionRepository.deleteById(id);
        return "redirect:/institutions";
    }

    @GetMapping("/applications/institutions/{id}/used")
    public String institutionUsedPage(@PathVariable Long id, Model model, Principal principal) {

        Institution institution = institutionRepository.findById(id).orElseThrow();
        if (!institution.getUser().equals(userService.getLoggedInUser())) {
            throw new AccessDeniedException("You cannot edit this company");
        }

        List<Application> applications =
                applicationRepository.findAllByInstitutionId(id); // You need a repository method

        model.addAttribute("institution", institution);
        model.addAttribute("applications", applications);
        model.addAttribute("title", "Company is in use");

        return "application/institution_used"; // new Thymeleaf template
    }

}
