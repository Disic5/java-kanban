package tasktracker.controller;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
            default:
                sendMethodNotAllowed(exchange, "Метод не поддерживается");
        }
    }


    protected void processGet(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange, "Метод processGet в BaseHandler ???? ");
    }

    protected void processPost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange, "Метод processPost в BaseHandler ???? ");
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange, "Метод processDelete в BaseHandler ???? ");
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

    protected void sendMethodNotAllowed(HttpExchange exchange, String message) throws IOException {
        writeResponse(exchange, message, 405);
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
}