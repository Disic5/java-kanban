package tasktracker.fileservice;

import tasktracker.fileservice.exception.ManagerSaveException;
import tasktracker.history.InMemoryHistoryManager;
import tasktracker.model.Epic;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.InMemoryTaskManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;

    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    //сохранение задач в файл
    public void save() {
        if (!Files.exists(file.toPath())) {
            //пробросим исключение наше тогда будет try, catch
            throw new ManagerSaveException("Файл не найден: " + file.getName());
        }
        StringBuilder sB = new StringBuilder("id,type,name,status,description,duration,localDateTime,epic\n");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toString()))) {
            for (Task task : getAllTasks()) {
                sB.append(task.toString());
            }
            for (Epic epic : getAllEpics()) {
                sB.append(epic.toString());
            }
            for (SubTask subTask : getAllSubTasks()) {
                sB.append(subTask.toString());
            }
            writer.write(sB.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла: " + file.getPath());
        }
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        String id = fields[0].trim();
        String type = fields[1].toUpperCase();
        String name = fields[2].trim();
        String description = fields[3].trim();
        String status = fields[4].toUpperCase();
        Duration duration = fields[5].equals("null") ? Duration.ZERO : Duration.parse(fields[5]);
        LocalDateTime startTime = fields[6].equals("null") ? null : LocalDateTime.parse(fields[6]);

        return switch (type) {

            case "TASK" -> new Task(Integer.parseInt(id), name, description, Progress.valueOf(status),
                    duration, startTime);
            case "EPIC" -> new Epic(Integer.parseInt(id), name, description, Progress.valueOf(status),
                    duration, startTime);
            case "SUBTASK" -> {
                if (fields.length < 8) {
                    throw new IllegalArgumentException("Подзадача должна содержать ID эпика: " + value);
                }
                String epicId = fields[7];
                yield new SubTask(Integer.parseInt(id), name, description, Progress.valueOf(status),
                        duration, startTime, Integer.parseInt(epicId));
            }
//
            default -> throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        };
    }

    //     метод будет восстанавливать данные менеджера из файла при запуске программы.
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 1; i < lines.size(); i++) {
                Task task = fromString(lines.get(i));
                if (task instanceof Epic) {
                    fileManager.getEpicMap().put(task.getId(), (Epic) task);
                } else if (task instanceof SubTask) {
                    fileManager.getSubTaskMap().put(task.getId(), (SubTask) task);
                } else {
                    fileManager.getTaskMap().put(task.getId(), task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузки из файла: " + e.getMessage());
        }
        return fileManager;
    }
}