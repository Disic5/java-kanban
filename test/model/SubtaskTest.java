package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.service.TaskManager;
import tasktracker.utils.Managers;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    private SubTask subTask;
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        subTask = new SubTask("Test", "test", Progress.NEW, 1);
        taskManager = Managers.getDefault();
    }

    @DisplayName("Наследники класса Task равны друг другу")
    @Test
    void getSubTaskId_whenIdAreEqual_shouldBeEqual() {
        int id = 1;
        subTask.setId(id);
        SubTask subTask1 = new SubTask("Test", "test", Progress.NEW, 1);
        subTask1.setId(id);

        assertEquals(subTask.getId(), subTask1.getId(), "Экземпляры SubTask с одинаковым id должны быть равны");
    }

    @DisplayName("Subtask не может быть без Epic")
    @Test
    void addSubtask_putSubtaskWithoutEpic_shouldBeFailed() {
        subTask.setId(1);
        taskManager.addNewSubTask(subTask);

        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }
}
