import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class TestThread extends Thread {
    Random random;
    int size = 1000;
    private NonBlockingHashMap<Integer, Integer> map;

    public NonBlockingHashMap<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(NonBlockingHashMap<Integer, Integer> map) {
        this.map = map;
    }

    public TestThread() {
        map = new NonBlockingHashMap<>();
        random = new Random();
    }

    @Override
    public void run() {
        for(int i = 0; i < size / 2; i++)
        {
            map.put(random.nextInt() % 200, random.nextInt());
            map.remove(random.nextInt() % 200, random.nextInt());
        }
    }
}

class NonBlockingHashMapTest {
    @Test
    void containsKey() {
        NonBlockingHashMap<Integer, Integer> map = new NonBlockingHashMap<>();
        for(int i = 0; i < 100; i += 16)
            map.put(i, i + 1);
        map.remove(64);
        assertFalse(map.containsKey(64));
    }

    @Test
    void get() {
        NonBlockingHashMap<Integer, Integer> map = new NonBlockingHashMap<>();
        for(int i = 0; i < 100; i += 16)
            map.put(i, i + 1);
        assertEquals(65, map.get(64));
    }

    @Test
    void put() {
        var testMap = initMap();
        for (int i = 0; i < 10000; ++i) {
            assertEquals(i, testMap.get("i=" + i));
        }
    }

    private NonBlockingHashMap<String, Integer> initMap() {
        var map = new NonBlockingHashMap<String, Integer>();
        var ints = IntStream.range(0, 100001).boxed().collect(Collectors.toList());

        ints
            .parallelStream()
            .forEach(k -> map.put("i=" + k, k));
        return map;
    }

    @Test
    void crushTest(){
        NonBlockingHashMap map = new NonBlockingHashMap();
        TestThread thread1 = new TestThread();
        TestThread thread2 = new TestThread();
        thread1.setMap(map);
        thread2.setMap(map);
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
        var ints = IntStream.range(0, 100001).boxed().collect(Collectors.toList());
        ints
            .parallelStream()
            .forEach(k -> testMap.remove("i=" + k));
        for (int i = 0; i < 10000; ++i) {
            assertNull(testMap.get("i="+i));
        }
    }
}