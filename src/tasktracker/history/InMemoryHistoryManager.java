package tasktracker.history;

import tasktracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> indexHistoryMap = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;
    private int size;

    @Override
    public void add(Task task) {
        addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }
        return result;
    }

    @Override
    public void remove(int id) {
        Node<Task> taskNode = indexHistoryMap.get(id);
        if (taskNode != null) {
            removeNode(taskNode);
            indexHistoryMap.remove(id);
        }
    }

    public void addLast(Task task) {
        if (indexHistoryMap.containsKey(task.getId())) {
            removeNode(indexHistoryMap.get(task.getId()));
        }
        Node<Task> newNode = new Node<>(task);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        indexHistoryMap.put(task.getId(), newNode);
        size++;
    }

    public Task getHistoryTasks(Integer id) {
        Node<Task> current = indexHistoryMap.get(id);
        if (current == null) {
            throw new NoSuchElementException();
        }
        return current.getData();
    }

    public void removeNode(Node<Task> taskNode) {
        if (taskNode == null) {
            return;
        }
        Node<Task> next = taskNode.getNext();
        Node<Task> prev = taskNode.getPrev();

        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
        }
        taskNode.setNext(null);
        taskNode.setPrev(null);
        size--;
    }
}
