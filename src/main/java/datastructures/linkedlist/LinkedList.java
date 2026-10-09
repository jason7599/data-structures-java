package datastructures.linkedlist;

public interface LinkedList<T> extends Iterable<T> {
    T getFirst();
    T getLast();

    void pushFirst(T data);
    void pushLast(T data);

    T popFirst();
    T popLast();

    T get(int index);

    void clear();

    int size();
    default boolean isEmpty() {
        return size() == 0;
    }
}
