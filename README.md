# 1. EduJob Application Tracker info
This project is a Spring Boot + Thymeleaf MVC web application that allows users to track applications for jobs, universities, lycees or courses. Users can manage multiple applications, track required documents, deadlines, interviews, and application status. Users can also add supervisors (e.g., parents or career advisors) to oversee their application progress.
The project is designed to demonstrate full-stack Spring Boot skills, including MVC architecture, JPA relationships, form validation, authentication, file upload, and deployment using Docker with an Oracle database.

### Project description
- Track applications for jobs, universities, lycées, and courses.
- Users can be students or job seekers.
- Supervisors (like parents or career consultants) can monitor applications.
- Track required documents, deadlines, interviews, and responses.
- Upload and store documents (PDFs) in Oracle BLOBs.
- Notifications for upcoming deadlines/interviews.

### Project sctructure 
```
src/main/java
 ├── config        # Security & app config
 ├── controller    # MVC controllers
 ├── model         # JPA entities
 ├── repository    # Spring Data repositories
 └── service       # Business logic

src/main/resources
 ├── templates     # Thymeleaf HTML
 ├── static        # CSS, JS, images
 └── application.yml

```

### Features
List main features:
- User registration and login (Spring Security)
- Role-based access (ADMIN, USER)
- Application CRUD
- Document upload (PDFs stored in Oracle BLOBs)
- Many-to-many relationships (Applications ↔ Documents)
- Institution management (universities, employers, lycées, courses)
- Dashboard showing applications and notifications about deadlines
### Future possible enhasements (TODO)
- Dashboard and email notifications for deadlines/interviews
- Advanced search/filter for applications
- Export application data to PDF/Excel
- Integration with external APIs (universities or employers)
- Supervisor management (self-referencing user relationships)

### Technical stack
- Backend: Spring Boot 3.x
- Frontend: Thymeleaf, HTML, CSS, JavaScript
- Database: Oracle (Docker container)
- ORM: JPA / Hibernate
- Authentication: Spring Security (login, session management)
- Validation: @Valid, @NotNull, @Size, etc.
- Build: Maven, executable JAR
- Development tools: DBeaver, Docker

### DB Schema
![ERD diagram](images/ERD.png)
Describe entities and relationships briefly (or refer to ERD):
- Users: 
 - id: Long, primary key, auto-generated
 - username: String, unique, not null (used for login)
 - password: String, not null (hashed via Spring Security)
 - email: String, not null, validated as email
 - firstName, lastName: String, not null
 - birthDate: LocalDate, optional
 - phone: String, optional
 - role_id: foreign key → roles.id (Many-to-One)
Relationships:
 - Supervisors / Supervised Users: Self-referencing Many-to-Many through user_supervisor join table
 - Applications: One-to-Many (User → Application)
 - Documents: One-to-Many (User → Document)
 - Institutions: One-to-Many (User → Institution)
- Roles: ADMIN, USER
 - id: Long, primary key, auto-generated
 - name: String, unique, not null (e.g., ADMIN, USER)
Relationships:
 - Users: One-to-Many (Role → User) — each user is assigned exactly one role.
- Applications: track submissions and deadlines
 - id: Long, primary key, auto-generated
 - title: String, not null — application title
 - description: String (up to 5000 chars) — optional description
 - user_id: Long, foreign key → users.id — owner of the application
 - institution_id: Long, foreign key → institutions.id — related institution
 - applicationType: ENUM (JOB / UNIVERSITY / LYCEE / COURSE)
 - creationDate: LocalDate — automatically set on creation
 - submitDate, submitDeadline, responseDeadline: LocalDate — track submission and response deadlines
 - status: ENUM (PLANNED, SUBMITTED, ACCEPTED, REJECTED, etc.)
 - responseStatus: ENUM (tracks response/result)
 - resultNotes: String (up to 2000 chars) — optional notes
Relationships:
 - Many-to-One: User → Application (each application belongs to a single user)
 - Many-to-One: Institution → Application (application linked to an institution)
 - Many-to-Many: Application ↔ Document via app_doc join table (each application can have multiple documents, and each document can be linked to multiple applications)
- Documents: uploaded PDFs, reusable across applications
 - id: Long, primary key, auto-generated
 - fileName: String, not null — name of the file
 - contentType: String, not null — file MIME type (pdf, image, etc.)
 - uploadDate: LocalDate — date when file was uploaded
 - data: BLOB — file content stored in Oracle BLOB
 - status: ENUM (READY, NOT_READY, IN_PROGRESS)
