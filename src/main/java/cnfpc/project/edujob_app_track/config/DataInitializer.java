/*package cnfpc.project.edujob_app_track.config;

import cnfpc.project.edujob_app_track.model.Role;
import cnfpc.project.edujob_app_track.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {  // only if table is empty
            roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("USER"));
        }
    }
}

*/