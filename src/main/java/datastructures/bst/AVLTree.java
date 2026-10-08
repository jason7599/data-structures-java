package datastructures.bst;

public class AVLTree<T extends Comparable<? super T>>
        extends AbstractBinarySearchTree<T, AVLTree.Node<T>> {

    protected static class Node<T> extends AbstractNode<T, Node<T>> {
        protected int height;

        Node(T data) {
            super(data);
        }
    }

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

    @Override
    public void insert(T data) {
        root = insert(root, data);
    }

    @Override
    public void remove(T data) {
        root = remove(root, data, false);
    }

    @Override
    public void removeAll(T data) {
        root = remove(root, data, true);
    }
}
