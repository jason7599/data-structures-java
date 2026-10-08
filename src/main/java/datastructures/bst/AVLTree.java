package datastructures.bst;

public class AVLTree<T extends Comparable<? super T>> implements BinarySearchTree<T> {

    private static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;
        int height = 0;
        int count = 1;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> root;
    private int size;

    private int height(Node<T> n) {
        return n == null ? -1 : n.height;
    }

    /**
     * < -1: right-heavy
     * > 1: left-heavy
     */
    private int balanceFactor(Node<T> n) {
        return height(n.left) - height(n.right);
    }

    private void updateHeight(Node<T> n) {
        n.height = Math.max(height(n.left), height(n.right)) + 1;
    }

    private Node<T> rotateRight(Node<T> n) {
        Node<T> newParent = n.left;
        n.left = newParent.right;
        newParent.right = n;

        updateHeight(n);
        updateHeight(newParent);

        return newParent;
    }

    private Node<T> rotateLeft(Node<T> n) {
        Node<T> newParent = n.right;
        n.right = newParent.left;
        newParent.left = n;

        updateHeight(n);
        updateHeight(newParent);

        return newParent;
    }

    private Node<T> rebalance(Node<T> n) {
        if (n == null) return null;

        updateHeight(n);
        int bf = balanceFactor(n);
        if (Math.abs(bf) <= 1) return n;

        if (bf < -1) {  // right-heavy
            if (balanceFactor(n.right) > 0) { // rl
                n.right = rotateRight(n.right);
            }
            n = rotateLeft(n);
        } else { // left-heavy
            if (balanceFactor(n.left) < 0) { // lr
                n.left = rotateLeft(n.left);
            }
            n = rotateRight(n);
        }

        return n;
    }

    private Node<T> insert(Node<T> n, T data) {
        if (n == null) {
            size++;
            return new Node<>(data);
        }

        int cmp = data.compareTo(n.data);
        if (cmp == 0) {
            n.count++;
            size++;
            return n;
        }

        if (cmp < 0) n.left = insert(n.left, data);
        else n.right = insert(n.right, data);

        return rebalance(n);
    }

    // unlink the min node of subtree n,
    // rebalance on the way back up
    private Node<T> removeMin(Node<T> n) {
        // found min, replace with right
        if (n.left == null) return n.right;

        n.left = removeMin(n.left);

        return rebalance(n);
    }

    private Node<T> remove(Node<T> n, T data, boolean all) {
        if (n == null) return null; // no match

        int cmp = data.compareTo(n.data);

        if (cmp < 0) n.left = remove(n.left, data, all);
        else if (cmp > 0) n.right = remove(n.right, data, all);
        else {
            if (!all && n.count > 1) {
                n.count--;
                size--;
            } else {
                size -= n.count;

                // no height adjustment needed here
                // parent will update its height as recursion unfolds
                if (n.left == null) n = n.right;
                else if (n.right == null) n = n.left;
                else {
                    // has both children, take inorder successor
                    Node<T> succ = min(n.right);
                    n.data = succ.data;
                    n.count = succ.count;

                    n.right = removeMin(n.right);
                }
            }
        }

        return rebalance(n);
    }

    private Node<T> min(Node<T> n) {
        while (n.left != null) n = n.left;
        return n;
    }

    private Node<T> max(Node<T> n) {
        while (n.right != null) n = n.right;
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
        return min(root).data;
    }

    @Override
    public T max() {
        if (root == null) return null;
        return max(root).data;
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
