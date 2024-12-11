package tasktracker.fileservice;

import tasktracker.fileservice.exception.ManagerSaveException;
import tasktracker.history.HistoryManager;
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
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //сохранение задач в файл
    public void save() throws ManagerSaveException {
        if (!Files.exists(file.toPath())) {
            //пробросим исключение наше тогда будет try, catch
            throw new ManagerSaveException("Файл не найден: " + file.getName());
        }
        StringBuilder sB = new StringBuilder("id,type,name,status,description,epic\n");
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

    public static Task fromString(String value) throws ManagerSaveException {
        String[] fields = value.split(",");
        String id = fields[0].trim();
        String type = fields[1].toUpperCase();
        String name = fields[2].trim();
        String description = fields[3].trim();
        String status = fields[4].toUpperCase();
        String epicId = fields.length > 5 ? fields[5] : "";
        return switch (type) {
            case "TASK" -> new Task(Integer.parseInt(id), name, description, Progress.valueOf(status));
            case "EPIC" -> new Epic(Integer.parseInt(id), name, description, Progress.valueOf(status));
            case "SUBTASK" ->
                    new SubTask(Integer.parseInt(id), name, description, Progress.valueOf(status), Integer.parseInt(epicId));
            default -> throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        };
    }

    //     метод будет восстанавливать данные менеджера из файла при запуске программы.
    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
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