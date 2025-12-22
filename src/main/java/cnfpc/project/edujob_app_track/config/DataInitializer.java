package cnfpc.project.edujob_app_track.config;

import cnfpc.project.edujob_app_track.model.Role;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.RoleRepository;
import cnfpc.project.edujob_app_track.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize Roles
        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
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
            admin.setPassword("$2a$10$OUbU6HaS.hEUMQoS22XBLut2XbULd9KKibq1WDoK4MoN5Mcmbe4Kmi"); // Pre-generated hash for "admin"
            admin.setEmail("anna@bu.com");
            admin.setFirstName("Anna");
            admin.setLastName("Bu");
            admin.setBirthDate(LocalDate.of(2000, 5, 28)); // Example date
            admin.setPhone("123456789");
            admin.setRole(adminRole); // Assign ADMIN role
            userRepository.save(admin);
        }
    }
}

