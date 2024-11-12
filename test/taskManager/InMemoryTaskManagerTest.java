package taskManager;

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
import static task_tracker.model.Progress.*;


class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        task = new Task("Test", "Test", NEW);
        epic = new Epic("Test", "test");
        subTask = new SubTask("Test", "test", Progress.NEW, 1);
        taskManager = Managers.getDefault();

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
        task.setDescription("Updated Task");
        task.setStatus(IN_PROGRESS);
        taskManager.updateTask(task);
        Task taskById = taskManager.getTaskById(1);

        assertEquals(task, taskById);
    }

    @DisplayName("Успешное удаление задачи по id")
    @Test
    void deleteTaskById_whenTaskIsExist_shouldDeleteId() {
        initializeTasks();
        taskManager.deleteTaskById(task.getId());

        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
    }

    @DisplayName("Показать все задачи")
    @Test
    void getAllTasks_shouldReturnAllTask() {
        initializeTasks();
        List<Task> allTasks = taskManager.getAllTasks();

        assertNotNull(allTasks, "Задачи не возвращаются.");
        assertEquals(2, allTasks.size(), "Неверное количество задач.");
    }


    @DisplayName("Успешное удаление всех задач")
    @Test
    void deleteAllTasks_shouldDeleteAllTask() {
        initializeTasks();
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалились");
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

    @DisplayName("Успешное обновление Epic")
    @Test
    void updateEpic_shouldBeUpdate() {
        taskManager.addNewEpic(epic);
        epic.setDescription("update epic");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(1);

        assertEquals(updatedEpic, epic);
    }

    @DisplayName("Успешное удаление всех Epic и Subtask")
    @Test
    void deleteAllEpics() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteAllEpics();

        assertTrue(epic.getSubTaskList().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Подзадачи не удалились");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Задачи не удалились");
    }

    @DisplayName("Успешное удаление epic по id")
    @Test
    void deleteEpicById() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        subTask.setId(1);

        List<SubTask> subTaskList = epic.getSubTaskList();
        taskManager.deleteEpicById(epic.getId());

        assertTrue(subTaskList.isEmpty());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @DisplayName("Успешное получение списка всех Epic")
    @Test
    void getAllEpics() {
        taskManager.addNewEpic(epic);
        List<Epic> allEpics = taskManager.getAllEpics();

        assertNotNull(allEpics);
        assertEquals(1, allEpics.size());
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

    @DisplayName("Успешное обновление статуса у Epic на статус NEW")
    @Test
    void updateEpicStatus_whenSubtaskChangeStatus_shouldUpdateStatusNew() {
        epic.setStatus(IN_PROGRESS);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        taskManager.updateEpicStatus(epic);

        assertEquals(NEW, epic.getStatus());
    }

    @DisplayName("Успешное обновление статуса у Epic на статус DONE")
    @Test
    void updateEpicStatus_whenSubtaskChangeStatus_shouldUpdateStatusDone() {
        epic.setStatus(NEW);
        taskManager.addNewEpic(epic);
        subTask.setStatus(DONE);
        taskManager.addNewSubTask(subTask);
        taskManager.updateEpicStatus(epic);

        assertEquals(DONE, epic.getStatus());
    }

    @DisplayName("Успешное обновление статуса у Epic на статус IN_PROGRESS")
    @Test
    void updateEpicStatus_whenSubtaskChangeStatus_shouldUpdateStatusInProgress() {
        epic.setStatus(NEW);
        taskManager.addNewEpic(epic);
        SubTask subTask2 = new SubTask("Test", "subtask2", NEW, epic.getId());
        subTask.setStatus(DONE);
        taskManager.addNewSubTask(subTask);
        taskManager.addNewSubTask(subTask2);
        taskManager.updateEpicStatus(epic);

        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    /**
     * SUBTASK
     */

    @DisplayName("Успешное создание подзадачи")
    @Test
    void addNewSubTask_whenEpicIsCreated_shouldAddSuccessfully() {
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

    @DisplayName("Успешное получение всех подзадач у Эпика")
    @Test
    void getAllSubtaskByEpic_whenEpicHasSubtask_ShouldReturnListSubtasks() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        List<SubTask> subTaskList = epic.getSubTaskList();

        assertNotNull(subTaskList);
        assertEquals(1, subTaskList.size());

    }

    @DisplayName("Успешный поиск подзадачи по id")
    @Test
    void getSubTaskById_whenSubTaskExists_shouldReturnSubTask() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        SubTask subTaskById = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(subTaskById);
        assertEquals(subTask, subTaskById);
    }

    @DisplayName("Не удалось найти подзадачу по id")
    @Test
    void getSubTaskById_whenTaskNotExistOrNull_shouldThrowException() {
        Integer id = null;
        int idNotExist = 100;

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.getSubTaskById(id),
                "subtask not found with id = " + id);

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.getSubTaskById(idNotExist),
                "subtask not found with id = " + idNotExist);
    }

    @DisplayName("Успешное обновление подзадачи")
    @Test
    void updateSubTask_whenPutNewSubTask_shouldBeUpdated() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        subTask.setId(1);
        subTask.setStatus(IN_PROGRESS);
        subTask.setDescription("Updated SubTask");
        taskManager.updateSubTask(subTask);
        SubTask subTaskById = taskManager.getSubTaskById(1);

        assertEquals(subTask, subTaskById);
    }

    @DisplayName("Успешное удаление подзадачи по id")
    @Test
    void deleteSubTaskById_whenSubTaskIsExist_shouldDeleteId() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteSubTaskById(subTask.getId());

        assertEquals(0, taskManager.getAllSubTasks().size());
    }

    @DisplayName("Показать все подзадачи")
    @Test
    void getAllSubTasks_shouldReturnAllSubTask() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();

        assertNotNull(allSubTasks);
        assertEquals(1, allSubTasks.size());
    }

    @DisplayName("Успешное удаление всех подзадач")
    @Test
    void deleteAllSubTasks_shouldDeleteAllSubTask() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @DisplayName("Поля нельзя изменять после создания")
    @Test
    void testTaskFieldsImmutability() {
        Task task = new Task("Исходная задача", "Описание");
        taskManager.addNewTask(task);

        Task storedTask = taskManager.getTaskById(task.getId());
        storedTask.setName("Измененное имя");

        Task reFetchedTask = taskManager.getTaskById(task.getId());
        assertEquals("Исходная задача", reFetchedTask.getName(),
                "Имя задачи должно оставаться неизменным после изменения в другой ссылке");
    }

    @DisplayName("Задачи с разными ID не должны конфликтовать ")
    @Test
    void testGeneratedAndSpecifiedIdTasksConflict() {
        Task generatedTask = new Task("Генерируемая задача", "Описание");
        taskManager.addNewTask(generatedTask);

        Task specifiedTask = new Task("Задача с ID", "Описание");
        specifiedTask.setId(generatedTask.getId());
        taskManager.addNewTask(specifiedTask);

        assertNotEquals(generatedTask, specifiedTask,
                "Задачи с разными ID не должны конфликтовать");
    }
}