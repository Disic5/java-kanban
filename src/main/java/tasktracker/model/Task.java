package tasktracker.model;

import tasktracker.fileservice.TaskType;

import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private Progress status;

    public Task(Integer id, String name, String description, Progress status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Progress status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        setStatus(Progress.NEW);
    }

    public Task(Task copyTask) {
        this.id = copyTask.id;
        this.name = copyTask.name;
        this.description = copyTask.getDescription();
        this.status = copyTask.getStatus();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Progress getStatus() {
        return status;
    }

    public void setStatus(Progress status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s%n", id, TaskType.TASK, name, description, status);
    }
}
