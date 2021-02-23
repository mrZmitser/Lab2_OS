import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NonBlockingHashMapTest {
    @Test
    void containsKey() {
        //todo
    }

    @Test
    void get() {
        //todo
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
            .forEach(k -> {
                map.put("i=" + k, k);
            });
        return map;
    }

    @Test
    void remove() {
        //todo
    }
}