package tasktracker.controller;

import tasktracker.fileservice.FileBackedTaskManager;
import tasktracker.fileservice.exception.ManagerSaveException;
import tasktracker.model.Epic;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;
import tasktracker.utils.Managers;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {
        TaskManager taskManager = Managers.getDefault();
        File file = Paths.get("src/main/resources/tasks_file.csv").toFile();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
        FileBackedTaskManager loadFromFile = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Загружаем из файла Task: " + loadFromFile.getAllTasks());
        System.out.println("Загружаем из файла Epic: " + loadFromFile.getAllEpics());
        System.out.println("Загружаем из файла Subtask: " + loadFromFile.getAllSubTasks());

        Task task1 = new Task("Task", "task-descr-1", Progress.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 11, 11, 10, 0));
        Task task2 = new Task("Task", "task-descr-2", Progress.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 11, 12, 11, 0));
        Task task3 = new Task("Task", "task-descr-3", Progress.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2025, 11, 11, 12, 0));
        Task task4 = new Task("Task", "task-descr-4", Progress.NEW, Duration.ofMinutes(90),
                LocalDateTime.of(2025, 11, 11, 13, 0));

        /**
         * Task
         * */

        fileManager.addNewTask(task1);
        fileManager.addNewTask(task2);
        fileManager.addNewTask(task3);
        fileManager.addNewTask(task4);

//        fileManager.deleteAllTasks();

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewTask(task4);


        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
//        taskManager.getTaskById(4);

        /**
         * Epic
         * */

        Epic epic1 = new Epic("Epic", "epic-descr 1");
        Epic epic2 = new Epic("Epic", "epic-descr 2");
        Epic epic3 = new Epic("Epic", "epic-descr 3");
//        Epic epic4 = new Epic("Epic", "epic-descr 4");

        fileManager.addNewEpic(epic1);
        fileManager.addNewEpic(epic2);
        fileManager.addNewEpic(epic3);


        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);

        /**
         * SubTask
         * */

        SubTask subTask4 = new SubTask("Subtask", "sub-descr 3", Progress.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 11, 15, 10, 0), epic2.getId());
        SubTask subTask5 = new SubTask("Subtask", "sub-descr 4", Progress.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 11, 16, 10, 0), epic2.getId());
        SubTask subTask6 = new SubTask("Subtask", "sub-descr 5", Progress.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 11, 17, 11, 0), epic3.getId());
        SubTask subTask7 = new SubTask("Subtask", "sub-descr 6", Progress.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 11, 18, 11, 0), epic3.getId());
        SubTask subTask8 = new SubTask("Subtask", "sub-descr 3",
                Progress.DONE, Duration.ZERO, null, epic3.getId());

        fileManager.addNewSubTask(subTask4);
        fileManager.addNewSubTask(subTask5);
        fileManager.addNewSubTask(subTask6);
        fileManager.addNewSubTask(subTask7);
//        fileManager.addNewSubTask(subTask8);

        taskManager.addNewSubTask(subTask4);
        taskManager.addNewSubTask(subTask5);
        taskManager.addNewSubTask(subTask6);
        taskManager.addNewSubTask(subTask7);
        taskManager.addNewSubTask(subTask8);

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic3.getId());

        System.out.println("\n" + "-".repeat(20) + "Print All" + "-".repeat(20));
        /**
         * Удаляем задачу, и поверяем что она удалилась из истории тоже
         * */
//        taskManager.deleteTaskById(1);
//        taskManager.deleteAllTasks();

        /**
         * Удаляем Эпик, и поверяем что она удалились его подзадачи
         * */
//        taskManager.deleteEpicById(epic3.getId());
//        taskManager.deleteAllEpics();

        printAllTasks(taskManager);
        System.out.println(epic3 + "-".repeat(20));

        System.out.println("\nОтсортированные по времени задачи: ");
        taskManager.getPrioritizedTasks();
    }

    /**
     * Выводим в консоль все задачи и историю
     */
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubtaskByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
