package task_tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
        } else {
            System.out.println("Не верный Id");
        }
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
        Epic epic = epicMap.get(id);
        if (epic.getSubTaskList() != null) {
            System.out.println(epic.getSubTaskList());
            epic.getSubTaskList().clear();
        }
        epicMap.remove(id);
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public Epic getEpicById(Integer id) {
        return epicMap.getOrDefault(id, null);
    }

    public void updateEpicStatus(Epic epic) {
        List<SubTask> subTaskList = epic.getSubTaskList();
        if (subTaskList != null) {
            for (SubTask subTask : subTaskList) {
                if (!subTask.getStatus().equals(Progress.DONE)) {
                    epic.setStatus(Progress.IN_PROGRESS);
                    break;
                } else {
                    epic.setStatus(Progress.DONE);
                }
            }
        }
    }

    /***
     * SubTask
     * */
    public void addNewSubTask(SubTask subTask) {
        if (epicMap.containsKey(subTask.getEpicId())) {
            subTask.setId(++idCounter);
            subTaskMap.put(subTask.getId(), subTask);
            epicMap.get(subTask.getEpicId()).addSubTask(subTask);
        }
    }

    public List<SubTask> getAllSubtaskByEpic(Epic epic) {
        return epic.getSubTaskList();
    }

    public SubTask getSubTaskById(Integer id) {
        return subTaskMap.getOrDefault(id, null);
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
            } else {
                newEpic.addSubTask(subTask);
            }
        }
        subTask.setId(id);
        subTaskMap.put(id, subTask);
    }

    public void deleteSubTaskById(Integer id) {
        if (subTaskMap.containsKey(id)) {
            subTaskMap.remove(id);
        } else {
            System.out.println("Не верный Id");
        }
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    public void deleteAllSubTasks() {
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
