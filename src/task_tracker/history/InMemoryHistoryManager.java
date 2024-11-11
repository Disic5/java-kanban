package task_tracker.history;

import task_tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> histories = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (histories.size() == 10){
            histories.remove(0);
        }
        histories.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return histories;
    }

    public List<Task> getHistories() {
        return histories;
    }
}
