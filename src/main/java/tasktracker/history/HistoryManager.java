package tasktracker.history;

import tasktracker.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

    Task getHistoryTasks(Integer id);
}
