package algorithms.sorting;

import java.util.Comparator;

public class BubbleSort implements Sort {

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        for (int i = 0; i < arr.length - 1; i++) {
            boolean swapped = false;

            // i of the last elements are already finalized
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (comparator.compare(arr[j], arr[j + 1]) > 0) {
                    Sort.swap(arr, j, j + 1);
                    swapped = true;
                }
            }

            if (!swapped) break;
        }
    }
}
