package datastructures.hashtable;

class LinearProbingHashTableTest extends HashTableTest {
    @Override
    protected <K, V> HashTable<K, V> newTable() {
        return new LinearProbingHashTable<>();
    }
}
