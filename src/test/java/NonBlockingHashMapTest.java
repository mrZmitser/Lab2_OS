import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class NonBlockingHashMapTest {
    @Test
    void containsKey() {
        NonBlockingHashMap<Integer, Integer> map = new NonBlockingHashMap<>();
        for (int i = 0; i < 100; i += 16)
            map.put(i, i + 1);
        map.remove(64);
        assertFalse(map.containsKey(64));
    }

    @Test
    void get() {
        NonBlockingHashMap<Integer, Integer> map = new NonBlockingHashMap<>();
        for (int i = 0; i < 100; i += 16)
            map.put(i, i + 1);
        assertEquals(65, map.get(64));
    }

    @Test
    void put() {
        var testMap = initMap();
        for (int i = 0; i < 10001; ++i) {
            assertEquals(i, testMap.get("i=" + i));
        }
    }

    private NonBlockingHashMap<String, Integer> initMap() {
        var map = new NonBlockingHashMap<String, Integer>();
        var ints = IntStream.range(0, 10001).boxed().collect(Collectors.toList());

        ints
                .parallelStream()
                .forEach(k -> map.put("i=" + k, k));
        return map;
    }

    @Test
    void crushTest() {
        var map = new NonBlockingHashMap<Integer, Integer>();
        Random random = new Random();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                map.put(2*(random.nextInt() % 200), random.nextInt());
                map.remove(2*(random.nextInt() % 200) + 1);
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                map.put(2*(random.nextInt() % 200) + 1, random.nextInt());
                map.remove(2*(random.nextInt() % 200));
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join(0);
            thread2.join(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void remove() {
        var testMap = initMap();
        var ints = IntStream.range(0, 10001).boxed().collect(Collectors.toList());
        ints
                .parallelStream()
                .forEach(k -> testMap.remove("i=" + k));
        for (int i = 0; i < 10001; ++i) {
            assertNull(testMap.get("i=" + i));
        }
    }
}