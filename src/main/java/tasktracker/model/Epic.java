package tasktracker.model;

import tasktracker.fileservice.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<SubTask> subTaskList;

    public Epic(Integer id, String name, String description, Progress status) {
        super(id, name, description, status);
        subTaskList = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTaskList = new ArrayList<>();
    }

    public Epic(Epic copyEpic) {
        super(copyEpic);
        subTaskList = new ArrayList<>(copyEpic.subTaskList);
    }


    public List<SubTask> getSubTaskList() {
        return subTaskList;
    }

    public void addSubTask(SubTask subTask) {
        subTaskList.add(subTask);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s%n", getId(), TaskType.EPIC, getName(), getDescription(), getStatus());
    }
}

