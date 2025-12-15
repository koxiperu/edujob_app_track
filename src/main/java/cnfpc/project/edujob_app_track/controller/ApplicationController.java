package cnfpc.project.edujob_app_track.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import cnfpc.project.edujob_app_track.model.Application;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationStatus;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationType;
import cnfpc.project.edujob_app_track.model.Enums.ResultStatus;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.ApplicationRepository;
import cnfpc.project.edujob_app_track.repository.InstitutionRepository;
import cnfpc.project.edujob_app_track.repository.UserRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationRepository applicationRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;

    public ApplicationController(ApplicationRepository applicationRepository,
                                 InstitutionRepository institutionRepository,
                                 UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Application> applications = applicationRepository.findByUser(user);

        List<Application> jobApplications = applications.stream()
                .filter(a -> a.getApplicationType() == ApplicationType.JOB)
                .toList();

        List<Application> eduApplications = applications.stream()
                .filter(a -> a.getApplicationType() != ApplicationType.JOB)
                .toList();

        model.addAttribute("jobApplications", jobApplications);
        model.addAttribute("eduApplications", eduApplications);
        model.addAttribute("title", "My Applications");
        model.addAttribute("containerClass", "user");

        return "application/list";
    }


    /* CREATE FORM */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("application", new Application());
        model.addAttribute("types", ApplicationType.values());
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("responseStatuses", ResultStatus.values());
        model.addAttribute("institutions", institutionRepository.findAll());
        model.addAttribute("returnUrl", "/applications/new");
        model.addAttribute("title", "Add Application");
        model.addAttribute("containerClass", "forms");
        return "application/form";
    }

    /* CREATE */
    @PostMapping
    public String create(@Valid @ModelAttribute Application application,
                         BindingResult result,
                         Principal principal,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("types", ApplicationType.values());
            model.addAttribute("statuses", ApplicationStatus.values());
            model.addAttribute("resultStatus", ResultStatus.values());
            model.addAttribute("institutions", institutionRepository.findAll());
            model.addAttribute("title", "Add Application");
            return "application/form";
        }

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        application.setUser(user);
        applicationRepository.save(application);

        return "redirect:/applications";
    }

    /* EDIT FORM */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Application application = applicationRepository.findById(id).orElseThrow();

        model.addAttribute("application", application);
        model.addAttribute("types", ApplicationType.values());
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("responseStatuses", ResultStatus.values());
        model.addAttribute("institutions", institutionRepository.findAll());
        model.addAttribute("returnUrl", "/applications/" + id + "/edit");
        model.addAttribute("title", "Edit Application");
        model.addAttribute("containerClass", "forms");
        return "application/form";
    }

    /* UPDATE */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Application application,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("types", ApplicationType.values());
            model.addAttribute("statuses", ApplicationStatus.values());
            model.addAttribute("responseStatuses", ResultStatus.values());
            model.addAttribute("institutions", institutionRepository.findAll());
            model.addAttribute("returnUrl", "/applications/" + id + "/edit");
            model.addAttribute("title", "Edit Application");
            return "application/form";
        }

        application.setId(id);
        applicationRepository.save(application);

        return "redirect:/applications";
    }

    /* DELETE */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        applicationRepository.deleteById(id);
        return "redirect:/applications";
    }
}
