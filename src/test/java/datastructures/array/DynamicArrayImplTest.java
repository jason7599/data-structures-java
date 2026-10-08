package datastructures.array;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayImplTest {

    private static final int MIN_CAPACITY = 16;

    @Test
    void matchesArrayListUnderRandomOps() {
        for (long seed = 0; seed < 20; seed++) {
            stressWithSeed(seed);
        }
    }

    private void stressWithSeed(long seed) {
        Random rng = new Random(seed);
        DynamicArrayImpl<Integer> mine = new DynamicArrayImpl<>();
        List<Integer> ref = new ArrayList<>();

        // At 25% adds roughly balance removes, so the array keeps shrinking back
        // toward empty. Higher rates grow it through several resizes.
        int addPct = 25 + rng.nextInt(26);

        for (int step = 0; step < 5_000; step++) {
            String ctx = "seed=" + seed + " step=" + step;
            int x = rng.nextInt(1_000);

            if (rng.nextInt(500) == 0) {
                mine.clear();
                ref.clear();
            } else {
                int roll = rng.nextInt(100);

                if (roll < addPct) {
                    switch (rng.nextInt(3)) {
                        case 0 -> { mine.pushBack(x);  ref.add(x); }
                        case 1 -> { mine.pushFront(x); ref.addFirst(x); }
                        default -> {
                            // range is [-1, size + 1], so both invalid edges get hit
                            int idx = rng.nextInt(ref.size() + 3) - 1;
                            assertSameOutcome(
                                    () -> { ref.add(idx, x); return null; },
                                    () -> { mine.add(idx, x); return null; },
                                    ctx + " add(" + idx + ")");
                        }
                    }
                } else if (roll < addPct + 25) {
                    switch (rng.nextInt(3)) {
                        case 0 -> {
                            if (ref.isEmpty()) assertThrowsEmpty(mine::popBack, ctx + " popBack on empty");
                            else assertEquals(ref.removeLast(), mine.popBack(), ctx + " popBack");
                        }
                        case 1 -> {
                            if (ref.isEmpty()) assertThrowsEmpty(mine::popFront, ctx + " popFront on empty");
                            else assertEquals(ref.removeFirst(), mine.popFront(), ctx + " popFront");
                        }
                        default -> {
                            int idx = rng.nextInt(ref.size() + 2) - 1;  // [-1, size]
                            assertSameOutcome(() -> ref.remove(idx), () -> mine.remove(idx),
                                    ctx + " remove(" + idx + ")");
                        }
                    }
                } else {
                    int idx = rng.nextInt(ref.size() + 2) - 1;  // [-1, size]
                    switch (rng.nextInt(4)) {
                        case 0 -> assertSameOutcome(() -> ref.get(idx), () -> mine.get(idx),
                                ctx + " get(" + idx + ")");
                        case 1 -> assertSameOutcome(() -> ref.set(idx, x), () -> mine.set(idx, x),
                                ctx + " set(" + idx + ")");
                        case 2 -> {
                            if (ref.isEmpty()) assertThrowsEmpty(mine::front, ctx + " front on empty");
                            else assertEquals(ref.getFirst(), mine.front(), ctx + " front");
                        }
                        default -> {
                            if (ref.isEmpty()) assertThrowsEmpty(mine::last, ctx + " last on empty");
                            else assertEquals(ref.getLast(), mine.last(), ctx + " last");
                        }
                    }
                }
            }

            assertEquals(ref.size(), mine.size(), ctx + " size");
            assertEquals(ref.isEmpty(), mine.isEmpty(), ctx + " isEmpty");
            assertEquals(ref, toList(mine), ctx + " contents");
            assertCapacityInvariants(mine, ctx);
        }
    }

    // ---------------------------------------------------------------
    // Resizing policy
    // ---------------------------------------------------------------

    @Test
    void growsThenShrinksBackToMinimum() {
        DynamicArrayImpl<Integer> arr = new DynamicArrayImpl<>();
        assertEquals(MIN_CAPACITY, arr.capacity());

        int peak = 0;
        for (int i = 0; i < 10_000; i++) {
            arr.pushBack(i);
            assertCapacityInvariants(arr, "push " + i);
            peak = Math.max(peak, arr.capacity());
        }
        assertTrue(peak >= 10_000, "never grew enough: " + peak);

        for (int i = 0; i < 10_000; i++) {
            assertEquals(i, arr.popFront());
            assertCapacityInvariants(arr, "pop " + i);
        }
        assertTrue(arr.isEmpty());
        assertEquals(MIN_CAPACITY, arr.capacity(), "should shrink all the way back down");
    }

    @Test
    void doesNotThrashAtTheBoundary() {
        DynamicArrayImpl<Integer> arr = new DynamicArrayImpl<>();
        for (int i = 0; i < 1_000; i++) arr.pushBack(i);

        // fill to exactly full, so the next push triggers a grow
        while (arr.size() < arr.capacity()) arr.pushBack(0);
        int full = arr.capacity();

        arr.pushBack(0);
        int grown = arr.capacity();
        assertTrue(grown > full);

        // bouncing across the growth boundary must not resize every time
        for (int i = 0; i < 1_000; i++) {
            arr.popBack();
            arr.pushBack(0);
            assertEquals(grown, arr.capacity(), "resized while bouncing at step " + i);
        }
    }

    @Test
    void clearResetsCapacity() {
        DynamicArrayImpl<Integer> arr = new DynamicArrayImpl<>();
        for (int i = 0; i < 1_000; i++) arr.pushBack(i);

        arr.clear();
        assertEquals(0, arr.size());
        assertEquals(MIN_CAPACITY, arr.capacity());

        arr.pushBack(42);  // still usable afterward
        assertEquals(42, arr.get(0));
    }

    // ---------------------------------------------------------------
    // Iterator contract
    // ---------------------------------------------------------------

    @Test
    void emptyArrayIteratesNothing() {
        for (Integer ignored : new DynamicArrayImpl<Integer>()) {
            fail("should not iterate over an empty array");
        }
    }

    @Test
    void nextThrowsWhenExhausted() {
        DynamicArrayImpl<Integer> arr = new DynamicArrayImpl<>();
        arr.pushBack(1);

        Iterator<Integer> it = arr.iterator();
        assertEquals(1, it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void iteratorStopsAtSizeNotCapacity() {
        DynamicArrayImpl<Integer> arr = new DynamicArrayImpl<>();
        arr.pushBack(1);
        arr.pushBack(2);

        // capacity is 16, but only 2 real elements
        assertEquals(List.of(1, 2), toList(arr));
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static void assertCapacityInvariants(DynamicArrayImpl<?> arr, String ctx) {
        int cap = arr.capacity();
        assertTrue(cap >= arr.size(), ctx + ": capacity " + cap + " < size " + arr.size());
        assertTrue(cap >= MIN_CAPACITY, ctx + ": capacity " + cap + " below minimum");
        if (cap > MIN_CAPACITY) {
            assertTrue(arr.size() >= cap / 4,
                    ctx + ": size " + arr.size() + " under a quarter of capacity " + cap + ", should have shrunk");
        }
    }

    /**
     * front/last/pop on empty: accept either IndexOutOfBounds (what get(0) throws)
     * or NoSuchElement (what LinkedList/ArrayDeque throw), whichever convention you pick.
     */
    private static void assertThrowsEmpty(Supplier<?> op, String ctx) {
        Outcome o = run(op);
        assertTrue(o.error() == IndexOutOfBoundsException.class || o.error() == NoSuchElementException.class,
                ctx + ": expected IndexOutOfBounds or NoSuchElement, got " + o);
    }

    /** Goes through for-each on purpose, so every contents check also tests the iterator. */
    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> out = new ArrayList<>();
        for (T t : iterable) out.add(t);
        return out;
    }

    /** Either a returned value or the class of the exception thrown. */
    private record Outcome(Object value, Class<? extends Throwable> error) {}

    private static Outcome run(Supplier<?> op) {
        try {
            return new Outcome(op.get(), null);
        } catch (RuntimeException e) {
            return new Outcome(null, e.getClass());
        }
    }

    /** Both sides must return the same value, or throw the same exception type. */
    private static void assertSameOutcome(Supplier<?> expected, Supplier<?> actual, String ctx) {
        assertEquals(run(expected), run(actual), ctx);
    }
}