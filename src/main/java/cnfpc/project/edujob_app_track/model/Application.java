package cnfpc.project.edujob_app_track.model;

import java.time.LocalDate;
import java.util.Set;

import cnfpc.project.edujob_app_track.model.Enums.ApplicationStatus;
import cnfpc.project.edujob_app_track.model.Enums.ApplicationType;
import cnfpc.project.edujob_app_track.model.Enums.ResultStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;
    
    @Column(length = 5000)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Enumerated(EnumType.STRING)
    private ApplicationType applicationType; // JOB / UNIVERSITY / LYCEE / COURSE

    @Column(updatable = false)
    private LocalDate creationDate;
    private LocalDate submitDate;
    private LocalDate submitDeadline;
    private LocalDate responseDeadline;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // PLANNED, SUBMITTED, ACCEPTED, REJECTED, etc.

    @Enumerated(EnumType.STRING)
    private ResultStatus responseStatus;

    @Column(length = 2000)
    private String resultNotes;
    // Many-to-Many: Application ↔ Document
    @ManyToMany
    @JoinTable(
        name = "app_doc",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    
    private Set<Document> documents;

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            this.creationDate = LocalDate.now();
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
    }

    public LocalDate getSubmitDeadline() {
        return submitDeadline;
    }

    public void setSubmitDeadline(LocalDate submitDeadline) {
        this.submitDeadline = submitDeadline;
    }

    public LocalDate getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(LocalDate responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResultStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResultStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResultNotes() {
        return resultNotes;
    }

    public void setResultNotes(String resultNotes) {
        this.resultNotes = resultNotes;
    }

    
}
