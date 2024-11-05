package taskManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import task_tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {


    @BeforeEach
    void setUp() {
    }

//    @Test
//    void addNewTask() {
//        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
//        final int taskId = taskManager.addNewTask(task);
//
//        final Task savedTask = taskManager.getTask(taskId);
//
//        assertNotNull(savedTask, "Задача не найдена.");
//        assertEquals(task, savedTask, "Задачи не совпадают.");
//
//        final List<Task> tasks = taskManager.getTasks();
//
//        assertNotNull(tasks, "Задачи не возвращаются.");
//        assertEquals(1, tasks.size(), "Неверное количество задач.");
//        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
//    }

    @Test
    void getTaskById() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void deleteTaskById() {
    }

    @Test
    void getAllTasks() {
    }

    @Test
    void deleteAllTasks() {
    }

    @Test
    void addNewEpic() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void deleteAllEpics() {
    }

    @Test
    void deleteEpicById() {
    }

    @Test
    void getAllEpics() {
    }

    @Test
    void getEpicById() {
    }

    @Test
    void updateEpicStatus() {
    }

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