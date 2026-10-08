package datastructures.bst;

public abstract class AbstractNode<T, N extends AbstractNode<T, N>> {
    protected T data;
    protected N left;
    protected N right;
    protected int count = 1;

    protected AbstractNode(T data) {
        this.data = data;
    }
}
