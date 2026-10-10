package algorithms.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Modified insertion sort.
 * Instead of walking backwards one at a time, start with big strides.
 * Each iteration moves elements closer to their final position, leaving less work for future iterations.
 * Final iteration of gap = 1 is literally ordinary insertion sort,
 * but at that time the array is already a nearly sorted one.
 * This is very surprising. Technically it is one more loop than insertion sort,
 * but I'm seeing this consistently and significantly beats insertion sort
 * in every case except already sorted inputs.
 * Not a stable sort tho.
 * So it kinda sits in an awkward place.
 * If the input is truly tiny, you'd go with the stable insertion sort.
 * If the input is big, you'd go with an n log n algo anyway.
 */
public class ShellSort implements Sort {

    /**
     * Ciura's gap sequence (2001). Found by computer search, not derived from theory:
     * these gaps minimized the average comparisons on arrays up to a few thousand
     * elements. It's the best known sequence in practice, and nobody has proven how
     * good it is... magic.
     * The list stops at 1750 because that's where Ciura's search stopped. gaps()
     * extends it by multiplying by ~2.25, which is common practice, not something Ciura found.
     */
    private static final int[] CIURA = {1, 4, 10, 23, 57, 132, 301, 701, 1750};
    private static List<Integer> gaps(int n) {
        List<Integer> gaps = new ArrayList<>();
        for (int g : CIURA) {
            if (g < n) gaps.add(g);
        }
        // Ciura's list stops at 1750; extend it for larger arrays.
        int g = CIURA[CIURA.length - 1];
        while ((g = (int) (g * 2.25)) < n) {
            gaps.add(g);
        }
        return gaps;
    }

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        List<Integer> gaps = gaps(arr.length);
        for (int k = gaps.size() - 1; k >= 0; k--) {
            int gap = gaps.get(k);
            for (int i = gap; i < arr.length; i++) {
                T key = arr[i];
                int j = i - gap;
                while (j >= 0 && comparator.compare(arr[j], key) > 0) {
                    arr[j + gap] = arr[j];
                    j -= gap;
                }
                arr[j + gap] = key;
            }
        }
    }

    @Override
    public boolean isStable() {
        return false;
    }
}
