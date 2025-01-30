package controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.controller.HttpTaskServer;
import tasktracker.model.Epic;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.service.TaskManager;
import tasktracker.utils.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtaskTest {
    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("Успешное добавление Subtask")
    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Test", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        SubTask subTask = new SubTask("Test 2", "Testing subTask 2",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 1);

        taskManager.addNewEpic(epic);

        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Subtask не возвращаются");
        assertEquals(1, subTasks.size(), "Некорректное количество subtask");
        assertEquals("Test 2", subTasks.get(0).getName(), "Некорректное имя subtask");
    }

    @DisplayName("Получение всех подзадач")
    @Test
    public void testGetAllSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic1 = new Epic("Test 1", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        SubTask subTask1 = new SubTask("Test 1", "Testing subTask 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 1);

        SubTask subTask2 = new SubTask("Test 2", "Testing subTask 2",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20), 1);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются");
        assertEquals(2, subTasks.size(), "Некорректное количество задач");
        assertEquals("Test 1", subTasks.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Test 2", subTasks.get(1).getName(), "Некорректное имя задачи");
    }

    @DisplayName("Получение подзадачи по id")
    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        SubTask subTask1 = new SubTask("Test 1", "Testing subTask 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 1);

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks/" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(subTask1.toString(), response.body());

    }

    @DisplayName("Удаление подзадачи по id")
    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        SubTask subTask1 = new SubTask("Test 1", "Testing subTask 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 1);

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubTask(subTask1);

        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertEquals(1, subTasks.size(), "Некорректное количество задач");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks/" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<SubTask> subTasksAfterDelete = taskManager.getAllSubTasks();
        assertTrue(subTasksAfterDelete.isEmpty());
    }

    @DisplayName("Некорректный Json при запросе")
    @Test
    public void testInvalidJson() throws IOException, InterruptedException {
        String taskJson = "{\"wrongField\":\"test\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertTrue(subTasks.isEmpty());
    }

    @DisplayName("Удаление несуществующей подзадачи")
    @Test
    public void testDeleteNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("Неверный Http метод")
    @Test
    public void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString("{\"wrongField\":\"test\"}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @DisplayName("Задачи пересекаются")
    @Test
    public void testTaskOverlapping() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        SubTask subTask1 = new SubTask("Test 1", "Testing subTask 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 1);

        SubTask subTask2 = new SubTask("Test 2", "Testing subTask 2",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 1);
        taskManager.addNewEpic(epic1);

        String taskJson = gson.toJson(subTask1);
        String taskJson2 = gson.toJson(subTask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }
}