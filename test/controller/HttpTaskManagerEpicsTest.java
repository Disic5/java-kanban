package controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasktracker.controller.HttpTaskServer;
import tasktracker.model.Epic;
import tasktracker.model.Progress;
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

public class HttpTaskManagerEpicsTest {
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

    @DisplayName("Успешное добавление эпиков")
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing epic 2",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> allEpics = taskManager.getAllEpics();

        assertNotNull(allEpics, "Epic не возвращаются");
        assertEquals(1, allEpics.size(), "Некорректное количество epic");
        assertEquals("Test 2", allEpics.get(0).getName(), "Некорректное имя epic");
    }

    @DisplayName("Получение всех эпиков")
    @Test
    public void testGetAllTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic1 = new Epic("Test 1", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Epic epic2 = new Epic("Test 2", "Testing epic 2",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(5));
        Epic epic3 = new Epic("Test 3", "Testing epic 3",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> allEpics = taskManager.getAllEpics();

        assertNotNull(allEpics, "Задачи не возвращаются");
        assertEquals(3, allEpics.size(), "Некорректное количество задач");
        assertEquals("Test 1", allEpics.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Test 2", allEpics.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Test 3", allEpics.get(2).getName(), "Некорректное имя задачи");
    }

    @DisplayName("Получение эпиков по id")
    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Epic epic2 = new Epic("Test 2", "Testing epic 2",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(5));
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(epic1.toString(), response.body());

    }

    @DisplayName("Удаление эпиков по id")
    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.addNewEpic(epic1);

        List<Epic> allEpics = taskManager.getAllEpics();
        assertEquals(1, allEpics.size(), "Некорректное количество задач");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicsAfterDelete = taskManager.getAllEpics();
        assertTrue(epicsAfterDelete.isEmpty());
    }

    @DisplayName("Некорректный Json при запросе")
    @Test
    public void testInvalidJson() throws IOException, InterruptedException {
        String taskJson = "{\"wrongField\":\"test\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        List<Epic> allEpics = taskManager.getAllEpics();
        assertTrue(allEpics.isEmpty());
    }

    @DisplayName("Удаление несуществующей эпиков")
    @Test
    public void testDeleteNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics/999");
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
        URI url = URI.create("http://localhost:8081/epics");
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
        Epic epic2 = new Epic("Test 2", "Testing epic 2",
                Progress.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String epicJson = gson.toJson(epic1);
        String epicJson2 = gson.toJson(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }
}
