package datastructures.bst;

public class UnbalancedBinarySearchTree<T extends Comparable<? super T>> implements BinarySearchTree<T> {

    private static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;
        int count = 1;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> root;
    private int size;

    // ================ private helper methods ================

    private Node<T> insert(Node<T> n, T data) {
        if (n == null) {
            size++;
            return new Node<>(data);
        }

        int cmp = data.compareTo(n.data);
        if (cmp < 0) n.left = insert(n.left, data);
        else if (cmp > 0) n.right = insert(n.right, data);
        else {
            // exact match, increment count
            n.count++;
            size++;
        }

        return n;
    }

    private Node<T> find(Node<T> n, T data) {
        if (n == null) return null;

        int cmp = data.compareTo(n.data);
        if (cmp == 0) return n;
        return cmp < 0 ? find(n.left, data) : find(n.right, data);
    }

    // Largest element < data (or <= data if inclusive), or null.
    private T below(T data, boolean inclusive) {
        Node<T> n = root;
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
        Node<T> n = root;
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

    private Node<T> remove(Node<T> n, T data, boolean all) {
        if (n == null) return null; // no match

        int cmp = data.compareTo(n.data);

        // match
        if (cmp == 0) {
            if (!all && n.count > 1) {
                n.count--;
                size--;
                return n;
            }

            size -= n.count;

            if (n.left == null) return n.right;
            if (n.right == null) return n.left;

            // has both children
            // swap values with inorder successor (smallest in right subtree)
            // then remove inorder successor

            // n.right is the successor
            if (n.right.left == null) {
                n.data = n.right.data;
                n.count = n.right.count;

                n.right = n.right.right;
            } else {
                Node<T> succParent = n.right;
                while (succParent.left.left != null) {
                    succParent = succParent.left;
                }

                Node<T> succ = succParent.left;
                n.data = succ.data;
                n.count = succ.count;

                succParent.left = succ.right;
            }

            return n;
        }

        if (cmp < 0) n.left = remove(n.left, data, all);
        else n.right = remove(n.right, data, all);

        return n;
    }

    // ========================================================

    @Override
    public void insert(T data) {
        root = insert(root, data);
    }

    @Override
    public boolean contains(T data) {
        return find(root, data) != null;
    }

    @Override
    public int count(T data) {
        Node<T> n = find(root, data);
        return n == null ? 0 : n.count;
    }

    @Override
    public void remove(T data) {
        root = remove(root, data, false);
    }

    @Override
    public void removeAll(T data) {
        root = remove(root, data, true);
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public T min() {
        if (root == null) return null;
        Node<T> n = root;
        while (n.left != null) {
            n = n.left;
        }
        return n.data;
    }

    @Override
    public T max() {
        if (root == null) return null;
        Node<T> n = root;
        while (n.right != null) {
            n = n.right;
        }
        return n.data;
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
    public boolean isEmpty() {
        return size == 0;
    }
}
