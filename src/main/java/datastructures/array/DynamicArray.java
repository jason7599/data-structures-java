package datastructures.array;

public interface DynamicArray<T> extends Iterable<T> {
    T front();
    T last();
    T get(int index);

    T set(int index, T item);

    void pushFront(T item);
    void pushBack(T item);
    void add(int index, T item);

    T popFront();
    T popBack();
    T remove(int index);

    int size();
    default boolean isEmpty() {
        return size() == 0;
    }
    void clear();

    int capacity();
}
