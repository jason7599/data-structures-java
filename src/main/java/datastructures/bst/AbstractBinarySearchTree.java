package datastructures.bst;

import datastructures.sortedmultiset.SortedMultiset;

import java.util.*;

public abstract class AbstractBinarySearchTree<
        T extends Comparable<? super T>,
        N extends AbstractNode<T, N>>
implements SortedMultiset<T> {

    protected N root;
    protected int size;

    protected N find(N n, T data) {
        if (n == null) return null;
        int cmp = data.compareTo(n.data);
        if (cmp == 0) return n;
        return cmp < 0 ? find(n.left, data) : find(n.right, data);
    }

    protected N min(N n) {
        if (n == null) return null;
        while (n.left != null) n = n.left;
        return n;
    }

    protected N max(N n) {
        if (n == null) return null;
        while (n.right != null) n = n.right;
        return n;
    }

    // Largest element < data (or <= data if inclusive), or null.
    private T below(T data, boolean inclusive) {
        N n = root;
        T res = null;
        while (n != null) {
            int cmp = data.compareTo(n.data);
            if (cmp > 0 || (inclusive && cmp == 0)) {
                res = n.data;   // candidate; look for a bigger one
                n = n.right;
            } else {
                n = n.left;
            }
        }
        return res;
    }

    // Smallest element > data (or >= data if inclusive), or null.
    private T above(T data, boolean inclusive) {
        N n = root;
        T res = null;
        while (n != null) {
            int cmp = data.compareTo(n.data);
            if (cmp < 0 || (inclusive && cmp == 0)) {
                res = n.data;   // candidate; look for a smaller one
                n = n.left;
            } else {
                n = n.right;
            }
        }
        return res;
    }

    @Override
    public boolean contains(T data) {
        return find(root, data) != null;
    }

    @Override
    public int count(T data) {
        N n = find(root, data);
        return n == null ? 0 : n.count;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public T min() {
        N n = min(root);
        return n == null ? null : n.data;
    }

    @Override
    public T max() {
        N n = max(root);
        return n == null ? null : n.data;
    }

    @Override
    public T floor(T data) {
        return below(data, true);
    }

    @Override
    public T ceiling(T data) {
        return above(data, true);
    }

    @Override
    public T lower(T data) {
        return below(data, false);
    }

    @Override
    public T higher(T data) {
        return above(data, false);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final Deque<N> stack = new ArrayDeque<>();
            private N current;
            private int remaining; // copies current.data to support multiple elements

            { pushLeftSpine(root); } // initialize stack

            private void pushLeftSpine(N n) {
                while (n != null) {
                    stack.push(n);
                    n = n.left;
                }
            }

            @Override
            public boolean hasNext() {
                return remaining > 0 || !stack.isEmpty();
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();

                if (remaining == 0) { // also covers init case
                    current = stack.pop();
                    remaining = current.count;
                    pushLeftSpine(current.right);
                }

                remaining--;
                return current.data;
            }
        };
    }
}
