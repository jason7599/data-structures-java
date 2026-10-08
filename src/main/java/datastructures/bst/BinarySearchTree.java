package datastructures.bst;

public interface BinarySearchTree<T extends Comparable<? super T>>  {
    void insert(T data);
    boolean contains(T data);
    int count(T data);

    boolean remove(T data); // remove one occurrence
    int removeAll(T data);  // remove all, return how many were removed
    void clear();

    T min();
    T max();

    T floor(T data);    // largest <= data or null
    T ceiling(T data);  // smallest >= data or null
    T lower(T data);    // largest < data or null
    T higher(T data);   // smallest > data or null

    int size();         // total count, not distinct
    boolean isEmpty();
}
