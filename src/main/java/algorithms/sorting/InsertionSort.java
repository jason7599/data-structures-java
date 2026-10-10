package algorithms.sorting;

import java.util.Comparator;

public class InsertionSort implements Sort {

    /**
     * For each `i` starting from 1, walk backwards until the first smaller or equal element is found,
     * then insert after that position. Thanks to this potential of early termination (per `i`),
     * works fantastically on sorted or nearly sorted inputs.
     */
    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    @Override
    public boolean isStable() {
        return true;
    }
}
