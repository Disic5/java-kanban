package tasktracker.service;

import tasktracker.history.HistoryManager;
import tasktracker.model.Epic;
import tasktracker.model.SubTask;
import tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tasktracker.model.Progress.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;

    private final Map<Integer, Task> taskMap = new HashMap<>();

    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();

    private final Map<Integer, Epic> epicMap = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
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
            historyManager.add(new Task(taskMap.get(id)));
            return new Task(taskMap.get(id));
        } else {
            throw new IllegalArgumentException("task not found with id = " + id);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.replace(task.getId(), task);
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        taskMap.remove(id);
        historyManager.remove(id);
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
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.put(epic.getId(), epic);
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
        historyManager.remove(id);
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
            historyManager.add(new Epic(epicMap.get(id)));
            return new Epic(epicMap.get(id));
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
            historyManager.add(new SubTask(subTaskMap.get(id)));
            return new SubTask(subTaskMap.get(id));
        } else {
            throw new IllegalArgumentException("subtask not found with id = " + id);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Integer subTaskId = subTask.getId();
        if (subTaskMap.containsKey(subTaskId)) {
            SubTask oldSubtask = subTaskMap.get(subTaskId);
            Epic oldEpic = epicMap.get(oldSubtask.getEpicId());
            Epic newEpic = epicMap.get(subTask.getEpicId());
            oldEpic.getSubTaskList().remove(oldSubtask);
            newEpic.addSubTask(subTask);
            updateEpicStatus(newEpic);
        }
        subTaskMap.put(subTaskId, subTask);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        SubTask subtask = subTaskMap.remove(id);
        historyManager.remove(id);
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
