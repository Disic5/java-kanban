package task_tracker.controller;

import task_tracker.model.Epic;
import task_tracker.model.Progress;
import task_tracker.model.SubTask;
import task_tracker.model.Task;
import task_tracker.service.TaskManager;
import task_tracker.utils.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task", "task-descr 1", Progress.NEW);
        Task task2 = new Task("Task", "task-descr 2", Progress.NEW);
        Task task3 = new Task("Task", "task-descr 3", Progress.NEW);

        /**
         * Task
         * */

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        Task task = taskManager.getAllTasks().get(1);
        task.setDescription("task-descr 5 update");
        taskManager.updateTask(task);

        taskManager.getTaskById(3);
        taskManager.getTaskById(3);
        taskManager.getTaskById(3);
        taskManager.getTaskById(3);

        /**
         * Epic
         * */

        Epic epic1 = new Epic("Epic", "epic-descr 1");
        Epic epic2 = new Epic("Epic", "epic-descr 2");
        Epic epic3 = new Epic("Epic", "epic-descr 3");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);

        Epic epic = taskManager.getAllEpics().get(1);
        epic.setDescription("epic-descr-update");
        taskManager.updateEpic(epic);

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());

        /**
         * SubTask
         * */
        SubTask subTask1 = new SubTask("Subtask", "sub-descr 1", Progress.DONE, epic1.getId());
        SubTask subTask2 = new SubTask("Subtask", "sub-descr 2", Progress.DONE, epic2.getId());
        SubTask subTask3 = new SubTask("Subtask", "sub-descr 2", Progress.DONE, epic2.getId());
        SubTask subTask4 = new SubTask("Subtask", "sub-descr 3", Progress.NEW, epic3.getId());
        SubTask subTask5 = new SubTask("Subtask", "sub-descr 3", Progress.NEW, epic3.getId());
        SubTask subTask6 = new SubTask("Subtask", "sub-descr 3", Progress.DONE, epic3.getId());

        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        taskManager.addNewSubTask(subTask3);
        taskManager.addNewSubTask(subTask4);
        taskManager.addNewSubTask(subTask5);
        taskManager.addNewSubTask(subTask6);

        SubTask subTask = taskManager.getAllSubTasks().get(1);
        subTask.setStatus(Progress.NEW);
        SubTask subTask7 = taskManager.getAllSubTasks().get(2);
        subTask7.setStatus(Progress.IN_PROGRESS);
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask7);

        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(7);

        taskManager.getTaskById(3);
        taskManager.getTaskById(3);
        taskManager.getTaskById(3);

        taskManager.updateEpicStatus(epic3);

        System.out.println("\n" + "-".repeat(20) + "Print All" + "-".repeat(20));

        printAllTasks(taskManager);

    }

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
        for (Task task : Managers.getDefaultHistory().getHistory()) {
            System.out.println(task);
        }
    }
}
