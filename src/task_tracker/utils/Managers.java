package task_tracker.utils;

import task_tracker.history.HistoryManager;
import task_tracker.history.InMemoryHistoryManager;
import task_tracker.service.InMemoryTaskManager;
import task_tracker.service.TaskManager;

public class Managers {
    public Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
