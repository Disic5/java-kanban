import task_tracker.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task", "task-descr 1", Progress.NEW);
        Task task2 = new Task("Task", "task-descr 2", Progress.NEW);
        Task task3 = new Task("Task", "task-descr 3", Progress.NEW);


        /**
         * Task
         * */
        System.out.println("\n" + "-".repeat(20) + "TASK" + "-".repeat(20));

        System.out.println("Создание задач: ");
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        System.out.println(taskManager.getTaskMap());

        System.out.println("\n Обновление задач: ");
        taskManager.updateTask(task1.getId(), new Task("Task", "task-descr 5", Progress.IN_PROGRESS));
        System.out.println(taskManager.getTaskMap());

        System.out.println("\n Поиск по Id: ");
        System.out.println(taskManager.getTaskById(3));

        System.out.println("\n Удаление по Id: ");
        taskManager.deleteTaskById(3);
        System.out.println(taskManager.getTaskMap());

        System.out.println("\n Получить весь список: ");
        System.out.println(taskManager.getAllTasks());

        System.out.println("\n Очистить список: ");
        taskManager.deleteAllTasks();
        System.out.println(taskManager.getTaskMap());


        /**
         * Epic
         * */
        System.out.println("\n" + "-".repeat(20) + "EPIC" + "-".repeat(20));
        Epic epic1 = new Epic("Epic", "epic-descr 1");
        Epic epic2 = new Epic("Epic", "epic-descr 2");
        Epic epic3 = new Epic("Epic", "epic-descr 3");

        System.out.println("\n Создание Эпиков: ");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);
        System.out.println(taskManager.getEpicMap());

        System.out.println("\n Обновление Эпика: ");
        taskManager.updateEpic(epic1.getId(), new Epic("Epic", "epic-descr 5"));
        System.out.println(taskManager.getEpicMap());

        System.out.println("\n Поиск по Id: ");
        System.out.println(taskManager.getEpicById(epic1.getId()));

        System.out.println("\n Удаление по Id: ");
        taskManager.deleteEpicById(4);
        System.out.println(taskManager.getEpicMap());

        System.out.println("\n Получить весь список: ");
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n Очистить список: ");
        taskManager.deleteAllEpics();
        System.out.println(taskManager.getEpicMap());


        /**
         * SubTask
         * */
        SubTask subTask1 = new SubTask("Subtask", "sub-descr 1", Progress.DONE, epic1.getId());
        SubTask subTask2 = new SubTask("Subtask", "sub-descr 2", Progress.DONE, epic2.getId());
        SubTask subTask3 = new SubTask("Subtask", "sub-descr 2", Progress.DONE, epic2.getId());
        SubTask subTask4 = new SubTask("Subtask", "sub-descr 3", Progress.NEW, epic3.getId());
        SubTask subTask5 = new SubTask("Subtask", "sub-descr 3", Progress.NEW, epic3.getId());
        SubTask subTask6 = new SubTask("Subtask", "sub-descr 3", Progress.DONE, epic3.getId());

        System.out.println("\n" + "-".repeat(20) + "SUBTASK" + "-".repeat(20));

        System.out.println("\n Создание подзадач: ");
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        taskManager.addNewSubTask(subTask3);
        taskManager.addNewSubTask(subTask4);
        taskManager.addNewSubTask(subTask5);
        taskManager.addNewSubTask(subTask6);
        System.out.println(taskManager.getSubTaskMap());
        System.out.println("Число подзадач у Эпика 1 -  " + epic1.getSubTaskList().size());
        System.out.println("Число подзадач у Эпика 2 - " + epic2.getSubTaskList().size());
        System.out.println("Число подзадач у Эпика 3  - " + epic3.getSubTaskList().size());

        System.out.println("\n Обновление подзадач: ");
        taskManager.updateSubTask(subTask1.getId(), new SubTask("SubTask", "subtask-descr 5", Progress.IN_PROGRESS, epic1.getId()));
        System.out.println(taskManager.getSubTaskMap());

        System.out.println("\n Поиск по Id: ");
        System.out.println(taskManager.getSubTaskById(7));

        System.out.println("\n Вывести все подзадачи Epic: ");
        System.out.println(taskManager.getAllSubtaskByEpic(epic3));

        System.out.println("\n Изменить статус Epic: ");
        taskManager.updateEpicStatus(epic3);
        System.out.println(taskManager.getEpicById(epic3.getId()));

        System.out.println("\n Удаление по Id: ");
        taskManager.deleteSubTaskById(9);
        System.out.println(taskManager.getSubTaskMap());

        System.out.println("\n Получить весь список: ");
        System.out.println(taskManager.getAllSubTasks());

        System.out.println("\n Очистить список: ");
        taskManager.deleteAllSubTasks();
        System.out.println(taskManager.getSubTaskMap());

    }
}
