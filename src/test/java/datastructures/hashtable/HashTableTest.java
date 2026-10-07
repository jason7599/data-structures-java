package datastructures.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class HashTableTest {

    protected abstract <K, V> HashTable<K, V> newTable();

    HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = newTable();
    }

    // ---------------------------------------------------------------
    // Test keys
    // ---------------------------------------------------------------

    /** Deliberately terrible hash function so every key collides. */
    record BadHashKey(int id) {
        @Override
        public int hashCode() {
            return 1;
        }
    }

    record NegativeHashKey(int id) {
        @Override
        public int hashCode() {
            return -Math.abs(id);
        }
    }

    /** Builds a table holding a, b, c, all with the same hash, inserted in that order. */
    private HashTable<BadHashKey, String> collidingTable(BadHashKey a, BadHashKey b, BadHashKey c) {
        HashTable<BadHashKey, String> t = newTable();
        t.put(a, "A");
        t.put(b, "B");
        t.put(c, "C");
        return t;
    }

    // ---------------------------------------------------------------
    // Basic operations
    // ---------------------------------------------------------------

    @Test
    void putAndGet() {
        table.put("a", 1);
        table.put("b", 2);

        assertEquals(1, table.get("a"));
        assertEquals(2, table.get("b"));
        assertEquals(2, table.size());
    }

    @Test
    void getMissingKeyReturnsNull() {
        assertNull(table.get("missing"));
    }

    @Test
    void putExistingKeyReplacesValueWithoutIncreasingSize() {
        table.put("a", 1);
        table.put("a", 99);

        assertEquals(99, table.get("a"));
        assertEquals(1, table.size());
    }

    @Test
    void containsKeyDistinguishesNullValueFromMissingKey() {
        table.put("a", null);

        assertNull(table.get("a"));
        assertTrue(table.containsKey("a"));

        assertNull(table.get("missing"));
        assertFalse(table.containsKey("missing"));
    }

    @Test
    void removeExistingKey() {
        table.put("a", 1);

        assertEquals(1, table.remove("a"));
        assertNull(table.get("a"));
        assertFalse(table.containsKey("a"));
        assertEquals(0, table.size());
    }

    @Test
    void removeMissingKeyDoesNothing() {
        table.put("a", 1);

        assertNull(table.remove("missing"));
        assertEquals(1, table.size());
        assertEquals(1, table.get("a"));
    }

    @Test
    void clearRemovesEverything() {
        table.put("a", 1);
        table.put("b", 2);

        table.clear();

        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
        assertNull(table.get("a"));
        assertNull(table.get("b"));
    }

    // ---------------------------------------------------------------
    // Resizing
    // ---------------------------------------------------------------

    @Test
    void preservesEntriesAfterResize() {
        HashTable<Integer, String> t = newTable();

        for (int i = 0; i < 100; i++) {
            t.put(i, "value-" + i);
        }

        assertEquals(100, t.size());

        for (int i = 0; i < 100; i++) {
            assertEquals("value-" + i, t.get(i));
        }
    }

    // ---------------------------------------------------------------
    // Collisions
    // ---------------------------------------------------------------

    @Test
    void handlesCollisions() {
        BadHashKey a = new BadHashKey(1);
        BadHashKey b = new BadHashKey(2);
        BadHashKey c = new BadHashKey(3);

        HashTable<BadHashKey, String> t = collidingTable(a, b, c);

        assertEquals("A", t.get(a));
        assertEquals("B", t.get(b));
        assertEquals("C", t.get(c));
        assertEquals(3, t.size());
    }

    @Test
    void removesFirstInsertedOfCollidingKeys() {
        BadHashKey a = new BadHashKey(1);
        BadHashKey b = new BadHashKey(2);
        BadHashKey c = new BadHashKey(3);

        HashTable<BadHashKey, String> t = collidingTable(a, b, c);

        assertEquals("A", t.remove(a));

        assertNull(t.get(a));
        assertEquals("B", t.get(b));
        assertEquals("C", t.get(c));
        assertEquals(2, t.size());
    }

    @Test
    void removesMiddleInsertedOfCollidingKeys() {
        BadHashKey a = new BadHashKey(1);
        BadHashKey b = new BadHashKey(2);
        BadHashKey c = new BadHashKey(3);

        HashTable<BadHashKey, String> t = collidingTable(a, b, c);

        assertEquals("B", t.remove(b));

        assertEquals("A", t.get(a));
        assertNull(t.get(b));
        assertEquals("C", t.get(c));
        assertEquals(2, t.size());
    }

    @Test
    void removesLastInsertedOfCollidingKeys() {
        BadHashKey a = new BadHashKey(1);
        BadHashKey b = new BadHashKey(2);
        BadHashKey c = new BadHashKey(3);

        HashTable<BadHashKey, String> t = collidingTable(a, b, c);

        assertEquals("C", t.remove(c));

        assertEquals("A", t.get(a));
        assertEquals("B", t.get(b));
        assertNull(t.get(c));
        assertEquals(2, t.size());
    }

    /**
     * After removing an earlier colliding key, re-putting a later one must update it in place,
     * not insert a duplicate into the freed slot. (Classic linear-probing tombstone bug.)
     */
    @Test
    void putAfterRemovingCollidingKeyDoesNotDuplicate() {
        BadHashKey a = new BadHashKey(1);
        BadHashKey b = new BadHashKey(2);
        BadHashKey c = new BadHashKey(3);

        HashTable<BadHashKey, String> t = collidingTable(a, b, c);

        t.remove(a);
        t.put(c, "C2");

        assertEquals("C2", t.get(c));
        assertEquals(2, t.size());

        t.remove(c);
        assertNull(t.get(c));
        assertFalse(t.containsKey(c));
        assertEquals(1, t.size());
    }

    @Test
    void handlesNegativeHashCodes() {
        HashTable<NegativeHashKey, String> t = newTable();

        NegativeHashKey key = new NegativeHashKey(42);
        t.put(key, "hello");

        assertEquals("hello", t.get(key));
        assertTrue(t.containsKey(key));
        assertEquals(1, t.size());
    }

}
