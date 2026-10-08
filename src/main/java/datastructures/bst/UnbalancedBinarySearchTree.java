package datastructures.bst;

public class UnbalancedBinarySearchTree<T extends Comparable<? super T>>
        extends AbstractBinarySearchTree<T, UnbalancedBinarySearchTree.Node<T>> {

    protected static class Node<T> extends AbstractNode<T, Node<T>> {
        Node(T data) {
            super(data);
        }
    }

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
    public void remove(T data) {
        root = remove(root, data, false);
    }

    @Override
    public void removeAll(T data) {
        root = remove(root, data, true);
    }
}
