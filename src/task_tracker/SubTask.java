package task_tracker;

public class SubTask extends Task {
    private final Integer epicId;

    public SubTask(String name, String description, Progress status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status= " + getStatus() + ", " +
                "epicId=" + epicId +
                '}' + "\n";
    }
}

