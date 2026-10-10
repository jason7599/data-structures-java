package algorithms.sorting;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class BogoSort implements Sort {

    private static final int MAX_TRIES_BEFORE_POOPING_PANTS = 10_000;

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        for (int tries = 0; tries < MAX_TRIES_BEFORE_POOPING_PANTS; tries++) {

            boolean sorted = true;
            for (int i = 0; i < arr.length - 1; i++) {
                if (comparator.compare(arr[i], arr[i + 1]) > 0) {
                    sorted = false;
                    break;
                }
            }

            if (sorted) return;

            fisherYates(arr);
        }

        throw new RuntimeException("Pants pooed");
    }

    @Override
    public boolean isStable() {
        return false;
    }

    private <T> void fisherYates(T[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            Sort.swap(arr, i, ThreadLocalRandom.current().nextInt(i, arr.length));
        }
    }
}
