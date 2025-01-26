package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.exeption.NotFoundException;
import tasktracker.history.InMemoryHistoryManager;
import tasktracker.model.Epic;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static tasktracker.model.Progress.*;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @DisplayName("Успешный поиск задачи по id")
    @Test
    void getTaskById_whenTaskExists_shouldReturnTask() {
        Task task = new Task(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        Optional<Task> taskById = taskManager.getTaskById(task.getId());

        assertTrue(taskById.isPresent());
        assertEquals(task, taskById.get(), "Задача должна быть доступна по ID");
    }

    @DisplayName("Не удалось найти задачу по id")
    @Test
    void getTaskById_whenTaskNotExistOrNull_shouldThrowException() {
        Integer id = null;
        int idNotExist = 100;

        assertThrows(NotFoundException.class,
                () -> {
                    Optional<Task> taskById = taskManager.getTaskById(id);
                    if (taskById.isEmpty()) {
                        throw new NotFoundException("task not found with id = " + id);
                    }
                });

        assertThrows(NotFoundException.class,
                () -> taskManager.getTaskById(idNotExist),
                "task not found with id = " + idNotExist);
    }

    @DisplayName("Показать все задачи")
    @Test
    void getAllTasks_shouldReturnAllTask() {
        Task task = new Task(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        Task task2 = new Task(2, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
        List<Task> allTasks = taskManager.getAllTasks();

        assertNotNull(allTasks, "Задачи не возвращаются.");
        assertEquals(2, allTasks.size(), "Неверное количество задач.");
    }

    /**
     * EPIC
     */

    @DisplayName("Успешное получение списка всех Epic")
    @Test
    void getAllEpics() {
        Epic epic = new Epic("Epic", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addNewEpic(epic);
        List<Epic> allEpics = taskManager.getAllEpics();

        assertNotNull(allEpics);
        assertEquals(1, allEpics.size());
    }

    @DisplayName("Успешный поиск Epic по id")
    @Test
    void getEpicById_whenEpicExists_shouldReturnEpic() {
        Epic epic = new Epic("Epic", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        epic.setId(1);
        taskManager.addNewEpic(epic);
        Optional<Epic> epicById = taskManager.getEpicById(epic.getId());

        assertTrue(epicById.isPresent(), "epic not found.");
        assertNotNull(epicById, "Epic не найден");
        assertEquals(epic, epicById.get(), "Epic не совпадают.");
    }

    @DisplayName("Не удалось найти Epic по id")
    @Test
    void getEpicById_whenEpicNotExistOrNull_shouldThrowException() {
        Integer id = null;
        int idNotExist = 100;

        assertThrows(NotFoundException.class,
                () -> taskManager.getEpicById(id),
                "epic not found with id = " + id);

        assertThrows(NotFoundException.class,
                () -> taskManager.getEpicById(idNotExist),
                "epic not found with id = " + idNotExist);
    }

    @DisplayName("Успешное обновление статуса у Epic на статус NEW")
    @Test
    void updateEpicStatus_whenSubtaskChangeStatus_shouldUpdateStatusNew() {
        Epic epic = new Epic("Epic", "Description", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), 1);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        taskManager.updateEpicStatus(epic);

        assertEquals(NEW, epic.getStatus());
    }

    @DisplayName("Успешное обновление статуса у Epic на статус DONE")
    @Test
    void updateEpicStatus_whenSubtaskChangeStatus_shouldUpdateStatusDone() {
        Epic epic = new Epic("Epic", "Description", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), 1);
        epic.setStatus(NEW);
        taskManager.addNewEpic(epic);
        subTask.setStatus(DONE);
        taskManager.addNewSubTask(subTask);
        taskManager.updateEpicStatus(epic);

        assertEquals(DONE, epic.getStatus());
    }

    /**
     * SUBTASK
     */

    @DisplayName("Успешное получение всех подзадач у Эпика")
    @Test
    void getAllSubtaskByEpic_whenEpicHasSubtask_ShouldReturnListSubtasks() {
        Epic epic = new Epic("Epic", "Description", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), 1);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        List<SubTask> subTaskList = epic.getSubTaskList();

        assertNotNull(subTaskList);
        assertEquals(1, subTaskList.size());

    }

    @DisplayName("Успешный поиск подзадачи по id")
    @Test
    void getSubTaskById_whenSubTaskExists_shouldReturnSubTask() {
        Epic epic = new Epic("Epic", "Description", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), 1);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        Optional<SubTask> subTaskById = taskManager.getSubTaskById(subTask.getId());

        assertTrue(subTaskById.isPresent(), "subTask not found.");
        assertNotNull(subTaskById);
        assertEquals(subTask, subTaskById.get());
    }

    @DisplayName("Не удалось найти подзадачу по id")
    @Test
    void getSubTaskById_whenTaskNotExistOrNull_shouldThrowException() {
        Integer id = null;
        int idNotExist = 100;

        assertThrows(NotFoundException.class,
                () -> taskManager.getSubTaskById(id),
                "subtask not found with id = " + id);

        assertThrows(NotFoundException.class,
                () -> taskManager.getSubTaskById(idNotExist),
                "subtask not found with id = " + idNotExist);
    }


    @DisplayName("Показать все подзадачи")
    @Test
    void getAllSubTasks_shouldReturnAllSubTask() {
        Epic epic = new Epic("Epic", "Description", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), 1);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();

        assertNotNull(allSubTasks);
        assertEquals(1, allSubTasks.size());
    }

    @DisplayName("Поля нельзя изменять после создания")
    @Test
    void testTaskFieldsImmutability() {
        Task task = new Task(1, "Исходная задача", "Описание", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addNewTask(task);
        String taskName = task.getName();
        Optional<Task> taskById = taskManager.getTaskById(task.getId());
        assertTrue(taskById.isPresent());
        Task renamed = taskById.get();
        renamed.setName("Измененное имя");

        assertNotEquals(renamed.getName(), taskName,
                "Имя задачи должно оставаться неизменным после изменения в другой ссылке");
    }

    @DisplayName("Задачи с разными ID не должны конфликтовать ")
    @Test
    void testGeneratedAndSpecifiedIdTasksConflict() {
        Task generatedTask = new Task(1, "Генерируемая задача", "Описание", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addNewTask(generatedTask);

        Task specifiedTask = new Task(2, "Задача с ID", "Описание", IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.addNewTask(specifiedTask);

        assertNotEquals(generatedTask, specifiedTask,
                "Задачи с разными ID не должны конфликтовать");
    }

    @DisplayName("Проверка Эпика на граничные условия")
    @Test
    void EpicStatusCalculation() {
        Epic epic = new Epic(1, "Epic Test", "Description", NEW, Duration.ZERO, null);
        taskManager.addNewEpic(epic);

        // Создание подзадач
        SubTask subtask1 = new SubTask(1, "Subtask 1", "Description", NEW, Duration.ZERO, null, epic.getId());
        SubTask subtask2 = new SubTask(2, "Subtask 2", "Description", NEW, Duration.ZERO, null, epic.getId());
        taskManager.addNewSubTask(subtask1);
        taskManager.addNewSubTask(subtask2);

        // Граничное условие a: Все подзадачи со статусом NEW
        assertEquals(NEW, epic.getStatus(), "Все подзадачи NEW - статус эпика должен быть NEW");

        // Граничное условие b: Все подзадачи со статусом DONE
        subtask1.setStatus(DONE);
        subtask2.setStatus(DONE);
        taskManager.updateSubTask(subtask1);
        taskManager.updateSubTask(subtask2);
        assertEquals(DONE, epic.getStatus(), "Все подзадачи DONE - статус эпика должен быть DONE");

        // Граничное условие c: Подзадачи со статусами NEW и DONE
        subtask1.setStatus(NEW);
        taskManager.updateSubTask(subtask1);
        assertEquals(IN_PROGRESS, epic.getStatus(), "Подзадачи со статусами NEW и DONE - статус эпика должен быть IN_PROGRESS");

        // Граничное условие d: Подзадачи со статусом IN_PROGRESS
        subtask1.setStatus(IN_PROGRESS);
        subtask2.setStatus(IN_PROGRESS);
        taskManager.updateSubTask(subtask1);
        taskManager.updateSubTask(subtask2);
        assertEquals(IN_PROGRESS, epic.getStatus(), "Все подзадачи IN_PROGRESS - статус эпика должен быть IN_PROGRESS");
    }
}