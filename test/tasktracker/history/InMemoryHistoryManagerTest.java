package tasktracker.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.model.Epic;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;
import tasktracker.utils.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tasktracker.model.Progress.NEW;

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

        assertEquals(3, taskManager.getHistory().size());
    }

    @DisplayName("В истории хранятся только уникальные элементы")
    @Test
    void addHistory_whenAddDuplicate_ShouldAddUniqTasks() {
        int size = 3;
        epic.setId(1);
        subTask.setId(2);
        task.setId(3);
        addTasks(epic, 5);
        addTasks(subTask, 5);
        addTasks(task, 6);
        List<Task> history = historyManager.getHistory();
        assertEquals(size, history.size());
    }

    @DisplayName("Синхронное удаление задачи и задачи из истории")
    @Test
    void removeHistory_whenRemoveTask_shouldRemoveHistory() {
        Epic epic2 = new Epic("Test", "test2");
        epic2.setId(2);
        task.setId(3);
        subTask.setId(4);
        historyManager.add(task);
        historyManager.add(epic2);
        historyManager.remove(task.getId());

        assertEquals(1, historyManager.getHistory().size());
    }

    @DisplayName("Удаление задачи из истории по Id")
    @Test
    void removeHistoryById_shouldRemoveHistory() {
        epic.setId(1);
        historyManager.add(epic);
        historyManager.remove(epic.getId());

        assertTrue( historyManager.getHistory().isEmpty());
    }


    @DisplayName("История не ограничена размером")
    @Test
    void addHistory_whenSizeUnlimited_shouldBeSuccess() {
        int size = 20;
        addTasksWithId(task, size);
        assertEquals(size, historyManager.getHistory().size());
    }

    @DisplayName("Получение история по id")
    @Test
    void getHistoryById_shouldReturnTaskById() {
        addTasksWithId(task, 7);
        Task historyTasks = historyManager.getHistoryTasks(5);
        assertEquals(5, historyTasks.getId());

    }

    private void addTasks(Task task, int count) {
        for (int i = 0; i < count; i++) {
            historyManager.add(task);
        }
    }

    private void addTasksWithId(Task task, int count) {
        for (int i = 1; i <= count; i++) {
            task.setId(i);
            historyManager.add(new Task(task));
        }
    }

}