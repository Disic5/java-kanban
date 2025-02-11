package tasktracker.model;

import tasktracker.fileservice.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<SubTask> subTaskList;

    private LocalDateTime endTime;


    public Epic(Integer id, String name, String description, Progress status) {
        super(id, name, description, status);
        subTaskList = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description, Progress status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        subTaskList = new ArrayList<>();
    }

    public Epic(String name, String description, Progress status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        subTaskList = new ArrayList<>();
    }

    public Epic(String name, String description, Progress status) {
        super(name, description, status, Duration.ZERO, null);
        subTaskList = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTaskList = new ArrayList<>();
    }

    public Epic(Epic copyEpic) {
        super(copyEpic);
        subTaskList = copyEpic.subTaskList;
        duration = copyEpic.duration;
        startTime = copyEpic.startTime;
        endTime = copyEpic.endTime;
    }

    public List<SubTask> getSubTaskList() {
        if (subTaskList == null) {
            subTaskList = new ArrayList<>();
        }
        recalculateDurationAndTime();
        return subTaskList;
    }

    private void recalculateDurationAndTime() {
        if (subTaskList.isEmpty()) {
            duration = Duration.ZERO;
            startTime = null;
            endTime = null;
            return;
        }
        // Продолжительность всех подзадач
        duration = subTaskList.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        //Определяем минимальную подзадачу по времени
        startTime = subTaskList.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        endTime = subTaskList.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        setDuration(duration);
        setStartTime(startTime);
        setEndTime(endTime);

    }

    public void addSubTask(SubTask subTask) {
        if (subTaskList == null) {
            subTaskList = new ArrayList<>();
        }
        subTaskList.add(subTask);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s%n",
                getId(),
                TaskType.EPIC,
                getName(),
                getDescription(),
                getStatus(),
                getDuration(),
                getStartTime());
    }
}

