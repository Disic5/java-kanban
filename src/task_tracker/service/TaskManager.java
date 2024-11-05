package task_tracker.service;

import task_tracker.model.Epic;
import task_tracker.model.SubTask;
import task_tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static task_tracker.model.Progress.*;

public class TaskManager {
    private int idCounter = 0;
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();

    /**
     * Task
     */
    public void addNewTask(Task task) {
        task.setId(++idCounter);
        taskMap.put(task.getId(), task);
    }

    public Task getTaskById(Integer id) {
        return taskMap.getOrDefault(id, null);
    }

    public void updateTask(Integer id, Task task) {
        if (taskMap.containsKey(id)) {
            task.setId(id);
            taskMap.replace(id, task);
        }
    }

    public void deleteTaskById(Integer id) {
        taskMap.remove(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    /**
     * Epic
     */
    public void addNewEpic(Epic epic) {
        epic.setId(++idCounter);
        epicMap.put(epic.getId(), epic);
    }

    public void updateEpic(Integer id, Epic epic) {
        if (epicMap.containsKey(id)) {
            epic.setId(id);
            epicMap.put(id, epic);
        }
    }

    public void deleteAllEpics() {
        epicMap.clear();
        deleteAllSubTasks();
    }

    public void deleteEpicById(Integer id) {
        Epic epic = epicMap.remove(id);
        for (SubTask subtaskId : epic.getSubTaskList()){
            subTaskMap.remove(subtaskId.getId());
        }

    }


    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public Epic getEpicById(Integer id) {
        return epicMap.get(id);
    }

    public void updateEpicStatus(Epic epic) {
        int subtasksNewStatusCounter = 0;
        int subtasksDoneStatusCounter = 0;
        for (SubTask subtask : epic.getSubTaskList()) {
            if (NEW == subtask.getStatus()) {
                subtasksNewStatusCounter++;
            } else if (DONE == subtask.getStatus()) {
                subtasksDoneStatusCounter++;
            } else break;
        }
        if (epic.getSubTaskList().isEmpty() || subtasksNewStatusCounter == epic.getSubTaskList().size()) {
            epic.setStatus(NEW);
        } else if (subtasksDoneStatusCounter == epic.getSubTaskList().size()) {
            epic.setStatus(DONE);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    /***
     * SubTask
     * */
    public void addNewSubTask(SubTask subTask) {
        if (epicMap.containsKey(subTask.getEpicId())) {
            subTask.setId(++idCounter);
            subTaskMap.put(subTask.getId(), subTask);
            Epic epic = epicMap.get(subTask.getEpicId());
            epic.addSubTask(subTask);
            updateEpicStatus(epic);
        }
    }

    public List<SubTask> getAllSubtaskByEpic(Epic epic) {
        return epic.getSubTaskList();
    }

    public SubTask getSubTaskById(Integer id) {
        return subTaskMap.get(id);
    }

    public void updateSubTask(Integer id, SubTask subTask) {
        subTask.setId(id);
        Integer subTaskId = subTask.getId();
        if (subTaskMap.containsKey(subTaskId)) {
            SubTask oldSubtask = subTaskMap.get(subTaskId);
            Epic oldEpic = epicMap.get(oldSubtask.getEpicId());
            Epic newEpic = epicMap.get(subTask.getEpicId());
            if (!oldEpic.equals(newEpic)) {
                oldEpic.getSubTaskList().remove(oldSubtask);
            } else{
                newEpic.addSubTask(subTask);
                updateEpicStatus(newEpic);
            }
        }
        subTask.setId(id);
        subTaskMap.put(id, subTask);
    }

    public void deleteSubTaskById(Integer id) {
        SubTask subtask = subTaskMap.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epicMap.get(subtask.getEpicId());
        epic.getSubTaskList().remove(subtask);
        updateEpicStatus(epic);
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    public void deleteAllSubTasks() {
        for (Epic epic : epicMap.values()) {
            epic.getSubTaskList().clear();
            updateEpicStatus(epic);
        }
        subTaskMap.clear();
    }

    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public Map<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    public Map<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }
}
