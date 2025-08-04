package com.example.demo.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RenewableConcurrentHashMapTest {

    private static class TestListener<K, V> implements RenewableConcurrentHashMap.RenewalListener<K, V> {

        private final Map<K, V> renewedMap = new ConcurrentHashMap<>();
        private AtomicInteger renewedCount = new AtomicInteger(0);

        @Override
        public void onRenew(K key, V value) {
            renewedMap.put(key, value);
            renewedCount.incrementAndGet();
        }

        public Map<K, V> getRenewedMap() {
            return renewedMap;
        }

        public int getRenewedCount() {
            return renewedCount.get();
        }
    }

    @Test
    void testInsertAndRetrieveValue() {
        TestListener<String, Integer> listener = new TestListener<>();
        RenewableConcurrentHashMap<String, Integer> map = new RenewableConcurrentHashMap<>(listener);
        map.put("foo", 42);
        assertEquals(42, map.get("foo"));
    }

    @Test
    void testRemoveValue() {
        TestListener<String, Integer> listener = new TestListener<>();
        RenewableConcurrentHashMap<String, Integer> map = new RenewableConcurrentHashMap<>(listener);
        map.put("bar", 99);
        assertEquals(99, map.remove("bar"));
        assertNull(map.get("bar"));
    }


    @Test
    void testRetrieveNonExistentKey() {
        TestListener<String, Integer> listener = new TestListener<>();
        RenewableConcurrentHashMap<String, Integer> map = new RenewableConcurrentHashMap<>(listener);
        assertNull(map.get("not-present"));
    }


    @Test
    void testRenewNonExistentEntry() {
        TestListener<String, Integer> listener = new TestListener<>();
        RenewableConcurrentHashMap<String, Integer> map = new RenewableConcurrentHashMap<>(listener);
        // No entries in map
        map.renewEntries();
        assertTrue(listener.getRenewedMap().isEmpty());
    }

    @Test
    void testRenewEntriesInvokesListenerForAllEntries() {
        TestListener<String, Integer> listener = new TestListener<>();
        RenewableConcurrentHashMap<String, Integer> map = new RenewableConcurrentHashMap<>(listener);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.renewEntries();
        assertTrue(listener.getRenewedMap().keySet().containsAll(Arrays.asList("a", "b", "c")));
        assertEquals(3, listener.getRenewedMap().size());
    }

    @Test
    void testListenerReceivesCorrectKeyValuePairs() {
        TestListener<String, String> listener = new TestListener<>();
        RenewableConcurrentHashMap<String, String> map = new RenewableConcurrentHashMap<>(listener);
        map.put("x", "alpha");
        map.put("y", "beta");
        map.renewEntries();
        Map<String, String> renewed = listener.getRenewedMap();
        assertEquals("alpha", renewed.get("x"));
        assertEquals("beta", renewed.get("y"));
    }

    @Test
    void testStandardConcurrentHashMapBehavior() {
        TestListener<Integer, String> listener = new TestListener<>();
        RenewableConcurrentHashMap<Integer, String> map = new RenewableConcurrentHashMap<>(listener);
        map.put(1, "one");
        map.put(2, "two");
        assertEquals("one", map.get(1));
        assertEquals("two", map.get(2));
        assertEquals("one", map.remove(1));
        assertNull(map.get(1));
        assertEquals(1, map.size());
    }

    @Test
    void testRenewEntriesOnEmptyMap() {
        TestListener<String, String> listener = new TestListener<>();
        RenewableConcurrentHashMap<String, String> map = new RenewableConcurrentHashMap<>(listener);
        map.renewEntries();
        assertTrue(listener.getRenewedMap().isEmpty());
    }


    /* Below is beyond the scope of 4 hours */

    @Test
    void testRenewEntriesWithConcurrentModification() throws InterruptedException {
        TestListener<Integer, Integer> listener = new TestListener<>();
        RenewableConcurrentHashMap<Integer, Integer> map = new RenewableConcurrentHashMap<>(listener);

        for (int i = 0; i < 100; i++) {
            map.put(i, i * 10);
        }

        AtomicBoolean running = new AtomicBoolean(true);
        Thread modifier = getThread(running, map);
        //Give some time for thread to do some work
        Thread.sleep(50);
        try {
            map.renewEntries();
        } finally {
            running.set(false);
            modifier.join();
        }

        // Assert that some entries were renewed.
        // thread could have added more entries after renewEntries finished.
        int initialSize = 100;
        int finalRenewedCount = listener.getRenewedCount();
        System.out.println("Final Renewed Count: " + finalRenewedCount);

        assertTrue(finalRenewedCount >= initialSize);
    }

    private static Thread getThread(AtomicBoolean running, RenewableConcurrentHashMap<Integer, Integer> map) {
        AtomicInteger counter = new AtomicInteger(100);
        Thread modifier = new Thread(() -> {
            while (running.get()) {
                int currentCount = counter.getAndIncrement();
                map.put(currentCount, currentCount * 10);
                map.remove(currentCount - 50);
                try {
                    // Introduce a small delay, an attempt to cause race condition
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        modifier.start();
        return modifier;
    }

}
