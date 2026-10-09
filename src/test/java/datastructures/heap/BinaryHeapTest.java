package datastructures.heap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BinaryHeapTest {

    Heap<Integer> heap;

    @BeforeEach
    void setUp() {
        heap = new BinaryHeap<>();
    }

    @Test
    void emptyHeap() {
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        assertThrows(NoSuchElementException.class, heap::peek);
        assertThrows(NoSuchElementException.class, heap::pop);
    }

    @Test
    void singleElement() {
        heap.push(42);
        assertEquals(42, heap.peek());
        assertEquals(1, heap.size());
        assertEquals(42, heap.pop());
        assertTrue(heap.isEmpty());
        assertThrows(NoSuchElementException.class, heap::pop);
    }

    @Test
    void popsInAscendingOrder() {
        for (int x : new int[]{5, 3, 8, 1, 9, 2, 7}) heap.push(x);

        for (int expected : new int[]{1, 2, 3, 5, 7, 8, 9}) {
            assertEquals(expected, heap.pop());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    void reverseOrderIsMaxHeap() {
        Heap<Integer> heap = new BinaryHeap<>(Comparator.reverseOrder());
        for (int x : new int[]{5, 3, 8, 1, 9}) heap.push(x);

        for (int expected : new int[]{9, 8, 5, 3, 1}) {
            assertEquals(expected, heap.pop());
        }
    }

    @Test
    void duplicatesAreKept() {
        for (int x : new int[]{3, 1, 3, 1, 2, 3}) heap.push(x);

        assertEquals(6, heap.size());
        for (int expected : new int[]{1, 1, 2, 3, 3, 3}) {
            assertEquals(expected, heap.pop());
        }
    }

    @Test
    void clearResetsAndHeapIsReusable() {
        for (int i = 0; i < 100; i++) heap.push(i);
        heap.clear();

        assertTrue(heap.isEmpty());
        assertThrows(NoSuchElementException.class, heap::peek);

        heap.push(7);
        heap.push(3);
        assertEquals(3, heap.pop());
        assertEquals(7, heap.pop());
    }

    // ---------- stress test against java.util.PriorityQueue ----------

    @ParameterizedTest(name = "seed {0}")
    @ValueSource(longs = {1, 2, 3, 42, 1337})
    void matchesPriorityQueue(long seed) {
        Random rng = new Random(seed);
        PriorityQueue<Integer> ref = new PriorityQueue<>();

        final int ops = 200_000;
        for (int step = 0; step < ops; step++) {
            String ctx = "seed=" + seed + ", step=" + step;

            // Grow during the first half, shrink during the second, so the
            // backing array goes through both resize-up and trim-down.
            double pushChance = step < ops / 2 ? 0.65 : 0.35;

            if (ref.isEmpty() || rng.nextDouble() < pushChance) {
                // small value range -> lots of duplicates
                int x = rng.nextInt(1000) - 500;
                heap.push(x);
                ref.add(x);
            } else {
                assertEquals(ref.poll(), heap.pop(), ctx);
            }

            assertEquals(ref.size(), heap.size(), ctx);
            if (!ref.isEmpty()) {
                assertEquals(ref.peek(), heap.peek(), ctx);
            }

            // occasionally wipe everything
            if (rng.nextInt(50_000) == 0) {
                heap.clear();
                ref.clear();
            }
        }

        // drain whatever is left
        while (!ref.isEmpty()) {
            assertEquals(ref.poll(), heap.pop(), "drain, seed=" + seed);
        }
        assertTrue(heap.isEmpty());
    }
}