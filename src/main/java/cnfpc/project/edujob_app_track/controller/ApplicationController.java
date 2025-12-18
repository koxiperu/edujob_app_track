package cnfpc.project.edujob_app_track.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import cnfpc.project.edujob_app_track.model.Application;
import cnfpc.project.edujob_app_track.model.Document;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationStatus;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationType;
import cnfpc.project.edujob_app_track.model.Enums.ResultStatus;
import cnfpc.project.edujob_app_track.model.Institution;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.ApplicationRepository;
import cnfpc.project.edujob_app_track.repository.DocumentRepository;
import cnfpc.project.edujob_app_track.repository.InstitutionRepository;
import cnfpc.project.edujob_app_track.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationRepository applicationRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public ApplicationController(ApplicationRepository applicationRepository,
                                 InstitutionRepository institutionRepository,
                                 UserRepository userRepository,
                                 DocumentRepository documentRepository) {
        this.applicationRepository = applicationRepository;
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    /* LIST APPLICATIONS */
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
    public String createForm(@RequestParam(value = "returnUrl", required = false) String returnUrl,
                             Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("documents", documentRepository.findAllByUser(user));
        model.addAttribute("application", new Application());
        model.addAttribute("types", ApplicationType.values());
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("responseStatuses", ResultStatus.values());
        model.addAttribute("institutions", institutionRepository.findAllByUser(user));
        model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/applications/new");
        model.addAttribute("title", "Add Application");
        model.addAttribute("containerClass", "forms");
        return "application/form";
    }

    /* CREATE APPLICATION */
    @PostMapping
    public String create(@Valid @ModelAttribute Application application,
                         BindingResult result,
                         Principal principal,
                         @RequestParam(value = "documentIds", required = false) List<Long> documentIds,
                         @RequestParam(value = "returnUrl", required = false) String returnUrl,
                         Model model) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("documents", documentRepository.findAllByUser(user));
        if (result.hasErrors()) {
            model.addAttribute("types", ApplicationType.values());
            model.addAttribute("statuses", ApplicationStatus.values());
            model.addAttribute("responseStatuses", ResultStatus.values());
            model.addAttribute("institutions", institutionRepository.findAllByUser(user));
            model.addAttribute("title", "Add Application");
            model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/applications/new");
            return "application/form";
        }
        application.setUser(user);

        if (documentIds != null) {
            List<Document> docs = documentRepository.findAllById(documentIds);
            application.setDocuments(docs);
        } else {
            application.setDocuments(new ArrayList<>());
        }

        applicationRepository.save(application);

        return "redirect:/applications";
    }

    /* EDIT FORM */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @RequestParam(value = "returnUrl", required = false) String returnUrl,
                           Model model, Principal principal) {
        Application application = applicationRepository.findByIdWithDocuments(id).orElseThrow();
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("documents", documentRepository.findAllByUser(user));
        model.addAttribute("application", application);
        model.addAttribute("app_docs", application.getDocuments());
        model.addAttribute("types", ApplicationType.values());
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("responseStatuses", ResultStatus.values());
        model.addAttribute("institutions", institutionRepository.findAllByUser(user));
        model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/applications/" + id + "/edit");
        model.addAttribute("title", "Edit Application");
        model.addAttribute("containerClass", "forms");
        return "application/form";
    }

    /* UPDATE APPLICATION */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Application formApplication,
                         BindingResult result,
                         @RequestParam(value = "documentIds", required = false) List<Long> documentIds,
                         @RequestParam(value = "returnUrl", required = false) String returnUrl,
                         Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("documents", documentRepository.findAllByUser(user));
        if (result.hasErrors()) {
            model.addAttribute("types", ApplicationType.values());
            model.addAttribute("statuses", ApplicationStatus.values());
            model.addAttribute("responseStatuses", ResultStatus.values());
            model.addAttribute("institutions", institutionRepository.findAllByUser(user));
            model.addAttribute("title", "Edit Application");
            model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/applications/" + id + "/edit");
            return "application/form";
        }

        Application existing = applicationRepository.findByIdWithDocuments(id).orElseThrow();
        existing.setTitle(formApplication.getTitle());
        existing.setDescription(formApplication.getDescription());
        existing.setApplicationType(formApplication.getApplicationType());
        existing.setStatus(formApplication.getStatus());
        existing.setInstitution(formApplication.getInstitution());
        existing.setSubmitDate(formApplication.getSubmitDate());
        existing.setSubmitDeadline(formApplication.getSubmitDeadline());
        existing.setResponseDeadline(formApplication.getResponseDeadline());
        existing.setResponseStatus(formApplication.getResponseStatus());
        existing.setResultNotes(formApplication.getResultNotes());

        if (documentIds != null && !documentIds.isEmpty()) {
            List<Document> newDocs = documentRepository.findAllById(documentIds);
            existing.setDocuments(newDocs); // ✅ SAFE NOW
        }else {
            existing.setDocuments(new ArrayList<>());
        }


        applicationRepository.save(existing);

        return "redirect:/applications";
    }



    /* DELETE APPLICATION */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        applicationRepository.deleteById(id);
        return "redirect:/applications";
    }

    @GetMapping("/{id}/details")
    public String viewApplicationDetails(@PathVariable Long id, Model model) {
        Application application = applicationRepository.findByIdWithDocuments(id).orElseThrow();;
        if (application==null) {
            // Optionally handle not found
            return "redirect:/applications"; 
        }

        model.addAttribute("title", "Application Details");
        model.addAttribute("app", application);
        return "application/details";
    }

    @GetMapping("/documents/{id}/used")
    public String documentUsedPage(@PathVariable Long id, Model model, Principal principal) {

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        Document document = documentRepository.findById(id)
                .filter(d -> d.getUser().equals(user))
                .orElseThrow(() -> new SecurityException("Access denied"));

        List<Application> applications =
                applicationRepository.findAllByDocumentId(id);

        model.addAttribute("document", document);
        model.addAttribute("applications", applications);
        model.addAttribute("title", "Document is in use");

        return "application/doc_used";
    }

    @GetMapping("/institutions/{id}/used")
    public String institutionUsedPage(@PathVariable Long id, Model model, Principal principal) {

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        Institution institution = institutionRepository.findById(id)
                .filter(d -> d.getUser().equals(user))
                .orElseThrow(() -> new SecurityException("Access denied"));

        List<Application> applications =
                applicationRepository.findAllByInstitutionId(id);

        model.addAttribute("institution", institution);
        model.addAttribute("applications", applications);
        model.addAttribute("title", "Institution is in use");

        return "application/inst_used";
    }

    @PostMapping("/documents/{docId}/applications/{appId}/delete")
    public String deleteApplicationFromDocument(
            @PathVariable Long docId,
            @PathVariable Long appId,
            Principal principal
    ) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        Application application = applicationRepository.findById(appId)
                .filter(a -> a.getUser().equals(user))
                .orElseThrow(() -> new SecurityException("Access denied"));

        applicationRepository.delete(application);

        return "redirect:/applications/documents/" + docId + "/used";
        
    }

    @PostMapping("/institutions/{instId}/applications/{appId}/delete")
    public String deleteApplicationFromInstitution(
            @PathVariable Long instId,
            @PathVariable Long appId,
            Principal principal
    ) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        Application application = applicationRepository.findById(appId)
                .filter(a -> a.getUser().equals(user))
                .orElseThrow(() -> new SecurityException("Access denied"));

        applicationRepository.delete(application);

        // Redirect to the correct “institution-used” page
        return "redirect:/applications/institutions/" + instId + "/used";
    }



}
