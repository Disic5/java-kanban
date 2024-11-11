package task_tracker.service;

import task_tracker.model.Epic;
import task_tracker.model.SubTask;
import task_tracker.model.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    void addNewTask(Task task);

    Task getTaskById(Integer id);

    void updateTask(Integer id, Task task);

    void deleteTaskById(Integer id);

    List<Task> getAllTasks();

    void deleteAllTasks();

    void addNewEpic(Epic epic);

    void updateEpic(Integer id, Epic epic);

    void deleteAllEpics();

    void deleteEpicById(Integer id);

    List<Epic> getAllEpics();

    Epic getEpicById(Integer id);

    void updateEpicStatus(Epic epic);

    void addNewSubTask(SubTask subTask);

    List<SubTask> getAllSubtaskByEpic(Integer id);

    SubTask getSubTaskById(Integer id);

    void updateSubTask(Integer id, SubTask subTask);

    void deleteSubTaskById(Integer id);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

    Map<Integer, Task> getTaskMap();

    Map<Integer, Epic> getEpicMap();

    Map<Integer, SubTask> getSubTaskMap();
}
