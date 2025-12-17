package cnfpc.project.edujob_app_track.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import cnfpc.project.edujob_app_track.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("SELECT u.supervisedUsers FROM User u WHERE u.id = :id")
    Set<User> findSupervisedUsers(Long id);

    @Query("SELECT u.supervisors FROM User u WHERE u.id = :id")
    Set<User> findSupervisors(Long id);
}
