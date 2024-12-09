package tasktracker.controller;

import tasktracker.model.Epic;
import tasktracker.model.Progress;
import tasktracker.model.SubTask;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;
import tasktracker.utils.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task", "task-descr 1", Progress.NEW);
        Task task2 = new Task("Task", "task-descr 2", Progress.NEW);

        /**
         * Task
         * */

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);

        /**
         * Epic
         * */

        Epic epic1 = new Epic("Epic", "epic-descr 1");
        Epic epic3 = new Epic("Epic", "epic-descr 3");

        taskManager.addNewEpic(epic1);
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

        taskManager.addNewSubTask(subTask4);
        taskManager.addNewSubTask(subTask5);
        taskManager.addNewSubTask(subTask6);

        System.out.println("\n" + "-".repeat(20) + "Print All" + "-".repeat(20));
        /**
         * Удаляем задачу, и поверяем что она удалилась из истории тоже
         * */
        taskManager.deleteTaskById(1);
        taskManager.deleteAllTasks();

        /**
         * Удаляем Эпик, и поверяем что она удалились его подзадачи
         * */
        taskManager.deleteEpicById(epic3.getId());
        taskManager.deleteAllEpics();

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
