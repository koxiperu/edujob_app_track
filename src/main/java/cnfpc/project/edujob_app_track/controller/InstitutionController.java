package cnfpc.project.edujob_app_track.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cnfpc.project.edujob_app_track.model.Enums.InstitutionType;
import cnfpc.project.edujob_app_track.model.Institution;
import cnfpc.project.edujob_app_track.repository.InstitutionRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/institutions")
public class InstitutionController {
    private final InstitutionRepository institutionRepository;

    public InstitutionController(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @GetMapping
    public String list(Model model) {
        Map<InstitutionType, List<Institution>> grouped = Arrays.stream(InstitutionType.values())
                .collect(Collectors.toMap(
                        type -> type,
                        type -> institutionRepository.findByType(type)
                ));

        model.addAttribute("groupedInstitutions", grouped);
        model.addAttribute("title", "Institutions");
        model.addAttribute("containerClass", "user");

        return "institution/list";
    }

    /* CREATE FORM */
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) String returnUrl, Model model) {
        model.addAttribute("institution", new Institution());
        model.addAttribute("types", InstitutionType.values());
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("title", "Add Institution");
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
            model.addAttribute("title", "Add Institution");
            return "institution/form";
        }

        institutionRepository.save(institution);

        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }

        return "redirect:/institutions";
    }
    /* EDIT FORM */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Institution institution = institutionRepository.findById(id).orElseThrow();

        model.addAttribute("institution", institution);
        model.addAttribute("types", InstitutionType.values());
        model.addAttribute("title", "Edit Institution");
        model.addAttribute("containerClass", "forms");
        return "institution/form";
    }

    /* UPDATE */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,@Valid @ModelAttribute Institution institution, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", InstitutionType.values());
            model.addAttribute("title", "Edit Institution");
            return "institution/form";
        }

        institution.setId(id);
        institutionRepository.save(institution);
        return "redirect:/institutions";
    }

    /* DELETE */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        institutionRepository.deleteById(id);
        return "redirect:/institutions";
    }
}
