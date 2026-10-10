package datastructures.skiplist;

import datastructures.sortedmultiset.SortedMultiset;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SkipList<T extends Comparable<? super T>> implements SortedMultiset<T> {

    private static final class Node<T> {
        final T data;           // null only for the head sentinel
        final Node<T>[] next;   // next.length = this node's level. next[i] points to its successor in lane i.
        int count = 1;

        @SuppressWarnings("unchecked")
        Node(T data, int level) {
            this.data = data;
            this.next = (Node<T>[]) new Node[level];
        }
    }

    private static final int DEFAULT_MAX_LEVEL = 32;
    private static final double DEFAULT_P = 0.5;

    public final int maxLevel;
    public final double p;
    private final Random random;

    private final Node<T> head;

    private int levelCount = 1; // lanes currently in use; top lane index = levelCount - 1
    private int size;           // total count, including dupes

    public SkipList() {
        this(DEFAULT_MAX_LEVEL, DEFAULT_P, ThreadLocalRandom.current());
    }

    public SkipList(Random random) {
        this(DEFAULT_MAX_LEVEL, DEFAULT_P, random);
    }

    public SkipList(int maxLevel, double p, Random random) {
        if (maxLevel < 1) throw new IllegalArgumentException("maxLevel must be >= 1");
        if (!(p > 0 && p < 1)) throw new IllegalArgumentException("p must be in (0, 1)");

        this.maxLevel = maxLevel;
        this.p = p;
        this.head = new Node<>(null, maxLevel); // sentinel
        this.random = random;
    }

    // 1 + number of successful coin flips, capped at maxLevel
    private int randomLevel() {
        int height = 1;
        while (height < maxLevel && random.nextDouble() < p) height++;
        return height;
    }

    private Node<T>[] findPredecessors(T data) {
        // preds[i] = predecessor of data in level `i`
        // only checks on i = 0 ~ levelCount - 1
        @SuppressWarnings("unchecked")
        Node<T>[] preds = (Node<T>[]) new Node[maxLevel];

        Node<T> n = head;
        for (int level = levelCount - 1; level >= 0; level--) {
            // for each lane in use, find the predecessor for this data
            // note how n doesn't get reset per lane! Awesome
            // So the next iteration doesn't have to search from the beginning.
            while (n.next[level] != null && n.next[level].data.compareTo(data) < 0) {
                n = n.next[level];
            }
            preds[level] = n;
        }
        return preds;
    }

    private Node<T> findNode(T data) {
        Node<T> n = findPredecessors(data)[0].next[0];
        return (n != null && n.data.compareTo(data) == 0) ? n : null;
    }

    private void remove(T data, boolean all) {
        Node<T>[] update = findPredecessors(data);

        Node<T> node = update[0].next[0];
        // no match
        if (node == null || node.data.compareTo(data) != 0) {
            return;
        }

        if (!all && node.count > 1) {
            node.count--;
            size--;
            return;
        }

        for (int level = 0; level < node.next.length; level++) {
            update[level].next[level] = node.next[level];
        }

        while (levelCount > 1 && head.next[levelCount - 1] == null) {
            levelCount--;
        }
        size -= node.count;
    }

    @Override
    public void insert(T data) {
        Node<T>[] update = findPredecessors(data);

        // dupe
        Node<T> candidate = update[0].next[0];
        if (candidate != null && candidate.data.compareTo(data) == 0) {
            candidate.count++;
            size++;
            return;
        }

        int level = randomLevel();
        if (level > levelCount) {
            // Lanes above the current top weren't searched,
            // head is now the predecessor for this new data
            for (int i = levelCount; i < level; i++) {
                update[i] = head;
            }
            levelCount = level;
        }

        Node<T> inserted = new Node<>(data, level);
        for (int i = 0; i < level; i++) {
            inserted.next[i] = update[i].next[i]; // succeed the predecessor's successors (nice sentence)
            update[i].next[i] = inserted;
        }
        size++;
    }

    @Override
    public boolean contains(T data) {
        return findNode(data) != null;
    }

    @Override
    public int count(T data) {
        Node<T> n = findNode(data);
        return n == null ? 0 : n.count;
    }

    @Override
    public void remove(T data) {
        remove(data, false);
    }

    @Override
    public void removeAll(T data) {
        remove(data, true);
    }

    @Override
    public void clear() {
        for (; levelCount > 0; levelCount--) {
            head.next[levelCount - 1] = null;
        }
        levelCount = 1;
        size = 0;
    }

    @Override
    public T min() {
        return head.next[0] == null ? null : head.next[0].data;
    }

    @Override
    public T max() {
        Node<T> n = head;
        for (int level = levelCount - 1; level >= 0; level--) {
            while (n.next[level] != null) {
                n = n.next[level];
            }
        }
        // n might be head, but head has a sentinel value of null anyway
        return n.data;
    }

    @Override
    public T floor(T data) {
        Node<T> n = findPredecessors(data)[0];
        // exact match
        if (n.next[0] != null && n.next[0].data.compareTo(data) == 0) {
            n = n.next[0];
        }
        return n.data;
    }

    @Override
    public T ceiling(T data) {
        Node<T> n = findPredecessors(data)[0].next[0];
        return n == null ? null : n.data; // also covers the exact match case
    }

    @Override
    public T lower(T data) {
        return findPredecessors(data)[0].data;
    }

    @Override
    public T higher(T data) {
        Node<T> n = findPredecessors(data)[0].next[0];
        if (n == null) return null;

        // have to advance one more
        if (n.data.compareTo(data) == 0) {
            return n.next[0] == null ? null : n.next[0].data;
        }

        return n.data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> n = head.next[0];
            private int remaining = (n == null) ? 0 : n.count;  // copies left to emit from n

            @Override
            public boolean hasNext() {
                return n != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = n.data;
                if (--remaining == 0) {  // done with this node, move on
                    n = n.next[0];
                    remaining = (n == null) ? 0 : n.count;
                }
                return data;
            }
        };
    }
}
