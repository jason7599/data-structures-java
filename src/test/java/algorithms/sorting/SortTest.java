package algorithms.sorting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Runs every test below once per sort listed in sorts().
 * Adding a new sort = adding one line there. Sorts must be stateless,
 * since one instance is shared by all tests for that sort.
 */
@ParameterizedClass
@MethodSource("sorts")
class SortTest {

    /** Every sort under test, with the largest benchmark size it finishes in reasonable time. */
    static Stream<Arguments> sorts() {
        return Stream.of(
                sort(new BubbleSort(), 10_000),
                sort(new InsertionSort(), 100_000),
                sort(new SelectionSort(), 10_000),
                sort(new ShellSort(), 1_000_000),
                sort(new MergeSort(), 10_000_000)
        );
    }

    private static Arguments sort(Sort sort, int maxBenchmarkSize) {
        return Arguments.argumentSet(sort.name(), sort, maxBenchmarkSize);
    }

    static final int BENCHMARK_SIZE =
            Integer.parseInt(System.getProperty("benchmark.size", "10_000").replace("_", ""));

    @Parameter(0)
    Sort sort;

    @Parameter(1)
    int maxBenchmarkSize;

    static final long SEED = 666;

    Random rng;

    @BeforeEach
    void setUp() {
        rng = new Random(SEED);
    }

    // ---- Edge cases ----

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

    // ---- Stability ----

    record Item(int key, int originalIndex) {}

    @Test
    void stable() {
        if (!sort.isStable()) {
            return;
        }

        // Many duplicate keys; originalIndex tells equal items apart.
        Item[] items = new Item[1_000];
        for (int i = 0; i < items.length; i++) {
            items[i] = new Item(rng.nextInt(10), i);
        }

        sort.sort(items, Comparator.comparingInt(Item::key));

        for (int i = 1; i < items.length; i++) {
            Item prev = items[i - 1];
            Item cur = items[i];
            final int pos = i;
            assertTrue(prev.key() <= cur.key(),
                    () -> sort.name() + " is not sorted at position " + pos);
            if (prev.key() == cur.key()) {
                assertTrue(prev.originalIndex() < cur.originalIndex(),
                        () -> sort.name() + " reordered equal keys at position " + pos);
            }
        }
    }

    // ---- Benchmark (also checks correctness on bigger inputs) ----

    @Test
    void benchmark() {
        final int n = BENCHMARK_SIZE;
        assumeTrue(n <= maxBenchmarkSize, () -> "too slow for " + sort.name() + " at n = " + n);

        StringBuilder header = new StringBuilder(String.format("%-28s", "ms"));
        StringBuilder row = new StringBuilder(String.format("%-28s", sort.name() + String.format(" (n=%,d)", n)));

        for (InputShape shape : InputShape.values()) {
            Integer[] arr = shape.generate(n, rng);

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

    // ---- Helpers ----

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
                () -> sort.name() + " failed on " + Arrays.toString(arr));
    }
}