package taskManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import task_tracker.history.HistoryManager;
import task_tracker.model.Epic;
import task_tracker.model.Progress;
import task_tracker.model.SubTask;
import task_tracker.model.Task;
import task_tracker.service.TaskManager;
import task_tracker.utils.Managers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static task_tracker.model.Progress.IN_PROGRESS;
import static task_tracker.model.Progress.NEW;


class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        task = new Task("Test", "Test", NEW);
        epic = new Epic("Test", "test");
        subTask = new SubTask("Test", "test", Progress.NEW, 1);
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @DisplayName("Успешное создание задачи")
    @Test
    void addNewTask_whenTaskIsValid_shouldAddSuccessfully() {
        initializeTasks();
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @DisplayName("Успешный поиск задачи по id")
    @Test
    void getTaskById_whenTaskExists_shouldReturnTask() {
        initializeTasks();
        final Task taskById = taskManager.getTaskById(task.getId());

        assertNotNull(taskById, "Задача не найдена.");
        assertEquals(task, taskById, "Задачи не совпадают.");
    }

    @DisplayName("Не удалось найти задачу по id")
    @Test
    void getTaskById_whenTaskNotExistOrNull_shouldThrowException() {
        Integer id = null;
        int idNotExist = 100;

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.getTaskById(id),
                "task not found with id = " + id);

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.getTaskById(idNotExist),
                "task not found with id = " + idNotExist);
    }

    @DisplayName("Успешное обновление задачи")
    @Test
    void updateTask_whenPutNewTask_shouldBeUpdated() {
        initializeTasks();
        Task newTask = new Task("Test", "Updated Task", IN_PROGRESS);
        taskManager.updateTask(1, newTask);
        Task taskById = taskManager.getTaskById(1);

        assertEquals(newTask, taskById);
    }

    @DisplayName("Успешное удаление задачи по id")
    @Test
    void deleteTaskById_whenTaskIsExist_shouldDeleteId() {
        initializeTasks();
        taskManager.deleteTaskById(task.getId());

        assertEquals(1, taskManager.getTaskMap().size(), "Неверное количество задач.");
    }

    @Test
    void getAllTasks() {
        initializeTasks();
        List<Task> allTasks = taskManager.getAllTasks();

        assertNotNull(allTasks, "Задачи не возвращаются.");
        assertEquals(2, allTasks.size(), "Неверное количество задач.");
    }


    @Test
    void deleteAllTasks() {
        initializeTasks();
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTaskMap().isEmpty(), "Задачи не удалились");
    }

    private void initializeTasks() {
        task.setId(1);
        Task task2 = new Task("Test2", "Test2", NEW);
        task2.setId(2);
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
    }

    /**
     * EPIC
     */

    @DisplayName("Успешное создание задачи")
    @Test
    void addNewEpic_whenEpicIsValid_shouldAddSuccessfully() {
        epic.setId(1);
        taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Epic не найдена.");
        assertEquals(epic, savedEpic, "Epic не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpic() {
    }

    @Test
    void deleteAllEpics() {
    }

    @DisplayName("Успешное удаление epic по id")
    @Test
    void deleteEpicById() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        subTask.setId(1);

        List<SubTask> subTaskList = epic.getSubTaskList();
        taskManager.deleteEpicById(epic.getId());

        SubTask subtask = taskManager.getSubTaskMap().get(subTask.getId());

//        assertTrue(subTaskList.isEmpty());
        assertNull(subtask);
        assertEquals(0, taskManager.getEpicMap().size());
    }

    @Test
    void getAllEpics() {
    }

    @DisplayName("Успешный поиск Epic по id")
    @Test
    void getEpicById_whenEpicExists_shouldReturnEpic() {
        epic.setId(1);
        taskManager.addNewEpic(epic);
        Epic epicById = taskManager.getEpicById(epic.getId());

        assertNotNull(epicById, "Epic не найден");
        assertEquals(epic, epicById, "Epic не совпадают.");
    }

    @DisplayName("Не удалось найти Epic по id")
    @Test
    void getEpicById_whenEpicNotExistOrNull_shouldThrowException() {
        Integer id = null;
        int idNotExist = 100;

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.getEpicById(id),
                "epic not found with id = " + id);

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.getEpicById(idNotExist),
                "epic not found with id = " + idNotExist);
    }

    @Test
    void updateEpicStatus() {
    }



    /**
     * SUBTASK
     */

    @Test
    void addNewSubTask() {
    }

    @Test
    void getAllSubtaskByEpic() {
    }

    @Test
    void getSubTaskById() {
    }

    @Test
    void updateSubTask() {
    }

    @Test
    void deleteSubTaskById() {
    }

    @Test
    void getAllSubTasks() {
    }

    @Test
    void deleteAllSubTasks() {
    }
}