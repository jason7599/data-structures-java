package algorithms.sorting;

import java.util.Comparator;

public class SelectionSort implements Sort {

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (comparator.compare(arr[j], arr[min]) < 0) {
                    min = j;
                }
            }
            Sort.swap(arr, i, min);
        }
    }

    @Override
    public boolean isStable() {
        return false;
    }
}
