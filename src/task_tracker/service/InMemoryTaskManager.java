package task_tracker.service;

import task_tracker.history.HistoryManager;
import task_tracker.model.Epic;
import task_tracker.model.SubTask;
import task_tracker.model.Task;
import task_tracker.utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static task_tracker.model.Progress.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;

    private final Map<Integer, Task> taskMap = new HashMap<>();

    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();

    private final Map<Integer, Epic> epicMap = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    /**
     * Getters
     */
    @Override
    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    @Override
    public Map<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    @Override
    public Map<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    /**
     * Task
     */
    @Override
    public void addNewTask(Task task) {
        task.setId(++idCounter);
        taskMap.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(Integer id) {
        if (id != null && taskMap.containsKey(id)) {
            historyManager.add(taskMap.get(id));
            return taskMap.get(id);
        } else {
            throw new IllegalArgumentException("task not found with id = " + id);
        }
    }

    @Override
    public void updateTask(Integer id, Task task) {
        if (taskMap.containsKey(id)) {
            task.setId(id);
            taskMap.replace(id, task);
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        taskMap.remove(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public void deleteAllTasks() {
        taskMap.clear();
    }

    /**
     * Epic
     */
    @Override
    public void addNewEpic(Epic epic) {
        epic.setId(++idCounter);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Integer id, Epic epic) {
        if (epicMap.containsKey(id)) {
            epic.setId(id);
            epicMap.put(id, epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
        epicMap.clear();
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = epicMap.remove(id);
        for (SubTask subtaskId : epic.getSubTaskList()) {
            subTaskMap.remove(subtaskId.getId());
        }
        epic.getSubTaskList().clear();
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (id != null && epicMap.containsKey(id)) {
            historyManager.add(epicMap.get(id));
            return epicMap.get(id);
        } else {
            throw new IllegalArgumentException("epic not found with id = " + id);
        }
    }

    @Override
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
    @Override
    public void addNewSubTask(SubTask subTask) {
        if (epicMap.containsKey(subTask.getEpicId())) {
            subTask.setId(++idCounter);
            subTaskMap.put(subTask.getId(), subTask);
            Epic epic = epicMap.get(subTask.getEpicId());
            epic.addSubTask(subTask);
            updateEpicStatus(epic);
        }
    }

    @Override
    public List<SubTask> getAllSubtaskByEpic(Integer id) {
        Epic epic = epicMap.get(id);
        return epic.getSubTaskList();
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        if (id != null && subTaskMap.containsKey(id)) {
            historyManager.add(subTaskMap.get(id));
            return subTaskMap.get(id);
        } else {
            throw new IllegalArgumentException("subtask not found with id = " + id);
        }
    }


    @Override
    public void updateSubTask(Integer id, SubTask subTask) {
        subTask.setId(id);
        Integer subTaskId = subTask.getId();
        if (subTaskMap.containsKey(subTaskId)) {
            SubTask oldSubtask = subTaskMap.get(subTaskId);
            Epic oldEpic = epicMap.get(oldSubtask.getEpicId());
            Epic newEpic = epicMap.get(subTask.getEpicId());
            oldEpic.getSubTaskList().remove(oldSubtask);
            newEpic.addSubTask(subTask);
            updateEpicStatus(newEpic);
        }
        subTask.setId(id);
        subTaskMap.put(id, subTask);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        SubTask subtask = subTaskMap.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epicMap.get(subtask.getEpicId());
        epic.getSubTaskList().remove(subtask);
        updateEpicStatus(epic);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epicMap.values()) {
            epic.getSubTaskList().clear();
            updateEpicStatus(epic);
        }
        subTaskMap.clear();
    }
}
