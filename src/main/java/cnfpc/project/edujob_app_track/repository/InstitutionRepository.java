package cnfpc.project.edujob_app_track.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnfpc.project.edujob_app_track.model.Enums.InstitutionType;
import cnfpc.project.edujob_app_track.model.Institution;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long>{
    List<Institution> findByType(InstitutionType type);

    List<Institution> findByCountry(String country);

    List<Institution> findByNameContainingIgnoreCase(String name);
}

