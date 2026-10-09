package datastructures.heap;

import datastructures.array.DynamicArray;
import datastructures.array.DynamicArrayImpl;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class BinaryHeap<T> implements Heap<T> {

    private final Comparator<? super T> comparator;
    private final DynamicArray<T> table = new DynamicArrayImpl<>();

    // 0 based indexing
    private static int parent(int i) { return (i - 1) / 2; }
    private static int left(int i) { return i * 2 + 1; }
    private static int right(int i) { return i * 2 + 2; }

    public BinaryHeap(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator);
    }

    @SuppressWarnings("unchecked")
    public BinaryHeap() {
        this((Comparator<? super T>) Comparator.naturalOrder());
    }

    private boolean less(int i, int j) {
        return comparator.compare(table.get(i), table.get(j)) < 0;
    }

    private void swap(int i, int j) {
        table.set(i, table.set(j, table.get(i)));
    }

    private void siftUp(int i) {
        while (i > 0 && less(i, parent(i))) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private void siftDown(int i) {
        // leaf reached
        if (left(i) >= size()) return;

        // find the smaller child
        int next;
        if (right(i) >= size()) next = left(i);
        else next = less(right(i), left(i)) ? right(i) : left(i); // prefer left on ties

        if (less(next, i)) {
            swap(i, next);
            siftDown(next);
        }
    }

    @Override
    public void push(T item) {
        // add to last first
        table.pushBack(item);
        siftUp(table.size() - 1);
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return table.front();
    }

    @Override
    public T pop() {
        // does the empty check
        T min = peek();
        T last = table.popBack();

        if (!isEmpty()) {
            table.set(0, last);
            siftDown(0);
        }

        return min;
    }

    @Override
    public void clear() {
        table.clear();
    }

    @Override
    public int size() {
        return table.size();
    }
}
