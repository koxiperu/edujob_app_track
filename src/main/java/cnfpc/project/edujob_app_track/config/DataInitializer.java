package cnfpc.project.edujob_app_track.config;

import cnfpc.project.edujob_app_track.model.Institution;
import cnfpc.project.edujob_app_track.model.Role;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.model.Application;
import cnfpc.project.edujob_app_track.model.Document;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationStatus;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationType;
import cnfpc.project.edujob_app_track.model.Enums.DocumentStatus;
import cnfpc.project.edujob_app_track.model.Enums.InstitutionType;
import cnfpc.project.edujob_app_track.model.Enums.ResultStatus;
import cnfpc.project.edujob_app_track.repository.ApplicationRepository;
import cnfpc.project.edujob_app_track.repository.DocumentRepository;
import cnfpc.project.edujob_app_track.repository.InstitutionRepository;
import cnfpc.project.edujob_app_track.repository.RoleRepository;
import cnfpc.project.edujob_app_track.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, InstitutionRepository institutionRepository, DocumentRepository documentRepository, ApplicationRepository applicationRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.documentRepository = documentRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize Roles
        roleRepository.findByName("USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("USER");
            return roleRepository.save(role);
        });

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        // Initialize Admin User
        Optional<User> adminUserOptional = userRepository.findByUsername("annabu");
        if (adminUserOptional.isEmpty()) {
            User admin = new User();
            admin.setUsername("annabu");
            admin.setPassword(new BCryptPasswordEncoder().encode("admin")); // Pre-generated hash for "admin"
            admin.setEmail("anna@bu.com");
            admin.setFirstName("Anna");
            admin.setLastName("Bu");
            admin.setBirthDate(LocalDate.of(2000, 5, 16)); // Example date
            admin.setPhone("123456789");
            admin.setRole(adminRole); // Assign ADMIN role
            userRepository.save(admin);
            adminUserOptional = Optional.of(admin); // update optional for company creation
        }

        // Initialize Test User
        Optional<User> testUserOptional = userRepository.findByUsername("testuser");
        if (testUserOptional.isEmpty()) {
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(new BCryptPasswordEncoder().encode("password")); // Pre-generated hash for "password"
            testUser.setEmail("test@user.com");
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setBirthDate(LocalDate.of(1995, 1, 1)); // Example date
            testUser.setPhone("987654321");
            // Fetch the USER role, ensuring it exists
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new IllegalStateException("Default user role not found")); // Changed error message
            testUser.setRole(userRole); // Assign USER role
            userRepository.save(testUser);
            testUserOptional = Optional.of(testUser); // update optional for company creation
        }

        // Add default companies for existing users (annabu and testuser)
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

        if (adminUserOptional.isPresent()) {
            User admin = adminUserOptional.get();
            for (String companyName : companies) {
                // Check if the institution already exists for this user
                if (institutionRepository.findByNameAndUser(companyName, admin).isEmpty()) {
                    Institution institution = new Institution();
                    institution.setName(companyName);
                    institution.setType(InstitutionType.EMPLOYER);
                    institution.setCountry("Luxembourg");
                    institution.setAddress("123, " + companyName.replaceAll("\\s+", " ") + " Street, L-1234 Luxembourg");
                    institution.setWebsite("www." + companyName.toLowerCase().replaceAll("\\s+", "") + ".lu");
                    institution.setEmail("contact@" + companyName.toLowerCase().replaceAll("\\s+", "") + ".lu");
                    institution.setPhone("+352 123 456 789");
                    institution.setUser(admin);
                    institutionRepository.save(institution);
                }
            }
        }

        if (testUserOptional.isPresent()) {
            User testUser = testUserOptional.get();
            for (String companyName : companies) {
                // Check if the institution already exists for this user
                if (institutionRepository.findByNameAndUser(companyName, testUser).isEmpty()) {
                    Institution institution = new Institution();
                    institution.setName(companyName);
                    institution.setType(InstitutionType.EMPLOYER);
                    institution.setCountry("Luxembourg");
                    institution.setAddress("123, " + companyName.replaceAll("\\s+", " ") + " Street, L-1234 Luxembourg");
                    institution.setWebsite("www." + companyName.toLowerCase().replaceAll("\\s+", "") + ".lu");
                    institution.setEmail("contact@" + companyName.toLowerCase().replaceAll("\\s+", "") + ".lu");
                    institution.setPhone("+352 123 456 789");
                    institution.setUser(testUser);
                    institutionRepository.save(institution);
                }
            }
        }

        // Create documents and applications for testuser
        if (testUserOptional.isPresent()) {
            User testUser = testUserOptional.get();
            List<Document> testUserDocs = new ArrayList<>();
            if (documentRepository.findAllByUser(testUser).isEmpty()) {
                for (int i = 1; i <= 5; i++) {
                    Document doc = new Document();
                    doc.setFileName("TestUser_Document_" + i);
                    doc.setContentType("application/pdf");
                    doc.setStatus(DocumentStatus.READY);
                    doc.setUser(testUser);
                    testUserDocs.add(documentRepository.save(doc));
                }
            }

            // Clear existing applications and re-seed
            applicationRepository.deleteAll(applicationRepository.findByUser(testUser));

            List<Institution> testUserInsts = institutionRepository.findAllByUser(testUser);
            if (!testUserInsts.isEmpty()) {
                Application app1 = new Application();
                app1.setTitle("Job at " + testUserInsts.get(0).getName());
                app1.setUser(testUser);
                app1.setInstitution(testUserInsts.get(0));
                app1.setApplicationType(ApplicationType.JOB);
                app1.setStatus(ApplicationStatus.PLANNED);
                app1.setSubmitDeadline(LocalDate.now().plusDays(3)); // Added deadline
                app1.setResponseStatus(ResultStatus.PENDING);
                if (!testUserDocs.isEmpty()) {
                    app1.getDocuments().add(testUserDocs.get(0));
                    app1.getDocuments().add(testUserDocs.get(1));
                }
                applicationRepository.save(app1);

                Application app2 = new Application();
                app2.setTitle("Internship at " + testUserInsts.get(1).getName());
                app2.setUser(testUser);
                app2.setInstitution(testUserInsts.get(1));
                app2.setApplicationType(ApplicationType.JOB);
                app2.setStatus(ApplicationStatus.SUBMITTED);
                app2.setSubmitDeadline(LocalDate.now().plusMonths(1)); // Added deadline
                app2.setResponseStatus(ResultStatus.PENDING);
                applicationRepository.save(app2); // No documents

                Application app3 = new Application();
                app3.setTitle("University Application to " + testUserInsts.get(2).getName());
                app3.setUser(testUser);
                app3.setInstitution(testUserInsts.get(2));
                app3.setApplicationType(ApplicationType.UNIVERSITY);
                app3.setStatus(ApplicationStatus.ACCEPTED);
                app3.setSubmitDeadline(LocalDate.now().plusMonths(2)); // Added deadline
                app3.setResponseStatus(ResultStatus.SUCCESSFUL);
                if (testUserDocs.size() >= 3) {
                    app3.getDocuments().add(testUserDocs.get(2));
                }
                applicationRepository.save(app3);
            }
        }
        
        // Create documents and applications for admin
        if (adminUserOptional.isPresent()) {
            User admin = adminUserOptional.get();
            List<Document> adminDocs = new ArrayList<>();
            if (documentRepository.findAllByUser(admin).isEmpty()) {
                for (int i = 1; i <= 10; i++) {
                    Document doc = new Document();
                    doc.setFileName("Admin_Document_" + i);
                    doc.setContentType("application/pdf");
                    doc.setStatus(DocumentStatus.READY);
                    doc.setUser(admin);
                    adminDocs.add(documentRepository.save(doc));
                }
            }

            applicationRepository.deleteAll(applicationRepository.findByUser(admin));
            List<Institution> adminInsts = institutionRepository.findAllByUser(admin);
            if (adminInsts.size() >= 7) {
                for (int i = 0; i < 7; i++) {
                    Application app = new Application();
                    app.setTitle("Admin Application " + (i + 1));
                    app.setUser(admin);
                    app.setInstitution(adminInsts.get(i));
                    app.setApplicationType(i % 2 == 0 ? ApplicationType.JOB : ApplicationType.COURSE);
                    app.setStatus(ApplicationStatus.values()[i % ApplicationStatus.values().length]);
                    app.setResponseStatus(ResultStatus.values()[i % ResultStatus.values().length]);
                    
                    if (i == 1) { // Close deadline
                        app.setSubmitDeadline(LocalDate.now().plusDays(2));
                    } else if (i == 3) { // Close deadline
                        app.setSubmitDeadline(LocalDate.now().plusDays(5));
                    } else {
                        app.setSubmitDeadline(LocalDate.now().plusMonths(i + 1)); // Longer deadlines
                    }

                    if (i > 0 && adminDocs.size() > i) { 
                        app.getDocuments().add(adminDocs.get(i));
                    }
                    if (i == 2 && adminDocs.size() > 8) {
                        app.getDocuments().add(adminDocs.get(8));
                    }
                    applicationRepository.save(app);
                }
            }
        }
    }
}

