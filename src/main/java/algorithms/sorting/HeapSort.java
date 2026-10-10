package algorithms.sorting;

import java.util.Comparator;

/**
 * UNSTABLE in-place selection-based algo.
 * Treats the array itself as a binary max-heap (0-based: children at 2i+1, 2i+2).
 * 1. Heapify bottom-up: sift down every internal node from n/2 - 1 to 0. O(n).
 * 2. Repeatedly swap the root (max) to the end of the heap, shrink the heap
 *    by one, and sift the new root down. The sorted region grows from the back.
 * O(1) extra space, no recursion needed (siftDown can be iterative).
 * Poor cache locality (parent/child jumps), so it's usually slower than
 * QuickSort in practice. Mainly used as the worst-case fallback in IntroSort.
 *  Time:
 *  - Best:    O(n)         (all keys equal: strict comparison stops sifts early)
 *  - Average: O(n log n)
 *  - Worst:   O(n log n)
 */
public class HeapSort implements Sort {

    // 0 based indexing
    private static int left(int i) { return i * 2 + 1; }
    private static int right(int i) { return i * 2 + 2; }

    private static <T> boolean greater(
            T[] arr,
            Comparator<? super T> cmp,
            int i, int j
    ) {
        return cmp.compare(arr[i], arr[j]) > 0;
    }

    private static <T> void siftDown(T[] arr, Comparator<? super T> cmp, int i, int size) {
        if (left(i) >= size) return; // leaf

        // find the greater child
        int next;
        if (right(i) >= size) next = left(i);
        else next = greater(arr, cmp, left(i), right(i)) ? left(i) : right(i);

        if (greater(arr, cmp, next, i)) {
            Sort.swap(arr, next, i);
            siftDown(arr, cmp, next, size);
        }
    }

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> cmp) {
        final int N = arr.length;

        // Build a max-heap bottom-up: O(n)
        // Walk backwards from the last internal node,
        // indices from N / 2 are leaf nodes, which are already valid heaps.
        for (int i = N / 2 - 1; i >= 0; i--) {
            siftDown(arr, cmp, i, N);
        }

        // Now arr is a max-heap, with arr[0] being the max value.
        // Each iteration,
        // - place the 0th(root) value to the end of the array
        // - re-heapify arr[0..end). arr[end..] is already sorted and excluded
        for (int end = N - 1; end > 0; end--) {
            Sort.swap(arr, 0, end);
            siftDown(arr, cmp, 0, end);
        }
    }

    @Override
    public boolean isStable() {
        return false;
    }
}
