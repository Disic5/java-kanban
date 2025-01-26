package tasktracker.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import tasktracker.fileservice.exception.ManagerSaveException;
import tasktracker.model.Epic;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;
import tasktracker.utils.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8081;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHttpHandler(taskManager));
        server.createContext("/epics", new EpicHttpHandler(taskManager));
        server.createContext("/subtasks", new SubTaskHttpHandler(taskManager));
        server.createContext("/history", new HistoryHttpHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));

    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту! \n");
    }

    public void stop() {
        server.stop(0);
        System.out.println("\n HTTP-сервер остановлен");
    }

    public static void main(String[] args) throws ManagerSaveException, IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }

    /**
     * Task
     */
    static class TaskHttpHandler extends BaseHttpHandler {
        public TaskHttpHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handleRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            String response;
            if (requestMethod.equalsIgnoreCase("GET")) {
                if (path.equals("/tasks")) {
                    response = taskManager.getAllTasks().toString();
                    sendSuccessResponse(exchange, response);
                } else if (path.matches("/tasks/\\d+")) {
                    handleGetTaskById(exchange, path);
                }
            } else if (requestMethod.equalsIgnoreCase("POST")) {
                handlePostCreateTask(exchange);

            } else if (requestMethod.equalsIgnoreCase("DELETE")) {
                handleDeleteTaskById(exchange, path);
            } else {
                writeResponse(exchange, "Метод не поддерживается", 405);
            }
        }

        private void handleGetTaskById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Optional<Task> task = taskManager.getTaskById(id);
            if (task.isPresent()) {
                sendSuccessResponse(exchange, task.get().toString());
            } else {
                sendNotFound(exchange, path);
            }
        }

        private void handlePostCreateTask(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                writeResponse(exchange, "Описание задачи не может быть пустым", 400);
                return;
            }
            Gson gson = HttpTaskServer.getGson();
            Task task;

            task = gson.fromJson(body, Task.class);
            if (task.getName() == null || task.getDescription() == null || task.getStatus() == null) {
                writeResponse(exchange, "Некорректные данные: поля не должны быть null", 400);
                return;
            }

            taskManager.addNewTask(task);
            writeResponse(exchange, "Задача добавлена", 201);
        }

        private void handleDeleteTaskById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Optional<Task> task = taskManager.getTaskById(id);
            if (task.isPresent()) {
                taskManager.deleteTaskById(id);
                sendSuccessResponse(exchange, "Задача с id = " + id + " удалена");
            } else {
                sendNotFound(exchange, path);
            }
        }
    }

    /**
     * Epics
     */
    static class EpicHttpHandler extends BaseHttpHandler {
        public EpicHttpHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handleRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            String response;
            if (requestMethod.equalsIgnoreCase("GET")) {
                if (path.equals("/epics")) {
                    response = taskManager.getAllEpics().toString();
                    sendSuccessResponse(exchange, response);
                } else if (path.matches("/epics/\\d+")) {
                    handleGetEpicById(exchange, path);
                }
            } else if (requestMethod.equalsIgnoreCase("POST")) {
                handlePostCreateEpic(exchange);

            } else if (requestMethod.equalsIgnoreCase("DELETE")) {
                handleDeleteEpicById(exchange, path);
            } else {
                writeResponse(exchange, "Метод не поддерживается", 405);
            }
        }

        private void handlePostCreateEpic(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                writeResponse(exchange, "Описание задачи не может быть пустым", 400);
                return;
            }

            Gson gson = HttpTaskServer.getGson();
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getName() == null || epic.getDescription() == null || epic.getStatus() == null) {
                writeResponse(exchange, "Некорректные данные: поля не должны быть null", 400);
                return;
            }
            taskManager.addNewEpic(epic);
            writeResponse(exchange, "Epic успешно добавлен", 201);
        }

        private void handleGetEpicById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Optional<Epic> epic = taskManager.getEpicById(id);
            if (epic.isPresent()) {
                sendSuccessResponse(exchange, epic.get().toString());
            } else {
                sendNotFound(exchange, path);
            }
        }

        private void handleDeleteEpicById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Optional<Epic> epic = taskManager.getEpicById(id);
            if (epic.isPresent()) {
                taskManager.deleteEpicById(id);
                sendSuccessResponse(exchange, "Задача с id = " + id + " удалена");
            } else {
                sendNotFound(exchange, path);
            }
        }
    }

    /**
     * Subtasks
     */
    static class SubTaskHttpHandler extends BaseHttpHandler {
        public SubTaskHttpHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handleRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            String response;
            if (requestMethod.equalsIgnoreCase("GET")) {
                if (path.equals("/subtasks")) {
                    response = taskManager.getAllSubTasks().toString();
                    sendSuccessResponse(exchange, response);
                } else if (path.matches("/subtasks/\\d+")) {
                    handleGetSubtaskById(exchange, path);
                }
            } else if (requestMethod.equalsIgnoreCase("POST")) {
                handlePostCreateSubtask(exchange);

            } else if (requestMethod.equalsIgnoreCase("DELETE")) {
                handleDeleteSubtaskById(exchange, path);
            } else {
                writeResponse(exchange, "Метод не поддерживается", 405);
            }
        }

        private void handlePostCreateSubtask(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                writeResponse(exchange, "Описание задачи не может быть пустым", 400);
                return;
            }
            Gson gson = HttpTaskServer.getGson();
            SubTask subTask;
            try {
                subTask = gson.fromJson(body, SubTask.class);
                if (subTask.getEpicId() == null) {
                    writeResponse(exchange, "epicId должен присутствовать", 400);
                    return;
                }
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, e.getMessage(), 400);
                return;
            }

            taskManager.addNewSubTask(subTask);
            writeResponse(exchange, "Subtask успешно добавлен", 201);
        }

        private void handleGetSubtaskById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Optional<SubTask> subTask = taskManager.getSubTaskById(id);
            if (subTask.isPresent()) {
                sendSuccessResponse(exchange, subTask.get().toString());
            } else {
                sendNotFound(exchange, path);
            }
        }

        private void handleDeleteSubtaskById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Optional<SubTask> subTask = taskManager.getSubTaskById(id);
            if (subTask.isPresent()) {
                taskManager.deleteSubTaskById(id);
                sendSuccessResponse(exchange, "Задача с id = " + id + " удалена");
            } else {
                sendNotFound(exchange, path);
            }
        }
    }

    /**
     * History
     */
    static class HistoryHttpHandler extends BaseHttpHandler {
        public HistoryHttpHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handleRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            String response;
            if (requestMethod.equalsIgnoreCase("GET")) {
                if (path.equals("/history")) {
                    response = taskManager.getHistory().toString();
                    sendSuccessResponse(exchange, response);
                } else {
                    sendNotFound(exchange, "Некорректный запрос");
                }
            } else {
                writeResponse(exchange, "Метод не поддерживается", 405);
            }
        }
    }

    /**
     * Prioritized
     */
    static class PrioritizedHttpHandler extends BaseHttpHandler {
        public PrioritizedHttpHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handleRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            String response;
            if (requestMethod.equalsIgnoreCase("GET")) {
                if (path.equals("/prioritized")) {
                    response = taskManager.getPrioritizedTasks().toString();
                    sendSuccessResponse(exchange, response);
                } else {
                    sendNotFound(exchange, "Некорректный запрос");
                }
            } else {
                writeResponse(exchange, "Метод не поддерживается", 405);
            }
        }
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    /**
     * Adapters
     */
    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(FORMATTER.format(localDateTime));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return value == null || value.isBlank() ? null : LocalDateTime.parse(value, FORMATTER);
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(duration.toString());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            if (value == null) {
                return null;
            }
            return Duration.parse(value);
        }
    }
}
