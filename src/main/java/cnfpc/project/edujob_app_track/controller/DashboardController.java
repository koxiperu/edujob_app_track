package cnfpc.project.edujob_app_track.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cnfpc.project.edujob_app_track.model.Application;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationStatus;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationType;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.ApplicationRepository;
import cnfpc.project.edujob_app_track.repository.UserRepository;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public DashboardController(UserRepository userRepository,
                               ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Principal principal,
            @RequestParam(value = "status", required = false) ApplicationStatus status,
            @RequestParam(value = "type", required = false) ApplicationType type
    ) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        List<Application> applications;

        if (status != null && type != null) {
                // Filter by both status and type
                applications = applicationRepository.findByUserAndStatusAndApplicationType(user, status, type);
            } else if (status != null) {
                applications = applicationRepository.findByUserAndStatus(user, status);
            } else if (type != null) {
                applications = applicationRepository.findByUserAndApplicationType(user, type);
            } else {
                applications = applicationRepository.findByUser(user);
            }

        LocalDate today = LocalDate.now();

        // Create a map of applicationId → highlight (true/false)
        Map<Long, Boolean> highlightMap = applications.stream()
                .collect(Collectors.toMap(
                    Application::getId,
                    app -> app.getSubmitDeadline() != null &&
                        !app.getSubmitDeadline().isBefore(today) &&  // future or today
                        !app.getSubmitDeadline().isAfter(today.plusDays(7)) // within 7 days
                ));

        model.addAttribute("highlightMap", highlightMap);
        model.addAttribute("applications", applications);
        model.addAttribute("username", principal.getName());
        model.addAttribute("title", "Dashboard");
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("types", ApplicationType.values());
        model.addAttribute("selectedType", type);
        return "dashboard";
    }
}


