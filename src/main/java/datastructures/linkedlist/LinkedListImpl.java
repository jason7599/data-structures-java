package datastructures.linkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListImpl<T> implements LinkedList<T> {

    private static class Node<T> {
        final T data;
        Node<T> next;
        Node<T> prev;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    @Override
    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return head.data;
    }

    @Override
    public T getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return tail.data;
    }

    @Override
    public void pushFirst(T data) {
        Node<T> first = new Node<>(data);
        if (isEmpty()) {
            head = tail = first;
        } else {
            first.next = head;
            head.prev = first;
            head = first;
        }
        size++;
    }

    @Override
    public void pushLast(T data) {
        Node<T> last = new Node<>(data);
        if (isEmpty()) {
            head = tail = last;
        } else {
            tail.next = last;
            last.prev = tail;
            tail = last;
        }
        size++;
    }

    @Override
    public T popFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        T data = head.data;

        if (head == tail) {
            clear();
        } else {
            // head != tail ensures head.next exists
            head = head.next;
            head.prev = null;
            size--;
        }

        return data;
    }

    @Override
    public T popLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        T data = tail.data;

        if (head == tail) {
            clear();
        } else {
            tail = tail.prev;
            tail.next = null;
            size--;
        }

        return data;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> node;
        if (index < size / 2) {
            node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            node = tail;
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
        }

        return node.data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        // equal to head == null or tail == null
        return size == 0;
    }

    // Let GC take care
    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
