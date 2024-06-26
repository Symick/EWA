package nl.solar.app.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nl.solar.app.enums.ProjectStatus;
import nl.solar.app.models.views.ProjectView;

/**
 * Represents a project.
 * 
 * @author Tim Knops
 */
@Setter
@Getter
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(ProjectView.Overview.class)
    private long id;

    @JsonView(ProjectView.Overview.class)
    private String projectName;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonIncludeProperties({ "id", "team" })
    @JsonView(ProjectView.Overview.class)
    private Team team;

    @JsonView(ProjectView.Overview.class)
    private String client;

    @JsonView(ProjectView.Overview.class)
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = "Europe/Amsterdam")
    private Date dueDate;

    @JsonView(ProjectView.Overview.class)
    private String description;

    @Enumerated(EnumType.STRING)
    @JsonView(ProjectView.Overview.class)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Resource> resources = new HashSet<>();

    /**
     * Creates a project with the given parameters.
     * 
     * @param id          the id of the project
     * @param projectName the name of the project
     * @param team        the team that is working on the project
     * @param client      the client of the project
     * @param dueDate     the due date of the project
     * @param status      the status of the project
     * @param resources   the resources of the project
     */
    public Project(long id, String projectName, Team team, String client, Date dueDate, ProjectStatus status,
            Set<Resource> resources) {
        this.id = id;
        this.projectName = projectName;
        this.team = team;
        this.client = client;
        this.dueDate = dueDate;
        this.status = status;
        this.resources = resources;
    }

    public Project(long id) {
        this.id = id;
    }

    public Project() {
    }

    /**
     * Creates a dummy project with the given parameters.
     *
     * @param description the description of the project
     * @return a dummy project
     */
    public static Project createDummyProject(String description) {
        Project project = new Project();

        // Generates a random date between 2022-01-01 and 2026-01-01.
        Date randomDueDate = randomDate(new Date(1640995200000L), new Date(1789568000000L));
        project.setDueDate(randomDueDate);

        ProjectStatus randomStatus;
        if (randomDueDate.before(new Date())) { // If the due date is in the past, the project is completed.
            randomStatus = ProjectStatus.COMPLETED;
        } else { // If the due date is in the future, the project is either upcoming or in
                 // progress.
            double random = Math.random();

            // 40% chance of being upcoming.
            randomStatus = random < 0.4 ? ProjectStatus.UPCOMING : ProjectStatus.IN_PROGRESS;
        }

        project.setStatus(randomStatus);

        // Generate a random client and project name.
        project.setClient("Client " + (int) (Math.random() * 100));
        project.setProjectName("Project " + (int) (Math.random() * 100));
        project.setDescription(description);

        return project;
    }

    /**
     * Generates a random date between the given start and end date.
     * 
     * @param start the start date
     * @param end   the end date
     * @return a random date between the given start and end date
     */
    public static Date randomDate(Date start, Date end) {
        return new Date(start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime())));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof Project project) {
            return this.getId() == project.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
