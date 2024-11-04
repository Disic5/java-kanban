package task_tracker.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<SubTask> subTaskList;

    public Epic(String name, String description) {
        super(name, description);
        subTaskList = new ArrayList<>();
        this.setStatus(Progress.NEW);
    }


    public List<SubTask> getSubTaskList() {
        return subTaskList;
    }

    public void addSubTask(SubTask subTask) {
        subTaskList.add(subTask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status= " + getStatus() + ", " +
                '}' + "\n";
    }
}