Relationships:
 - Many-to-One: User → Document (uploader/owner of the document)
 - Many-to-Many: Document ↔ Application via app_doc join table (documents can be attached to multiple applications)
- Institutions: universities, employers, lycées, courses
 - id: Long, primary key, auto-generated
 - name: String, not null — institution name
 - type: ENUM (InstitutionType), not null — JOB / UNIVERSITY / LYCEE / COURSE
 - country: String — country of institution
 - address: String — full address
 - website: String — URL of institution
 - phone: String — contact phone number
 - email: String — contact email, validated with @Email
 - user_id: Long — owner/creator of the institution (optional)
Relationships:
 - One-to-Many: Institution → Application (an institution can have multiple applications linked)
 - Many-to-One: User → Institution (user who added/created the institution)
- Join tables: app_doc
 - application_id: Long, foreign key → applications.id
 - document_id: Long, foreign key → documents.id

### Testing
Currently no testing. 
Future versions may include:
- Unit tests (JUnit)
- Integration tests
- Testcontainers for Oracle

### Prerequisites
- Java 21
- Maven
- Docker (Oracle container running)
- DBeaver or any Oracle client (optional)

# 2. Project setup and run instructions

## 2.1. Run locally (Java & Maven required)
### 2.1.1. Check if Java 21, Maven and Docker installed (skip if already installed)
#### Install Java 21
##### Windows:
- Download JDK 21 (LTS) from:
https://adoptium.net/
- Choose:
Version: 21
Package: JDK
OS: Windows
- Install and check “Set JAVA_HOME”
- Verify:
```
java -version
```

##### macOS (Homebrew):
```
brew install openjdk@21
echo 'export JAVA_H$(/usr/libexec/java_home -v21)' >> ~/.zshrc
source ~/.zshrc
java -version
```

##### Linux (Ubuntu):
```
sudo apt update
sudo apt install -y openjdk-21-jdk
java -version
```

#### Install Maven
##### Windows
- Download Maven:
https://maven.apache.org/download.cgi
- Extract to: C:\Program Files\Apache\Maven
- Add to PATH: C:\Program Files\Apache\Maven\bin
- Verify:
```
mvn -version
```

##### macOS (Homebrew):
```
brew install maven
mvn -version
```

##### Linux (Ubuntu):
```
sudo apt install -y maven
mvn -version
```

#### Install Docker
 Because Oracle XE is too heavy to install manually — Docker is required. 
##### Windows / macOS:
- Download Docker Desktop
https://www.docker.com/products/docker-desktop
- Install and restart your computer if prompted
- Start Docker Desktop
- Verify installation:
```
docker --version
```
##### Linux (Ubuntu):
```
sudo apt update
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl start docker
sudo systemctl enable docker
docker --version
```

### 2.1.2. Run Oracle in Docker
After Java, Maven and Docker installed, let's start Oracle XE 21c with:
- SYS password: Admin123
- Schema: edujob_app_tracker
- Schema password: edujobapptrackerpassword
- Exposes Oracle on localhost:1521

Pull docker image for Oracle db:
```
docker pull gvenzl/oracle-xe:21-slim
```
Run container:
```
docker run -d \
  --name oracle-xe-1 \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=Admin123 \
  -e APP_USER=edujob_app_tracker \
  -e APP_USER_PASSWORD=edujobapptrackerpassword \
  gvenzl/oracle-xe:21-slim
```

Connect with DBeaver / SQL Developer:
Host: localhost
Port: 1521
Service name: XEPDB1
Username: edujob_app_tracker
Password: edujobapptrackerpassword

### 2.1.3. Run app
Clone git repository, open, build and run:
```
git clone https://github.com/koxiperu/edujob_app_track.git
cd edujob-app-tracker
./mvnw clean package -DskipTests
java -jar target/edujob_app_track-0.0.1-SNAPSHOT.jar
```

Open browser: http://localhost:8080

The application will start with a set of mocked data.

**Note:** The `-DskipTests` flag is used to skip the tests, which are currently failing.

## 2.2. Run using docker-compose.yml (alternative to 2.1)
//TODO:

# 3. Usage
- Register a new user or login
- Add a supervisor (if applicable)
- Create applications and attach documents
- Track status, deadlines, and upcoming interviews
- View dashboards and notifications

