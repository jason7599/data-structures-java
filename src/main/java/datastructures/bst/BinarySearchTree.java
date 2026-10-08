package datastructures.bst;

public interface BinarySearchTree<T extends Comparable<? super T>>  {
    void insert(T data);
    boolean contains(T data);
    int count(T data);

    void remove(T data);    // remove one occurrence
    void removeAll(T data); // remove all
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
