package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.model.Epic;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasktracker.model.Progress.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    /**
     * Task
     */

    @DisplayName("Успешное создание задачи")
    @Test
    void addNewTask_whenTaskIsValid_shouldAddSuccessfully() {
        Task task = new Task("Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addNewTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @DisplayName("Успешное обновление задачи")
    @Test
    void updateTask_whenPutNewTask_shouldBeUpdated() {
        Task task = new Task(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addNewTask(task);
        task.setDescription("Updated Task");
        task.setStatus(IN_PROGRESS);
        taskManager.updateTask(task);
        Task taskById = taskManager.getTaskById(1);

        assertEquals("Updated Task", taskById.getDescription());
    }

    @DisplayName("Успешное удаление задачи по id синхронно с историей")
    @Test
    void deleteTaskById_whenTaskIsExist_shouldDeleteId() {
        initializeAndAddTasks();
        taskManager.getTaskById(1);
        taskManager.deleteTaskById(1);

        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное удаление всех задач")
    @Test
    void deleteAllTasks_shouldDeleteAllTask() {
        initializeAndAddTasks();
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалились");
        assertTrue(taskManager.getHistory().isEmpty());
    }

    /**
     * Epic
     */
    @DisplayName("Успешное создание эпика")
    @Test
    void addNewEpic_whenTaskIsValid_shouldAddSuccessfully() {
        Epic epic = new Epic("Epic", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addNewEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @DisplayName("Успешное обновление Epic")
    @Test
    void updateEpic_shouldBeUpdate() {
        Epic epic = new Epic(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addNewEpic(epic);
        epic.setDescription("update epic");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(1);

        assertEquals("update epic", updatedEpic.getDescription());
    }

    @DisplayName("Успешное обновление статуса у Epic на статус IN_PROGRESS")
    @Test
    void updateEpicStatus_whenSubtaskChangeStatus_shouldUpdateStatusInProgress() {
        Epic epic = new Epic("Epic", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), 1);
        taskManager.addNewEpic(epic);
        SubTask subTask2 = new SubTask(2, "SubTask", "Description", DONE, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        taskManager.addNewSubTask(subTask);
        taskManager.addNewSubTask(subTask2);
        taskManager.updateEpicStatus(epic);

        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    @DisplayName("Успешное удаление всех Epic и Subtask")
    @Test
    void deleteAllEpics() {
        Epic epic = new Epic(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Epic epic2 = new Epic(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.addNewEpic(epic);
        taskManager.addNewEpic(epic2);

        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3), 1);
        SubTask subTask2 = new SubTask(2, "Subtask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2), 1);
        taskManager.addNewSubTask(subTask);
        taskManager.addNewSubTask(subTask2);

        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.deleteAllEpics();

        assertTrue(epic.getSubTaskList().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Подзадачи не удалились");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Задачи не удалились");
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное удаление epic по id синхронно с историей")
    @Test
    void deleteEpicById() {
        Epic epic = new Epic(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), 1);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        subTask.setId(1);

        List<SubTask> subTaskList = epic.getSubTaskList();
        taskManager.getEpicById(epic.getId());
        taskManager.deleteEpicById(epic.getId());

        assertTrue(subTaskList.isEmpty());
        assertEquals(0, taskManager.getAllEpics().size());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    /**
     * SUBTASK
     */
    @DisplayName("Успешное создание подзадачи")
    @Test
    void addNewSubTask_whenEpicIsCreated_shouldAddSuccessfully() {
        Epic epic = new Epic(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), 1);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");
    }

    @DisplayName("Успешное обновление подзадачи")
    @Test
    void updateSubTask_whenPutNewSubTask_shouldBeUpdated() {
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        taskManager.addNewSubTask(subTask);
        subTask.setId(1);
        subTask.setStatus(IN_PROGRESS);
        subTask.setDescription("Updated SubTask");
        taskManager.updateSubTask(subTask);
        SubTask subTaskById = taskManager.getSubTaskById(1);

        assertEquals("Updated SubTask", subTaskById.getDescription());
    }

    @DisplayName("Успешное удаление подзадачи по id синхронно с историей")
    @Test
    void deleteSubTaskById_whenSubTaskIsExist_shouldDeleteId() {
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteSubTaskById(subTask.getId());

        assertEquals(0, taskManager.getAllSubTasks().size());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное удаление всех подзадач")
    @Test
    void deleteAllSubTasks_shouldDeleteAllSubTask() {
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        SubTask subTask2 = new SubTask(2, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        taskManager.addNewSubTask(subTask);
        taskManager.addNewSubTask(subTask2);
        taskManager.deleteAllSubTasks();

        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    private void initializeAndAddTasks() {
        Task task = new Task("Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
    }
}
