package task_tracker.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task_tracker.model.Epic;
import task_tracker.model.Progress;
import task_tracker.model.SubTask;
import task_tracker.model.Task;
import task_tracker.service.TaskManager;
import task_tracker.utils.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task_tracker.model.Progress.NEW;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        task = new Task("Test", "Test", NEW);
        epic = new Epic("Test", "test");
        subTask = new SubTask("Test", "test", Progress.NEW, 1);
         Managers.getDefaultHistory().getHistory().clear();
    }



    @DisplayName("Успешное добавление истории при вызову getId()")
    @Test
    void getHistory_whenCallTaskById_shouldAddToHistory() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        taskManager.addNewTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getSubTaskById(subTask.getId());

        assertEquals(6, historyManager.getHistory().size());
    }

    @DisplayName("Размер списка истрории не привышает 10")
    @Test
    void addHistory_whenCallMoreThan10_ShouldAdd10TasksAndRemoveFirstElement() {
        int maxSize = 10;
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(subTask);
        historyManager.add(subTask);
        historyManager.add(subTask);
        historyManager.add(subTask);
        historyManager.add(subTask);
        List<Task> history = historyManager.getHistory();

        assertEquals(maxSize, history.size());
    }
}