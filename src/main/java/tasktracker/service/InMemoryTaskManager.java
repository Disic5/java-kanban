package tasktracker.service;

import tasktracker.exeption.NotFoundException;
import tasktracker.history.HistoryManager;
import tasktracker.model.Epic;
import tasktracker.model.SubTask;
import tasktracker.model.Task;

import java.util.*;

import static tasktracker.model.Progress.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;

    private final Map<Integer, Task> taskMap = new HashMap<>();

    private final Set<Task> sortedTaskSet = new TreeSet<>(Task::compareByDate);

    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();

    private final Map<Integer, Epic> epicMap = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    /**
     * Task
     */
    public boolean validateOverlapping(Task task) {
        return sortedTaskSet.stream()
                .anyMatch(existingTask -> existingTask.isOverlapping(task));
    }

    @Override
    public void addNewTask(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        if (validateOverlapping(task)) {
            throw new IllegalArgumentException("Задачи пересекается по времени");
        }
        if (task.getId() == null) {
            task.setId(++idCounter);
        }
        sortedTaskSet.add(task);
        taskMap.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(Integer id) {
        if (id != null && taskMap.containsKey(id)) {
            historyManager.add(new Task(taskMap.get(id)));
            return new Task(taskMap.get(id));
        } else {
            throw new NotFoundException("task not found with id = " + id);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            Task oldTask = taskMap.get(task.getId());
            taskMap.put(task.getId(), task);
            sortedTaskSet.remove(oldTask);
            if (validateOverlapping(task)) {
                sortedTaskSet.add(oldTask);
                throw new IllegalArgumentException("Задачи пересекается по времени");
            } else {
                sortedTaskSet.add(task);
                System.out.println("Задача обновлена");
            }
        } else {
            throw new IllegalArgumentException("task not found with id = " + task.getId());
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (id != null && taskMap.containsKey(id)) {
            sortedTaskSet.remove(taskMap.get(id));
            taskMap.remove(id);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("task not found with id = " + id);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
            sortedTaskSet.remove(task);
        }
        taskMap.clear();
    }

    /**
     * Epic
     */
    @Override
    public void addNewEpic(Epic epic) {
        if (validateOverlapping(epic)) {
            throw new IllegalArgumentException("Эпики пересекается по времени");
        }
        if (epic.getId() == null) {
            epic.setId(++idCounter);
        }
        sortedTaskSet.add(epic);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            Epic oldEpic = epicMap.get(epic.getId());
            epicMap.put(epic.getId(), epic);
            sortedTaskSet.remove(oldEpic);
            if (validateOverlapping(epic)) {
                sortedTaskSet.add(oldEpic);
                System.out.println("Epic пересекается с существующими id = " + epic.getId());
            } else {
                sortedTaskSet.add(epic);
                System.out.println("Эпик обновлен");
            }
        } else {
            throw new NotFoundException("epic not found with id = " + epic.getId());
        }
    }

    @Override
    public void deleteAllEpics() {
        if (historyManager != null) {
            for (Epic epic : epicMap.values()) {
                historyManager.remove(epic.getId());
                sortedTaskSet.remove(epic);
            }
        }
        deleteAllSubTasks();
        epicMap.clear();
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (id != null && epicMap.containsKey(id)) {
            Epic epic = epicMap.remove(id);
            sortedTaskSet.remove(epic);
            if (historyManager != null) {
                historyManager.remove(id);
            }
            for (SubTask subtaskId : epic.getSubTaskList()) {
                subTaskMap.remove(subtaskId.getId());
                sortedTaskSet.remove(subtaskId);
            }
            epic.getSubTaskList().clear();
        } else {
            throw new NotFoundException("epic not found with id = " + id);
        }
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
            throw new NotFoundException("epic not found with id = " + id);
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

            if (validateOverlapping(subTask)) {
                throw new IllegalArgumentException("Подзадачи пересекается по времени");
            }
            if (subTask.getStartTime() != null) {
                sortedTaskSet.add(subTask);
            }
            if (subTask.getId() == null) {
                subTask.setId(++idCounter);
            }
            subTaskMap.put(subTask.getId(), subTask);
            Epic epic = epicMap.get(subTask.getEpicId());
            epic.addSubTask(subTask);
            sortedTaskSet.remove(epic);
            if (epic.getStartTime() != null) {
                sortedTaskSet.add(epic);
            }
            updateEpicStatus(epic);
        } else {
            throw new NotFoundException("subTask cannot add without epicId not found epicId = " + subTask.getEpicId());
        }
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        if (id != null && subTaskMap.containsKey(id)) {
            historyManager.add(new SubTask(subTaskMap.get(id)));
            return new SubTask(subTaskMap.get(id));
        } else {
            throw new NotFoundException("subtask not found with id = " + id);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Integer subTaskId = subTask.getId();
        if (subTaskMap.containsKey(subTaskId)) {
            SubTask oldSubtask = subTaskMap.get(subTaskId);
            sortedTaskSet.remove(oldSubtask);
            if (validateOverlapping(subTask)) {
                sortedTaskSet.add(oldSubtask);
                throw new IllegalArgumentException("Подзадачи пересекается по времени");
            } else {
                sortedTaskSet.add(subTask);
                Epic oldEpic = epicMap.get(oldSubtask.getEpicId());
                Epic newEpic = epicMap.get(subTask.getEpicId());
                oldEpic.getSubTaskList().remove(oldSubtask);
                newEpic.addSubTask(subTask);
                updateEpicStatus(newEpic);
                System.out.println("Подзадача обновлена");
            }
        }
        subTaskMap.put(subTaskId, subTask);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        if (id != null && subTaskMap.containsKey(id)) {
            SubTask subtask = subTaskMap.remove(id);
            historyManager.remove(id);
            sortedTaskSet.remove(subtask);
            if (subtask == null) {
                return;
            }
            Epic epic = epicMap.get(subtask.getEpicId());
            epic.getSubTaskList().remove(subtask);
            updateEpicStatus(epic);
        } else {
            throw new NotFoundException("subtask not found with id = " + id);
        }

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
        for (SubTask subTask : subTaskMap.values()) {
            historyManager.remove(subTask.getId());
            sortedTaskSet.remove(subTask);
        }
        subTaskMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTaskSet);
    }

    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public Map<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    public Map<Integer, Epic> getEpicMap() {
        return epicMap;
    }
}
