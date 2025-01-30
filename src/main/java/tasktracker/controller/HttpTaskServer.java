package tasktracker.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import tasktracker.exeption.NotFoundException;
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
        protected void processGet(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/tasks")) {
                String response = taskManager.getAllTasks().toString();
                sendSuccessResponse(exchange, response);
            } else if (path.matches("/tasks/\\d+")) {
                processGetTaskById(exchange, path);
            } else {
                sendNotFound(exchange, "Некорректный запрос");
            }
        }

        private void processGetTaskById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            try {
                Task task = taskManager.getTaskById(id);
                sendSuccessResponse(exchange, task.toString());
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        }

        @Override
        protected void processPost(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (!path.equals("/tasks") && !path.matches("/tasks/\\d+")) {
                sendNotFound(exchange, "Некорректный запрос");
                return;
            }
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
            try {
                taskManager.addNewTask(task);
                writeResponse(exchange, "Задача добавлена", 201);
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange, e.getMessage());
            }
        }

        @Override
        protected void processDelete(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            try {
                taskManager.deleteTaskById(id);
                sendSuccessResponse(exchange, "Задача с id = " + id + " удалена");
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
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
        protected void processGet(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/epics")) {
                String response = taskManager.getAllEpics().toString();
                sendSuccessResponse(exchange, response);
            } else if (path.matches("/epics/\\d+")) {
                processGetEpicById(exchange, path);
            } else {
                sendNotFound(exchange, "Некорректный запрос");
            }
        }

        private void processGetEpicById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            try {
                Epic epic = taskManager.getEpicById(id);
                sendSuccessResponse(exchange, epic.toString());
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        }

        @Override
        protected void processPost(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (!path.equals("/epics") && !path.matches("/epics/\\d+")) {
                sendNotFound(exchange, "Некорректный запрос");
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                writeResponse(exchange, "Описание задачи не может быть пустым", 400);
                return;
            }
            Gson gson = HttpTaskServer.getGson();
            Epic epic;

            epic = gson.fromJson(body, Epic.class);
            if (epic.getName() == null || epic.getDescription() == null || epic.getStatus() == null) {
                writeResponse(exchange, "Некорректные данные: поля не должны быть null", 400);
                return;
            }
            try {
                taskManager.addNewEpic(epic);
                writeResponse(exchange, "Эпик добавлена", 201);
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange, e.getMessage());
            }
        }

        @Override
        protected void processDelete(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            try {
                taskManager.deleteEpicById(id);
                sendSuccessResponse(exchange, "Эпик с id = " + id + " удален");
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
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
        protected void processGet(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/subtasks")) {
                String response = taskManager.getAllSubTasks().toString();
                sendSuccessResponse(exchange, response);
            } else if (path.matches("/subtasks/\\d+")) {
                processGetSubtaskById(exchange, path);
            } else {
                sendNotFound(exchange, "Некорректный запрос");
            }
        }

        @Override
        protected void processPost(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (!path.equals("/subtasks") && !path.matches("/subtasks/\\d+")) {
                sendNotFound(exchange, "Некорректный запрос");
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                writeResponse(exchange, "Описание задачи не может быть пустым", 400);
                return;
            }
            Gson gson = HttpTaskServer.getGson();
            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (subTask.getEpicId() == null) {
                writeResponse(exchange, "epicId должен присутствовать", 400);
                return;
            }

            try {
                taskManager.addNewSubTask(subTask);
                writeResponse(exchange, "Подзадача добавлена", 201);
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange, e.getMessage());
            }
        }

        private void processGetSubtaskById(HttpExchange exchange, String path) throws IOException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            try {
                SubTask subTask = taskManager.getSubTaskById(id);
                sendSuccessResponse(exchange, subTask.toString());
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        }

        @Override
        protected void processDelete(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            try {
                taskManager.deleteSubTaskById(id);
                sendSuccessResponse(exchange, "Подзадача с id = " + id + " удалена");
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
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
        protected void processGet(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/history")) {
                String response = taskManager.getHistory().toString();
                sendSuccessResponse(exchange, response);
            } else {
                sendNotFound(exchange, "Некорректный запрос");
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
        protected void processGet(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/prioritized")) {
                String response = taskManager.getPrioritizedTasks().toString();
                sendSuccessResponse(exchange, response);
            } else {
                sendNotFound(exchange, "Некорректный запрос");
            }
        }
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new BaseHttpHandler.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new BaseHttpHandler.LocalDateTimeAdapter())
                .create();
    }
}
