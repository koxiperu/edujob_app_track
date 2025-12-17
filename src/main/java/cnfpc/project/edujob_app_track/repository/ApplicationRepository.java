package cnfpc.project.edujob_app_track.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cnfpc.project.edujob_app_track.model.Application;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationStatus;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationType;
import cnfpc.project.edujob_app_track.model.Enums.ResultStatus;
import cnfpc.project.edujob_app_track.model.User;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>{
    List<Application> findByUser(User user);
    List<Application> findByUserAndStatus(User user, ApplicationStatus status);
    List<Application> findByStatus(ApplicationStatus status);
    List<Application> findByApplicationType(ApplicationType type);
    List<Application> findByTitleContainingIgnoreCase(String title);
    List<Application> findByResponseStatus(String resultStatus);
    List<Application> findByUserAndResponseStatus(User user, ResultStatus resultStatus);
    List<Application> findByUserAndStatusAndResponseStatus(User user, ApplicationStatus status, ResultStatus resultStatus);
    List<Application> findByStatusAndResponseStatus(ApplicationStatus status, ResultStatus resultStatus);

    List<Application> findByUserAndApplicationType(User user, ApplicationType type);
    List<Application> findByUserAndStatusAndApplicationType(User user, ApplicationStatus status, ApplicationType type);

    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.documents LEFT JOIN FETCH a.institution WHERE a.id = :id")
    Optional<Application> findByIdWithDocuments(@Param("id") Long id);

}
