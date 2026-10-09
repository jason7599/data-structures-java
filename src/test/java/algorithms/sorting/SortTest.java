package algorithms.sorting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

abstract class SortTest {

    protected abstract Sort newSort();

    static final int BENCHMARK_SIZE = 10_000;
    static final long SEED = 666;

    Sort sort;
    Random rng;

    @BeforeEach
    void setUp() {
        sort = newSort();
        rng = new Random(SEED);
    }

    @Test
    void empty() {
        assertSort(new Integer[0]);
    }

    @Test
    void single() {
        assertSort(new Integer[]{1});
    }

    @Test
    void two() {
        assertSort(new Integer[]{2, 1});
    }

    @Test
    void equal() {
        assertSort(new Integer[]{2, 2, 2});
    }

    @Test
    void benchmark() {
        StringBuilder header = new StringBuilder(String.format("%-16s", "ms"));
        StringBuilder row = new StringBuilder(String.format("%-16s", sort.name()));

        for (InputShape shape : InputShape.values()) {
            Integer[] arr = shape.generate(BENCHMARK_SIZE, rng);

            Integer[] expected = arr.clone();
            Arrays.sort(expected);

            long start = System.nanoTime();
            sort.sort(arr);
            double ms = (System.nanoTime() - start) / 1_000_000.0;

            assertArrayEquals(expected, arr, () -> sort.name() + " failed on " + shape);

            header.append(String.format("%15s", shape));
            row.append(String.format("%15.2f", ms));
        }

        System.out.println(header + "\n" + row + "\n");
    }

    enum InputShape {
        RANDOM, SORTED, REVERSED, FEW_UNIQUE, NEARLY_SORTED;

        Integer[] generate(int n, Random rng) {
            Integer[] a = new Integer[n];
            switch (this) {
                case RANDOM -> { for (int i = 0; i < n; i++) a[i] = rng.nextInt(); }
                case SORTED -> { for (int i = 0; i < n; i++) a[i] = i; }
                case REVERSED -> { for (int i = 0; i < n; i++) a[i] = n - i; }
                case FEW_UNIQUE -> { for (int i = 0; i < n; i++) a[i] = rng.nextInt(10); }
                case NEARLY_SORTED -> {
                    for (int i = 0; i < n; i++) a[i] = i;
                    // Swap ~2% of positions with random partners.
                    for (int k = 0; n > 1 && k < n / 50 + 1; k++) {
                        Sort.swap(a, rng.nextInt(n), rng.nextInt(n));
                    }
                }
            }
            return a;
        }
    }

    void assertSort(Integer[] arr) {
        Integer[] expected = arr.clone();
        Arrays.sort(expected);

        Integer[] actual = arr.clone();
        sort.sort(actual);

        assertArrayEquals(expected, actual,
                () -> sort.name() + " failed");
    }

}