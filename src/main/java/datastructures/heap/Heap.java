package datastructures.heap;

/**
 * no T extends Comparable, let comparator be parameterized.
 * follows min-heap semantics, meaning peek/pop returns the minimum according to the comparator
 */
public interface Heap<T> {
    void push(T item);

    T peek();
    T pop();

    void clear();

    int size();
    default boolean isEmpty() {
        return size() == 0;
    }
}
