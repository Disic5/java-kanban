package tasktracker.fileservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.fileservice.exception.ManagerSaveException;
import tasktracker.model.Epic;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.model.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private static Path tempFile;

    @BeforeAll
    static void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".csv");
    }

    @AfterAll
    static void cleanUp() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @DisplayName("сохранение в файл задач")
    @Test
    void saveToFile_shouldSaveTasksToFile() throws IOException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(tempFile.toFile());
        Epic epic1 = new Epic(1, "Epic", "epic-descr 1", Progress.NEW);
        Epic epic2 = new Epic(2, "Epic", "epic-descr 2", Progress.NEW);
        Epic epic3 = new Epic(3, "Epic", "epic-descr 3", Progress.NEW);

        fileManager.addNewEpic(epic1);
        fileManager.addNewEpic(epic2);
        fileManager.addNewEpic(epic3);

        assertTrue(Files.exists(tempFile), "Файл не был создан.");

        String readFile = Files.readString(tempFile);
        String expected =
                """
                        id,type,name,status,description,epic
                        1,EPIC,Epic,epic-descr 1,NEW
                        2,EPIC,Epic,epic-descr 2,NEW
                        3,EPIC,Epic,epic-descr 3,NEW
                        """;

        // в файлах часто используется \r заменил на \n
        assertEquals(normalizeLineEndings(expected), normalizeLineEndings(readFile));
    }

    @DisplayName("удаление задачи из файла")
    @Test
    void deleteTask_shouldDeleteToFileTasks() throws IOException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(tempFile.toFile());
        Epic epic1 = new Epic(1, "Epic", "epic-descr 1", Progress.NEW);
        Epic epic2 = new Epic(2, "Epic", "epic-descr 2", Progress.NEW);
        Epic epic3 = new Epic(3, "Epic", "epic-descr 3", Progress.NEW);
        fileManager.addNewEpic(epic1);
        fileManager.addNewEpic(epic2);
        fileManager.addNewEpic(epic3);

        String initialContent = Files.readString(tempFile);
        fileManager.deleteEpicById(1);
        String updatedFile = Files.readString(tempFile);
        String expected =
                """
                        id,type,name,status,description,epic
                        2,EPIC,Epic,epic-descr 2,NEW
                        3,EPIC,Epic,epic-descr 3,NEW
                        """;

        assertNotEquals(normalizeLineEndings(updatedFile), normalizeLineEndings(initialContent));
        assertEquals(normalizeLineEndings(expected), normalizeLineEndings(updatedFile));
    }

    @DisplayName("удаление всех задач из файла")
    @Test
    void deleteAllTask_shouldDeleteToFileTasks() throws IOException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(tempFile.toFile());
        Epic epic1 = new Epic(1, "Epic", "epic-descr 1", Progress.NEW);
        Epic epic2 = new Epic(2, "Epic", "epic-descr 2", Progress.NEW);
        Epic epic3 = new Epic(3, "Epic", "epic-descr 3", Progress.NEW);

        fileManager.addNewEpic(epic1);
        fileManager.addNewEpic(epic2);
        fileManager.addNewEpic(epic3);

        String initialContent = Files.readString(tempFile);
        fileManager.deleteAllEpics();
        String updateFile = Files.readString(tempFile);
        String expected =
                "id,type,name,status,description,epic\n";

        assertNotEquals(normalizeLineEndings(updateFile), normalizeLineEndings(initialContent));
        assertEquals(normalizeLineEndings(expected), normalizeLineEndings(updateFile));
    }

    @DisplayName("обновление задач в файле")
    @Test
    void updateAndSaveToFileTasks() throws IOException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(tempFile.toFile());

        Epic epic1 = new Epic(1, "Epic", "epic-descr 1", Progress.NEW);
        Epic epic2 = new Epic(2, "Epic", "epic-descr 2", Progress.NEW);
        Epic epic3 = new Epic(3, "Epic", "epic-descr 3", Progress.NEW);

        fileManager.addNewEpic(epic1);
        fileManager.addNewEpic(epic2);
        fileManager.addNewEpic(epic3);

        String initialContent = Files.readString(tempFile);
        epic2.setId(5);
        epic2.setStatus(Progress.IN_PROGRESS);
        fileManager.updateEpic(epic2);
        String updatedFile = Files.readString(tempFile);

        String expected =
                """
                        id,type,name,status,description,epic
                        1,EPIC,Epic,epic-descr 1,NEW
                        5,EPIC,Epic,epic-descr 2,IN_PROGRESS
                        3,EPIC,Epic,epic-descr 3,NEW
                        """;

        assertNotEquals(normalizeLineEndings(updatedFile), normalizeLineEndings(initialContent));
        assertEquals(normalizeLineEndings(expected), normalizeLineEndings(updatedFile));
    }

    @DisplayName("получить задачу из строки")
    @Test
    void fromStringToTask_whenStringCorrect_shouldBeSuccess() throws ManagerSaveException {
        String taskString = "1,TASK,Task,task-descr-1,NEW";
        String epicString = "6,EPIC,Epic,epic-descr-3,IN_PROGRESS";
        String subtaskString = "7,SUBTASK,Subtask,sub-descr 3,NEW,6";

        Task task = FileBackedTaskManager.fromString(taskString);
        Task epic = FileBackedTaskManager.fromString(epicString);
        Task subtask = FileBackedTaskManager.fromString(subtaskString);

        assertEquals(new Task(1, "Task", "task-descr-1", Progress.NEW), task);
        assertEquals(new Epic(6, "Epic", "epic-descr-3", Progress.IN_PROGRESS), epic);
        assertEquals(new SubTask(7, "Subtask", "sub-descr 3", Progress.NEW, 6), subtask);
    }

    @DisplayName("ошибка десериализации выбросит исключение")
    @Test
    void fromStringToTaskTest() {
        String taskString = "1,UNKNOWN,Task,task-descr-1,NEW";
        ManagerSaveException exception = assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.fromString(taskString));

        assertEquals("Неизвестный тип задачи: UNKNOWN", exception.getMessage());
    }

    @DisplayName("загрузка из файла")
    @Test
    void loadFromFile() throws ManagerSaveException, IOException {
        String content =
                """
                        id,type,name,status,description,epic
                        1,EPIC,Epic,epic-descr 1,NEW
                        5,EPIC,Epic,epic-descr 2,IN_PROGRESS
                        3,EPIC,Epic,epic-descr 3,NEW
                        """;
        StringBuilder sB = new StringBuilder(normalizeLineEndings(content));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            writer.write(sB.toString());
        }
        List<String> list = Files.readAllLines(tempFile);
        FileBackedTaskManager loadFromFile = FileBackedTaskManager.loadFromFile(tempFile.toFile());

        assertFalse(list.isEmpty());
        assertTrue(Files.exists(tempFile), "Файл не был создан.");
        assertFalse(loadFromFile.getAllEpics().isEmpty());
        assertNotNull(loadFromFile.getAllEpics());
        assertEquals(3, loadFromFile.getAllEpics().size());
        assertTrue(loadFromFile.getAllTasks().isEmpty());
        assertTrue(loadFromFile.getAllSubTasks().isEmpty());
    }

    private String normalizeLineEndings(String input) {
        return input.replace("\r\n", "\n").replace("\r", "\n");
    }
}