package datastructures.array;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DynamicArrayImpl<T> implements DynamicArray<T> {

    private static final int DEFAULT_INIT_CAPACITY = 16;

    private Object[] items = new Object[DEFAULT_INIT_CAPACITY];
    private int size;

    private void resize(int capacity) {
        items = Arrays.copyOf(items, capacity);
    }

    @Override
    public T front() {
        return get(0);
    }

    @Override
    public T last() {
        return get(size - 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }
        return (T) items[index];
    }

    @Override
    public T set(int index, T item) {
        T old = get(index);
        items[index] = item;
        return old;
    }

    @Override
    public void pushFront(T item) {
        add(0, item);
    }

    @Override
    public void pushBack(T item) {
        add(size, item);
    }

    @Override
    public void add(int index, T item) {
        // index == size is allowed, means push back
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index);
        }

        if (size == items.length) {
            resize(items.length + items.length / 2);
        }

        // move [index, size) one slot right
        System.arraycopy(items, index, items, index + 1, size - index);
        items[index] = item;
        size++;
    }

    @Override
    public T popFront() {
        return remove(0);
    }

    @Override
    public T popBack() {
        return remove(size - 1);
    }

    @Override
    public T remove(int index) {
        // does the index check
        T removed = get(index);

        System.arraycopy(items, index + 1, items, index, size - index - 1);
        items[--size] = null;

        // different threshold for resizing up,
        // so that it doesn't sit in a middle ground
        // continuously causing grows and trims
        if (items.length > DEFAULT_INIT_CAPACITY && size < items.length / 4) {
            resize(Math.max(items.length / 2, DEFAULT_INIT_CAPACITY));
        }

        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        items = new Object[DEFAULT_INIT_CAPACITY];
        size = 0;
    }

    @Override
    public int capacity() {
        return items.length;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return get(index++);
            }
        };
    }
}
