package tasktracker.service;

import tasktracker.model.Epic;
import tasktracker.model.SubTask;
import tasktracker.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    void addNewTask(Task task);

    Optional<Task> getTaskById(Integer id);

    void updateTask(Task task);

    void deleteTaskById(Integer id);

    List<Task> getAllTasks();

    void deleteAllTasks();

    void addNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteAllEpics();

    void deleteEpicById(Integer id);

    List<Epic> getAllEpics();

    Optional<Epic> getEpicById(Integer id);

    void updateEpicStatus(Epic epic);

    void addNewSubTask(SubTask subTask);

    List<SubTask> getAllSubtaskByEpic(Integer id);

    Optional<SubTask> getSubTaskById(Integer id);

    void updateSubTask(SubTask subTask);

    void deleteSubTaskById(Integer id);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
