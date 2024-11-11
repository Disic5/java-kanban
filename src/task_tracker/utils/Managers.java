package task_tracker.utils;

import task_tracker.history.HistoryManager;
import task_tracker.history.InMemoryHistoryManager;
import task_tracker.service.InMemoryTaskManager;
import task_tracker.service.TaskManager;

public class Managers {
    private final static HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
