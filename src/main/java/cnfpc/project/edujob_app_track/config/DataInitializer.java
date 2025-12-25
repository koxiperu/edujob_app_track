package cnfpc.project.edujob_app_track.config;

import cnfpc.project.edujob_app_track.model.Role;
import cnfpc.project.edujob_app_track.model.User;
import cnfpc.project.edujob_app_track.repository.RoleRepository;
import cnfpc.project.edujob_app_track.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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
            admin.setBirthDate(LocalDate.of(2000, 5, 28)); // Example date
            admin.setPhone("123456789");
            admin.setRole(adminRole); // Assign ADMIN role
            userRepository.save(admin);
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
            Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("USER");
                return roleRepository.save(newRole);
            });
            testUser.setRole(userRole); // Assign USER role
            userRepository.save(testUser);
        }
    }
}

