package task_tracker.history;

import task_tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> histories = new ArrayList<>();
    private static final Integer SIZE_HISTORY = 10;

    @Override
    public void add(Task task) {
        if (histories.size() == SIZE_HISTORY){
            histories.remove(0);
        }
        histories.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(histories);
    }
}
