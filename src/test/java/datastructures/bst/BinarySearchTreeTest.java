package datastructures.bst;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class BinarySearchTreeTest {


    protected abstract <T extends Comparable<? super T>> BinarySearchTree<T> newTree();

    // ---------------------------------------------------------------
    // Stress test: random ops against a TreeMap of counts
    // ---------------------------------------------------------------

    @Test
    void matchesTreeMapUnderRandomOps() {
        for (long seed = 0; seed < 20; seed++) {
            stressWithSeed(seed);
        }
    }

    private void stressWithSeed(long seed) {
        Random rng = new Random(seed);
        BinarySearchTree<Integer> tree = newTree();
        TreeMap<Integer, Integer> ref = new TreeMap<>();  // value -> count
        int refSize = 0;

        // Small key range = lots of duplicates and hits on existing keys.
        // Large key range = mostly unique keys and deeper trees.
        int keyRange = rng.nextBoolean() ? 50 : 10_000;
        // At 20% inserts roughly balance removals, so the tree keeps hitting empty.
        int insertPct = 20 + rng.nextInt(41);

        for (int step = 0; step < 5_000; step++) {
            String ctx = "seed=" + seed + " step=" + step;
            int x = rng.nextInt(keyRange);

            if (rng.nextInt(500) == 0) {
                tree.clear();
                ref.clear();
                refSize = 0;
            } else {
                int roll = rng.nextInt(100);

                if (roll < insertPct) {
                    tree.insert(x);
                    ref.merge(x, 1, Integer::sum);
                    refSize++;
                } else if (roll < insertPct + 15) {
                    tree.remove(x);
                    Integer c = ref.get(x);
                    if (c != null) {
                        if (c == 1) ref.remove(x);
                        else ref.put(x, c - 1);
                        refSize--;
                    }
                } else if (roll < insertPct + 20) {
                    tree.removeAll(x);
                    Integer c = ref.remove(x);
                    if (c != null) refSize -= c;
                } else {
                    switch (rng.nextInt(8)) {
                        case 0 -> assertEquals(ref.containsKey(x), tree.contains(x), ctx + " contains(" + x + ")");
                        case 1 -> assertEquals(ref.getOrDefault(x, 0), tree.count(x), ctx + " count(" + x + ")");
                        case 2 -> assertEquals(ref.floorKey(x), tree.floor(x), ctx + " floor(" + x + ")");
                        case 3 -> assertEquals(ref.ceilingKey(x), tree.ceiling(x), ctx + " ceiling(" + x + ")");
                        case 4 -> assertEquals(ref.lowerKey(x), tree.lower(x), ctx + " lower(" + x + ")");
                        case 5 -> assertEquals(ref.higherKey(x), tree.higher(x), ctx + " higher(" + x + ")");
                        case 6 -> assertEquals(ref.isEmpty() ? null : ref.firstKey(), tree.min(), ctx + " min");
                        default -> assertEquals(ref.isEmpty() ? null : ref.lastKey(), tree.max(), ctx + " max");
                    }
                }
            }

            assertEquals(refSize, tree.size(), ctx + " size");
            assertEquals(refSize == 0, tree.isEmpty(), ctx + " isEmpty");
            assertEquals(expand(ref), toList(tree), ctx + " contents");
        }
    }

    // ---------------------------------------------------------------
    // Sorted input: worst case for naive BSTs, rotation workout for AVL
    // ---------------------------------------------------------------

    @Test
    void ascendingThenDescendingInsertsStaySorted() {
        BinarySearchTree<Integer> tree = newTree();
        List<Integer> expected = new ArrayList<>();

        for (int i = 0; i < 2_000; i++) {
            tree.insert(i);
            expected.add(i);
        }
        assertEquals(expected, toList(tree));

        tree.clear();
        for (int i = 1_999; i >= 0; i--) {
            tree.insert(i);
        }
        assertEquals(expected, toList(tree));

        // knock out every other element, from the middle outward-ish
        for (int i = 0; i < 2_000; i += 2) {
            tree.remove(i);
        }
        expected.removeIf(v -> v % 2 == 0);
        assertEquals(expected, toList(tree));
        assertEquals(expected.size(), tree.size());
    }

    // ---------------------------------------------------------------
    // Iterator contract
    // ---------------------------------------------------------------

    @Test
    void emptyTreeIteratesNothing() {
        for (var ignored : newTree()) {
            fail("should not iterate over an empty tree");
        }
    }

    @Test
    void duplicatesAreYieldedCountTimes() {
        BinarySearchTree<Integer> tree = newTree();
        tree.insert(5);
        tree.insert(3);
        tree.insert(5);
        tree.insert(5);

        assertEquals(List.of(3, 5, 5, 5), toList(tree));
    }

    @Test
    void nextThrowsWhenExhausted() {
        BinarySearchTree<Integer> tree = newTree();
        tree.insert(1);

        Iterator<Integer> it = tree.iterator();
        assertEquals(1, it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void hasNextDoesNotAdvance() {
        BinarySearchTree<Integer> tree = newTree();
        tree.insert(2);
        tree.insert(1);

        Iterator<Integer> it = tree.iterator();
        for (int i = 0; i < 5; i++) assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertFalse(it.hasNext());
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /** Goes through for-each on purpose, so every contents check also tests the iterator. */
    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> out = new ArrayList<>();
        for (T t : iterable) out.add(t);
        return out;
    }

    /** {3=1, 5=3} -> [3, 5, 5, 5] */
    private static List<Integer> expand(TreeMap<Integer, Integer> counts) {
        List<Integer> out = new ArrayList<>();
        counts.forEach((value, count) -> {
            for (int i = 0; i < count; i++) out.add(value);
        });
        return out;
    }

}