package tasktracker.history;

public class Node<E> {
    private final E data;
    private Node<E> next;
    private Node<E> prev;

    public Node(E data) {
        this.data = data;

    }

    public E getData() {
        return data;
    }

    public Node<E> getPrev() {
        return prev;
    }

    public void setPrev(Node<E> prev) {
        this.prev = prev;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }
}
