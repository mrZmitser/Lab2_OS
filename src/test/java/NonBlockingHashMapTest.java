import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NonBlockingHashMapTest {
    @Test
        //to be deleted in final version
    void devTest() throws InterruptedException {
        NonBlockingHashMap<Integer, Integer> my = new NonBlockingHashMap<>();
        ConcurrentHashMap<Integer, Integer> chm = new ConcurrentHashMap<>();
        var shm = Collections.synchronizedMap(new HashMap<Integer, Integer>());
        HashMap<Integer, Integer> hm = new HashMap<>();
        var r = IntStream.range(1, 10001).boxed().collect(Collectors.toList());
        r.parallelStream().forEach(k -> my.put(0, my.get(0) == null ? 1 : my.get(0) + 1));
        r.parallelStream().forEach(k -> hm.put(0, hm.get(0) == null ? 1 : hm.get(0) + 1));
        r.parallelStream().forEach(k -> shm.put(0, shm.get(0) == null ? 1 : shm.get(0) + 1));
        r.parallelStream().forEach(k -> chm.put(0, chm.get(0) == null ? 1 : chm.get(0) + 1));


        System.out.print(shm.get(0));
        System.out.print('\t');
        System.out.print(chm.get(0));
        System.out.print('\t');
        System.out.print(hm.get(0));
        System.out.print('\t');
        System.out.println(my.get(0));

    }

    @Test
    void containsKey() {
    }

    @Test
    void get() {
    }

    @Test
    void put() {
    }

    @Test
    void remove() {
    }
}