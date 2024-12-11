package tasktracker.model;

import tasktracker.fileservice.TaskType;

public class SubTask extends Task {
    private final Integer epicId;

    public SubTask(int id, String name, String description, Progress status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Progress status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(SubTask copySubTask) {
        super(copySubTask);
        this.epicId = copySubTask.epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d%n", getId(), TaskType.SUBTASK, getName(), getDescription(), getStatus(), getEpicId());
    }
}

