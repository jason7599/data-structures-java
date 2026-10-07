package datastructures.hashtable;

class ChainedHashTableTest extends HashTableTest{
    @Override
    protected <K, V> HashTable<K, V> newTable() {
        return new ChainedHashTable<>();
    }
}
