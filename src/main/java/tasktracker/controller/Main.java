package tasktracker.controller;

import tasktracker.fileservice.FileBackedTaskManager;
import tasktracker.fileservice.exception.ManagerSaveException;
import tasktracker.history.HistoryManager;
import tasktracker.model.Epic;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;
import tasktracker.utils.Managers;

import java.io.File;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        File file = Paths.get("src/main/resources/tasks_file.csv").toFile();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(historyManager, file);
        FileBackedTaskManager loadFromFile = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Загружаем из файла Task: " + loadFromFile.getAllTasks());
        System.out.println("Загружаем из файла Epic: " + loadFromFile.getAllEpics());
        System.out.println("Загружаем из файла Subtask: " + loadFromFile.getAllSubTasks());

        Task task1 = new Task("Task", "task-descr-1", Progress.NEW);
        Task task2 = new Task("Task", "task-descr-2", Progress.NEW);
        Task task3 = new Task("Task", "task-descr-3", Progress.NEW);

        /**
         * Task
         * */

        fileManager.addNewTask(task1);
        fileManager.addNewTask(task2);
        fileManager.addNewTask(task3);

//        fileManager.deleteAllTasks();

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);

        /**
         * Epic
         * */

        Epic epic1 = new Epic("Epic", "epic-descr 1");
        Epic epic2 = new Epic("Epic", "epic-descr 2");
        Epic epic3 = new Epic("Epic", "epic-descr 3");

        fileManager.addNewEpic(epic1);
        fileManager.addNewEpic(epic2);
        fileManager.addNewEpic(epic3);


        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);


        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic3.getId());
        taskManager.getEpicById(epic3.getId());

        /**
         * SubTask
         * */

        SubTask subTask4 = new SubTask("Subtask", "sub-descr 3", Progress.NEW, epic3.getId());
        SubTask subTask5 = new SubTask("Subtask", "sub-descr 3", Progress.NEW, epic3.getId());
        SubTask subTask6 = new SubTask("Subtask", "sub-descr 3", Progress.DONE, epic3.getId());

        fileManager.addNewSubTask(subTask4);
        fileManager.addNewSubTask(subTask5);
        fileManager.addNewSubTask(subTask6);

        taskManager.addNewSubTask(subTask4);
        taskManager.addNewSubTask(subTask5);
        taskManager.addNewSubTask(subTask6);

        System.out.println("\n" + "-".repeat(20) + "Print All" + "-".repeat(20));
        /**
         * Удаляем задачу, и поверяем что она удалилась из истории тоже
         * */
        taskManager.deleteTaskById(1);
//        taskManager.deleteAllTasks();

        /**
         * Удаляем Эпик, и поверяем что она удалились его подзадачи
         * */
        taskManager.deleteEpicById(epic3.getId());
//        taskManager.deleteAllEpics();

        printAllTasks(taskManager);

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
