package epic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task_tracker.model.Epic;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("Test", "test");
    }

    @DisplayName("Наследники класса Task равны друг другу")
    @Test
    void getEpicId_whenIdAreEqual_shouldBeEqual() {
        int id = 1;
        epic.setId(id);
        Epic epic1 = new Epic("Test", "test");
        epic1.setId(id);

        assertEquals(epic.getId(), epic1.getId(), "Экземпляры Epic с одинаковым id должны быть равны");
    }
}
