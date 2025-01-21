package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.model.Progress;
import tasktracker.model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Test", "test", Progress.NEW);
    }

    @DisplayName("экземпляры класса Task равны друг другу")
    @Test
    void getTaskId_whenIdAreEqual_shouldBeEqual() {
        int id = 1;
        task.setId(id);
        Task task1 = new Task("Test", "test", Progress.NEW);
        task1.setId(id);

        assertEquals(task1.getId(), task.getId(), "Экземпляры Task с одинаковым id должны быть равны");
    }
}
