package algorithms.sorting;

import java.util.Comparator;

/**
 * STABLE divide and conquer algo.
 * Recursively splits the array in two halves, sorts each, then merges them back.
 * Uses a reusable O(n) aux buffer: means not an in-place sort.
 * Skips merging when the halves are already in order, improving perf on
 * sorted and partially sorted inputs.
 *  Time:
 *  - Best:    O(n)         (already sorted, with merge skipping)
 *  - Average: O(n log n)
 *  - Worst:   O(n log n)
 */
public class MergeSort implements Sort {

    // exclusive r. [l, r)
    // means size = r - l
    private <T> void mergeSort(
            T[] arr,
            T[] buf, // reusable buffer to avoid allocation per call
            Comparator<? super T> comparator,
            int l, int r
    ) {
        if (r - l <= 1) return;

        int m = l + (r - l) / 2;

        mergeSort(arr, buf, comparator, l, m);
        mergeSort(arr, buf, comparator, m, r);

        // optimization, skip unnecessary merge
        if (comparator.compare(arr[m - 1], arr[m]) <= 0) {
            return;
        }

        // Copy the current range into the reusable buffer
        System.arraycopy(arr, l, buf, l, r - l);

        int li = l;
        int ri = m;
        int i = l;
        while (li < m && ri < r) {
            if (comparator.compare(buf[li], buf[ri]) <= 0) { // stable!
                arr[i++] = buf[li++];
            } else {
                arr[i++] = buf[ri++];
            }
        }

        while (li < m) {
            arr[i++] = buf[li++];
        }
        while (ri < r) {
            arr[i++] = buf[ri++];
        }
    }

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        T[] buf = arr.clone(); // can be an empty arr, but Java doesn't allow new T[]
        mergeSort(arr, buf, comparator, 0, arr.length);
    }

    @Override
    public boolean isStable() {
        return true;
    }
}
