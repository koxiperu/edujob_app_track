package cnfpc.project.edujob_app_track.model;

public class Enums {
    public enum DocumentStatus {
        READY,
        NOT_READY,
        IN_PROGRESS
    }
    public enum InstitutionType {
        UNIVERSITY,
        EMPLOYER,
        LYCEE,
        COURSE
    }   

    public enum ApplicationType {
        JOB, UNIVERSITY, LYCEE, COURSE
    }

    public enum ApplicationStatus {
        PLANNED,
        SUBMITTED,
        ACCEPTED,
        REJECTED,
        WAITLISTED,
        WITHDRAWN
    }

    public enum ResultStatus {
        PENDING,
        SUCCESSFUL,
        FAILED
    }
}
