package datastructures.bst;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

abstract class BinarySearchTreeTest {

    protected abstract <T extends Comparable<? super T>> BinarySearchTree<T> newTree();

    BinarySearchTree<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = newTree();
    }

    private void insertAll(int... values) {
        for (int v : values) tree.insert(v);
    }

    // --------------------------------------------------------------- insert

    @Test
    void insertSingle() {
        tree.insert(5);
        assertFalse(tree.isEmpty());
        assertEquals(1, tree.size());
        assertTrue(tree.contains(5));
        assertEquals(1, tree.count(5));
    }

    @Test
    void insertMultiple() {
        insertAll(10, 20, 5, 23);

        assertFalse(tree.isEmpty());
        assertEquals(4, tree.size());
        assertTrue(tree.contains(10));
        assertTrue(tree.contains(23));
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(20));
        assertEquals(1, tree.count(20));
    }

    @Test
    void containsAbsentValues() {
        insertAll(50, 30, 70);
        assertFalse(tree.contains(10));
        assertFalse(tree.contains(40));
        assertFalse(tree.contains(90));
    }

    @Test
    void duplicatesAreCounted() {
        insertAll(5, 3, 5, 7, 5, 3);
        assertEquals(3, tree.count(5));
        assertEquals(2, tree.count(3));
        assertEquals(1, tree.count(7));
        assertEquals(0, tree.count(4));
        assertEquals(6, tree.size());
    }

    @Test
    void sortedInsertionDoesNotBreak() {
        // Worst case for the unbalanced tree: a linked list.
        for (int i = 0; i < 2000; i++) tree.insert(i);
        assertEquals(2000, tree.size());
        assertEquals(0, tree.min());
        assertEquals(1999, tree.max());
        assertTrue(tree.contains(1234));
    }

    // --------------------------------------------------------------- remove

    @Test
    void removeLeaf() {
        insertAll(50, 30, 70);
        assertTrue(tree.remove(30));
        assertFalse(tree.contains(30));
        assertEquals(2, tree.size());
    }

    @Test
    void removeNodeWithOnlyLeftChild() {
        insertAll(50, 30, 20);
        assertTrue(tree.remove(30));
        assertEquals(2, tree.size());
        assertTrue(tree.contains(20));
    }

    @Test
    void removeNodeWithOnlyRightChild() {
        insertAll(50, 30, 40);
        assertTrue(tree.remove(30));
        assertEquals(2, tree.size());
        assertTrue(tree.contains(40));
    }

    @Test
    void removeNodeWithTwoChildren() {
        insertAll(50, 30, 70, 20, 40, 60, 80);
        assertTrue(tree.remove(30));
        assertTrue(tree.contains(20));
        assertTrue(tree.contains(40));
        assertEquals(6, tree.size());
    }

    @Test
    void removeTwoChildrenWhereSuccessorIsRightChild() {
        insertAll(50, 30, 70, 80);
        assertTrue(tree.remove(50));
    }

    @Test
    void removeTwoChildrenWhereSuccessorIsDeep() {
        insertAll(50, 30, 70, 60, 80, 55, 65, 57);
        assertTrue(tree.remove(50));
    }

    @Test
    void removeRootWithOneChild() {
        // Catches forgetting to reassign root.
        insertAll(10, 20);
        assertTrue(tree.remove(10));
        assertFalse(tree.contains(10));
        assertEquals(20, tree.min());
    }

    @Test
    void removeOnlyElement() {
        tree.insert(1);
        assertTrue(tree.remove(1));
        assertTrue(tree.isEmpty());
    }

    @Test
    void removeEverythingOneByOne() {
        int[] values = {50, 30, 70, 20, 40, 60, 80, 35, 65};
        insertAll(values);
        for (int i = 0; i < values.length; i++) {
            assertTrue(tree.remove(values[i]), "removing " + values[i]);
            assertEquals(values.length - i - 1, tree.size());
        }
        assertTrue(tree.isEmpty());
    }

    @Test
    void removeAbsentValue() {
        insertAll(50, 30, 70);
        assertFalse(tree.remove(40));
        assertEquals(3, tree.size());
    }

    @Test
    void removeOneCopyOfDuplicate() {
        insertAll(5, 5, 5);
        assertTrue(tree.remove(5));
        assertEquals(2, tree.count(5));
        assertEquals(2, tree.size());
        assertTrue(tree.contains(5));
    }

    @Test
    void removeTwoChildrenWithDuplicateSuccessorKeepsSize() {
        // The successor (60) has count 3. Its copies move up; they must not
        // be subtracted from size.
        insertAll(50, 30, 70, 60, 60, 60, 80);
        assertTrue(tree.remove(50));
        assertEquals(6, tree.size());
        assertEquals(3, tree.count(60));
    }

    @Test
    void removeTwoChildrenNodeThatHasDuplicates() {
        insertAll(50, 50, 30, 70);
        assertTrue(tree.remove(50));
        assertEquals(1, tree.count(50));
        assertEquals(3, tree.size());
    }

    // ------------------------------------------------------------ removeAll

    @Test
    void removeAllReturnsCount() {
        insertAll(5, 3, 5, 7, 5);
        assertEquals(3, tree.removeAll(5));
        assertFalse(tree.contains(5));
        assertEquals(0, tree.count(5));
        assertEquals(2, tree.size());
    }

    @Test
    void removeAllAbsentReturnsZero() {
        insertAll(1, 2, 3);
        assertEquals(0, tree.removeAll(4));
        assertEquals(3, tree.size());
    }

    @Test
    void removeAllOnTwoChildrenNodeWithDuplicateSuccessor() {
        insertAll(50, 50, 30, 70, 60, 60);
        assertEquals(2, tree.removeAll(50));
        assertEquals(4, tree.size());
    }

    // ---------------------------------------------------------------- clear

    @Test
    void clearEmptiesTree() {
        insertAll(5, 3, 7, 3);
        tree.clear();
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertFalse(tree.contains(5));
    }

    @Test
    void usableAfterClear() {
        insertAll(5, 3, 7);
        tree.clear();
        tree.insert(42);
        assertEquals(1, tree.size());
    }

    // -------------------------------------------------------------- min/max

    @Test
    void minMax() {
        insertAll(50, 30, 70, 20, 80);
        assertEquals(20, tree.min());
        assertEquals(80, tree.max());
    }

    @Test
    void minMaxAfterRemoving() {
        insertAll(50, 30, 70, 20, 80);
        tree.remove(20);
        tree.remove(80);
        assertEquals(30, tree.min());
        assertEquals(70, tree.max());
    }

    // ------------------------------------------------- floor/ceiling/etc.

    @Test
    void floor() {
        insertAll(30, 10, 50, 20, 40);
        assertEquals(20, tree.floor(25));
        assertEquals(20, tree.floor(20));   // inclusive
        assertEquals(50, tree.floor(99));
        assertNull(tree.floor(5));
    }

    @Test
    void ceiling() {
        insertAll(30, 10, 50, 20, 40);
        assertEquals(30, tree.ceiling(25));
        assertEquals(30, tree.ceiling(30)); // inclusive
        assertEquals(10, tree.ceiling(-5));
        assertNull(tree.ceiling(55));
    }

    @Test
    void lower() {
        insertAll(30, 10, 50, 20, 40);
        assertEquals(20, tree.lower(25));
        assertEquals(10, tree.lower(20));   // strict
        assertNull(tree.lower(10));
        assertEquals(50, tree.lower(99));
    }

    @Test
    void higher() {
        insertAll(30, 10, 50, 20, 40);
        assertEquals(30, tree.higher(25));
        assertEquals(30, tree.higher(20));  // strict
        assertNull(tree.higher(50));
        assertEquals(10, tree.higher(-5));
    }

    // -------------------------------------------------- randomized model test

    /**
     * Runs random operations against both the tree and a TreeMap of counts,
     * checking they agree after every step. Fixed seed, so failures reproduce.
     */
    @Test
    void randomOperationsMatchModel() {
        Random rnd = new Random(42);
        TreeMap<Integer, Integer> model = new TreeMap<>();
        int modelSize = 0;

        for (int step = 0; step < 10_000; step++) {
            int x = rnd.nextInt(40);
            int op = rnd.nextInt(100);
            String desc;

            if (op < 50) {
                desc = "insert(" + x + ")";
                tree.insert(x);
                model.merge(x, 1, Integer::sum);
                modelSize++;
            } else if (op < 80) {
                desc = "remove(" + x + ")";
                boolean expected = model.containsKey(x);
                assertEquals(expected, tree.remove(x), at(step, desc));
                if (expected) {
                    model.computeIfPresent(x, (k, c) -> c == 1 ? null : c - 1);
                    modelSize--;
                }
            } else if (op < 99) {
                desc = "removeAll(" + x + ")";
                int expected = model.getOrDefault(x, 0);
                assertEquals(expected, tree.removeAll(x), at(step, desc));
                model.remove(x);
                modelSize -= expected;
            } else {
                desc = "clear()";
                tree.clear();
                model.clear();
                modelSize = 0;
            }

            String where = at(step, desc);
            assertEquals(modelSize, tree.size(), where);
            assertEquals(modelSize == 0, tree.isEmpty(), where);
            assertEquals(model.getOrDefault(x, 0), tree.count(x), where);
            assertEquals(model.containsKey(x), tree.contains(x), where);

            int probe = rnd.nextInt(50) - 5;
            assertEquals(model.floorKey(probe), tree.floor(probe), where + " floor(" + probe + ")");
            assertEquals(model.ceilingKey(probe), tree.ceiling(probe), where + " ceiling(" + probe + ")");
            assertEquals(model.lowerKey(probe), tree.lower(probe), where + " lower(" + probe + ")");
            assertEquals(model.higherKey(probe), tree.higher(probe), where + " higher(" + probe + ")");

            if (!model.isEmpty()) {
                assertEquals(model.firstKey(), tree.min(), where);
                assertEquals(model.lastKey(), tree.max(), where);
            }
        }
    }

    private static String at(int step, String desc) {
        return "step " + step + ": " + desc;
    }

    private static List<Integer> expand(Map<Integer, Integer> counts) {
        List<Integer> out = new ArrayList<>();
        counts.forEach((k, c) -> { for (int i = 0; i < c; i++) out.add(k); });
        return out;
    }
}