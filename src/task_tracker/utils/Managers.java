package task_tracker.utils;

import task_tracker.history.HistoryManager;
import task_tracker.history.InMemoryHistoryManager;
import task_tracker.service.InMemoryTaskManager;
import task_tracker.service.TaskManager;

public class Managers {
    private final static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private final static InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return historyManager;
    }
}
