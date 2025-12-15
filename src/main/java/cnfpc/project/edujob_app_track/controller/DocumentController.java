package cnfpc.project.edujob_app_track.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cnfpc.project.edujob_app_track.model.Document;
import cnfpc.project.edujob_app_track.model.Enums.DocumentStatus;
import cnfpc.project.edujob_app_track.repository.DocumentRepository;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;

    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /* LIST */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("documents", documentRepository.findAll());
        model.addAttribute("title", "Documents");
        model.addAttribute("containerClass", "user");
        return "document/list";
    }

    /* UPLOAD FORM */
    @GetMapping("/new")
    public String uploadForm(Model model) {
        model.addAttribute("document", new Document());
        model.addAttribute("statuses", DocumentStatus.values());
        model.addAttribute("title", "Upload Document");
        model.addAttribute("containerClass", "forms");
        return "document/form";
    }
    

    /* UPLOAD */
    @PostMapping
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("status") DocumentStatus status,
                         Model model) throws IOException {

        if (file.isEmpty()) {
            model.addAttribute("error", "File is required");
            return "document/form";
        }

        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType());
        doc.setData(file.getBytes());
        doc.setStatus(status);

        documentRepository.save(doc);

        return "redirect:/documents";
    }

    /* DELETE */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        documentRepository.deleteById(id);
        return "redirect:/documents";
    }
    
    /* EDIT FORM */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Document doc = documentRepository.findById(id).orElseThrow();
        model.addAttribute("document", doc);
        model.addAttribute("statuses", DocumentStatus.values());
        model.addAttribute("title", "Edit Document");
        model.addAttribute("containerClass", "forms");
        return "document/form";
    }

    /* UPDATE */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                        @RequestParam(value = "file", required = false) MultipartFile file,
                        @RequestParam("status") DocumentStatus status,
                        Model model) throws IOException {

        Document doc = documentRepository.findById(id).orElseThrow();

        if (file != null && !file.isEmpty()) {
            doc.setFileName(file.getOriginalFilename());
            doc.setContentType(file.getContentType());
            doc.setData(file.getBytes());
        }

        doc.setStatus(status);
        documentRepository.save(doc);

        return "redirect:/documents";
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Document doc = documentRepository.findById(id).orElseThrow();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(doc.getData());
    }
}
