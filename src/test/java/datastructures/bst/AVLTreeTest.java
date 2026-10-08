package datastructures.bst;

class AVLTreeTest extends BinarySearchTreeTest {

    @Override
    protected <T extends Comparable<? super T>> BinarySearchTree<T> newTree() {
        return new AVLTree<>();
    }
}