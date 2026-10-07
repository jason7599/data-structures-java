package datastructures.linkedlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {
    LinkedList<Integer> list = new LinkedListImpl<>();

    @BeforeEach
    void setUp() {
        list.clear();
    }

    @Test
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void pushFirstAddsElementsToFront() {
        list.pushFirst(1);
        list.pushFirst(2);
        list.pushFirst(3);

        assertEquals(3, list.size());
        assertEquals(3, list.getFirst());
        assertEquals(1, list.getLast());

        assertEquals(3, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(1, list.get(2));
    }

    @Test
    void pushLastAddsElementsToBack() {
        list.pushLast(1);
        list.pushLast(2);
        list.pushLast(3);

        assertEquals(3, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(3, list.getLast());

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    void addFirstAndPushLastWorkTogether() {
        list.pushFirst(2);
        list.pushFirst(1);
        list.pushLast(3);
        list.pushLast(4);

        assertEquals(4, list.size());

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
    }

    @Test
    void popFirstRemovesAndReturnsFirstElement() {
        list.pushLast(1);
        list.pushLast(2);
        list.pushLast(3);

        assertEquals(1, list.popFirst());

        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
        assertEquals(3, list.getLast());
    }

    @Test
    void popLastRemovesAndReturnsLastElement() {
        list.pushLast(1);
        list.pushLast(2);
        list.pushLast(3);

        assertEquals(3, list.popLast());

        assertEquals(2, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(2, list.getLast());
    }

    @Test
    void poppingOnlyElementLeavesListEmpty() {
        list.pushFirst(42);

        assertEquals(42, list.popFirst());

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void popLastOnSingleElementLeavesListEmpty() {
        list.pushFirst(42);

        assertEquals(42, list.popLast());

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void getFirstThrowsWhenEmpty() {
        assertThrows(
                NoSuchElementException.class,
                () -> list.getFirst()
        );
    }

    @Test
    void getLastThrowsWhenEmpty() {
        assertThrows(
                NoSuchElementException.class,
                () -> list.getLast()
        );
    }

    @Test
    void popFirstThrowsWhenEmpty() {
        assertThrows(
                NoSuchElementException.class,
                () -> list.popFirst()
        );
    }

    @Test
    void popLastThrowsWhenEmpty() {
        assertThrows(
                NoSuchElementException.class,
                () -> list.popLast()
        );
    }

    @Test
    void getThrowsForNegativeIndex() {
        list.pushLast(1);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> list.get(-1)
        );
    }

    @Test
    void getThrowsForIndexEqualToSize() {
        list.pushLast(1);
        list.pushLast(2);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> list.get(2)
        );
    }

    @Test
    void getWorksFromBothEnds() {
        for (int i = 0; i < 10; i++) {
            list.pushLast(i);
        }

        assertEquals(0, list.get(0));
        assertEquals(1, list.get(1));
        assertEquals(8, list.get(8));
        assertEquals(9, list.get(9));
    }

    @Test
    void clearEmptiesList() {
        list.pushLast(1);
        list.pushLast(2);
        list.pushLast(3);

        list.clear();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void clearOnEmptyListDoesNotThrow() {
        assertDoesNotThrow(() -> list.clear());

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void listCanBeReusedAfterClear() {
        list.pushLast(1);
        list.pushLast(2);

        list.clear();

        list.pushFirst(10);
        list.pushLast(20);

        assertEquals(2, list.size());
        assertEquals(10, list.getFirst());
        assertEquals(20, list.getLast());
    }
}