package tasktracker.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.exeption.NotFoundException;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            handleRequest(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    protected static void writeResponse(HttpExchange exchange,
                                        String responseString,
                                        int responseCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    protected void sendSuccessResponse(HttpExchange exchange, String message) throws IOException {
        writeResponse(exchange, message, 200);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        writeResponse(exchange, message, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        writeResponse(exchange, message, 406);
    }

    protected abstract void handleRequest(HttpExchange exchange) throws IOException;

}