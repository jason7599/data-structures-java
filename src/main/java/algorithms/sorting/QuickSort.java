package algorithms.sorting;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class QuickSort implements Sort {

    public enum PivotStrategy {
        LAST, RANDOM, MEDIAN_OF_THREE
    }

    private final PivotStrategy strategy;

    public QuickSort(PivotStrategy strategy) {
        this.strategy = strategy;
    }

    // exclusive r. [l, r)
    private <T> void quickSort(T[] arr, Comparator<? super T> comparator, int l, int r) {
        if (r - l <= 1) return;

        int p = partition(arr, comparator, l, r);

        quickSort(arr, comparator, l, p);     // elements < pivot
        quickSort(arr, comparator, p + 1, r); // elements >= pivot (pivot itself excluded)
    }

    // Lomuto partition, returns the pivot's final index.
    // r - 1 = pivot
    // [l, store) < pivot
    // [store, j) >= pivot
    // [j, r - 1) not checked yet
    private <T> int partition(T[] arr, Comparator<? super T> comparator, int l, int r) {
        int pivotIndex = choosePivot(arr, comparator, l, r);
        T pivot = arr[pivotIndex];

        Sort.swap(arr, pivotIndex, r - 1);

        int store = l;

        for (int j = l; j < r - 1; j++) {
            if (comparator.compare(arr[j], pivot) <= 0) {
                if (store != j) {
                    Sort.swap(arr, store, j);
                }
                store++;
            }
        }

        Sort.swap(arr, store, r - 1);
        return store;
    }

    private <T> int choosePivot(T[] arr, Comparator<? super T> comparator, int l, int r) {
        return switch (strategy) {
            case LAST -> r - 1;
            case RANDOM -> l + ThreadLocalRandom.current().nextInt(r - l);
            case MEDIAN_OF_THREE -> medianOfThree(arr, comparator, l, l + (r - l) / 2, r - 1);
        };
    }

    // Returns the index (among a, b, c) holding the median value.
    private <T> int medianOfThree(T[] arr, Comparator<? super T> comparator, int a, int b, int c) {
        T x = arr[a], y = arr[b], z = arr[c];
        if (comparator.compare(x, y) < 0) {
            if (comparator.compare(y, z) < 0) return b;      // x < y < z
            return comparator.compare(x, z) < 0 ? c : a;     // x < z <= y, or z <= x < y
        } else {
            if (comparator.compare(x, z) < 0) return a;      // y <= x < z
            return comparator.compare(y, z) < 0 ? c : b;     // y < z <= x, or z <= y <= x
        }
    }

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        quickSort(arr, comparator, 0, arr.length);
    }

    @Override
    public boolean isStable() {
        return false;
    }

    @Override
    public String name() {
        return getClass().getSimpleName() + "(" + strategy.name() + ")";
    }
}
