package datastructures.bst;

class UnbalancedBinarySearchTreeTest extends BinarySearchTreeTest {
    @Override
    protected <T extends Comparable<? super T>> BinarySearchTree<T> newTree() {
        return new UnbalancedBinarySearchTree<>();
    }
}
