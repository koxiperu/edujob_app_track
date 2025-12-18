package cnfpc.project.edujob_app_track.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cnfpc.project.edujob_app_track.model.Document;
import cnfpc.project.edujob_app_track.model.Enums.DocumentStatus;
import cnfpc.project.edujob_app_track.model.User;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>{
    List<Document> findByStatus(DocumentStatus status);

    List<Document> findAllByUser(User user);

    Optional<Document> findByIdAndUser(Long id, User user);
}
