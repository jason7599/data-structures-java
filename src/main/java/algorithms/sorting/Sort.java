package algorithms.sorting;

import java.util.Comparator;

public interface Sort {

    <T> void sort(T[] arr, Comparator<? super T> comparator);

    default <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    default String name() {
        return getClass().getSimpleName();
    }

    static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
