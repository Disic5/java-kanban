package tasktracker.model;

import tasktracker.fileservice.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private Progress status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(Integer id, String name, String description, Progress status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Progress status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

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
        this.duration = copyTask.getDuration();
        this.startTime = copyTask.getStartTime();
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public static int compareByDate(Task t1, Task t2) {
        if (t1.getStartTime() == null && t2.getStartTime() == null) return Integer.compare(t1.getId(), t2.getId());
        if (t1.getStartTime() == null) return 1;
        if (t2.getStartTime() == null) return -1;


        int dateComparison = t1.getStartTime().compareTo(t2.getStartTime());
        if (dateComparison != 0) {
            return dateComparison;
        }
        // Если время начала одинаковое, сравниваем по ID
        return Integer.compare(t1.getId(), t2.getId());
    }

    public boolean isOverlapping(Task task) {
        if (task.startTime == null) {
            return false;
        }
        return this.startTime.isBefore(task.getEndTime())
                && task.getStartTime().isBefore(getEndTime());
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
        return String.format("%d,%s,%s,%s,%s,%s,%s%n",
                id,
                TaskType.TASK,
                name,
                description,
                status,
                duration,
                startTime);
    }
}
