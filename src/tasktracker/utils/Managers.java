package tasktracker.utils;

import tasktracker.history.HistoryManager;
import tasktracker.history.InMemoryHistoryManager;
import tasktracker.service.InMemoryTaskManager;
import tasktracker.service.TaskManager;

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
