package cnfpc.project.edujob_app_track.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cnfpc.project.edujob_app_track.model.Document;
import cnfpc.project.edujob_app_track.model.Enums.DocumentStatus;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.DocumentRepository;
import cnfpc.project.edujob_app_track.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final UserService userService;

    public DocumentController(DocumentRepository documentRepository, UserService userService) {
        this.documentRepository = documentRepository;
        this.userService = userService;
    }

    /* LIST ALL DOCUMENTS */
    @GetMapping
    public String list(Model model, Principal principal) {
        User currentUser = userService.getLoggedInUser();
        model.addAttribute("documents", documentRepository.findAllByUser(currentUser));
        model.addAttribute("title", "Documents");
        model.addAttribute("containerClass", "user");
        return "document/list";
    }

    /* CREATE FORM (optionally with returnUrl) */
    @GetMapping("/new")
    public String newDocumentForm(@RequestParam(value = "returnUrl", required = false) String returnUrl,
                                  Model model) {
        model.addAttribute("document", new Document());
        model.addAttribute("statuses", DocumentStatus.values());
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("title", "Add Document");
        model.addAttribute("containerClass", "forms");
        return "document/form";
    }

    /* CREATE DOCUMENT */
    @PostMapping
    public String createDocument(@Valid @ModelAttribute Document document,
                                 BindingResult result,
                                 @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 Model model,
                                Principal principal) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("statuses", DocumentStatus.values());
            model.addAttribute("title", "Add Document");
            return "document/form";
        }

        if (file != null && !file.isEmpty()) {
            document.setFileName(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            document.setData(file.getBytes());
        }

        User currentUser = userService.getLoggedInUser();
        document.setUser(currentUser);
        documentRepository.save(document);
        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/documents";
    }

    /* EDIT FORM */
    @GetMapping("/{id}/edit")
    public String editDocument(@PathVariable Long id,
                               @RequestParam(value = "returnUrl", required = false) String returnUrl,
                               Model model,Principal principal) {
        User currentUser = userService.getLoggedInUser();
        Document doc = documentRepository.findById(id)
            .filter(d -> d.getUser().equals(currentUser))
            .orElseThrow(() -> new SecurityException("You cannot access this document"));
        model.addAttribute("document", doc);
        model.addAttribute("statuses", DocumentStatus.values());
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("title", "Edit Document");
        model.addAttribute("containerClass", "forms");
        return "document/form";
    }

    /* UPDATE DOCUMENT */
    @PostMapping("/{id}/edit")
    public String updateDocument(@PathVariable Long id,
                                 @Valid @ModelAttribute Document document,
                                 BindingResult result,
                                 @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 Model model,
                                Principal principal) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("statuses", DocumentStatus.values());
            model.addAttribute("title", "Edit Document");
            return "document/form";
        }

        User currentUser = userService.getLoggedInUser();
        Document doc = documentRepository.findByIdAndUser(id, currentUser).orElseThrow(() -> new SecurityException("You cannot access this document"));
        // Update file if provided
        if (file != null && !file.isEmpty()) {
            doc.setFileName(file.getOriginalFilename());
            doc.setContentType(file.getContentType());
            doc.setData(file.getBytes());
        }

        // Update other fields
        doc.setStatus(document.getStatus());
        documentRepository.save(doc);

        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/documents";
    }

    /* DELETE DOCUMENT */
    @PostMapping("/{id}/delete")
    public String deleteDocument(@PathVariable Long id,
                                 @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                Principal principal) {

        User currentUser = userService.getLoggedInUser();
        documentRepository.findByIdAndUser(id, currentUser).orElseThrow(() -> new SecurityException("You cannot access this document"));
        documentRepository.deleteById(id);
        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/documents";
    }

    /* DOWNLOAD DOCUMENT */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id, Principal principal) {
        User currentUser = userService.getLoggedInUser();
        Document doc = documentRepository.findById(id)
            .filter(d -> d.getUser().equals(currentUser))
            .orElseThrow(() -> new SecurityException("You cannot download this document"));


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(doc.getData());
    }
}
