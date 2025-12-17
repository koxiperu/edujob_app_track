package cnfpc.project.edujob_app_track.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cnfpc.project.edujob_app_track.model.Document;
import cnfpc.project.edujob_app_track.model.Enums.DocumentStatus;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>{
    List<Document> findByStatus(DocumentStatus status);

    List<Document> findByFileNameContainingIgnoreCase(String fileName);
}
